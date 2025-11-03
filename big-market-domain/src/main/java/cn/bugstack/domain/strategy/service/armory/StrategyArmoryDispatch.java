package cn.bugstack.domain.strategy.service.armory;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 策略装配库(兵工厂)，负责初始化策略计算
 * @create 2023-12-23 10:02
 */
@Slf4j
@Service
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {

    @Resource
    private IStrategyRepository repository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public boolean assembleLotteryStrategyByActivityId(Long activityId) {
        Long strategyId = repository.queryStrategyIdByActivityId(activityId);
        return assembleLotteryStrategy(strategyId);
    }

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 1. 查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);

        // 2 缓存奖品库存【用于decr扣减库存使用】
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            Integer awardCount = strategyAward.getAwardCountSurplus();
            cacheStrategyAwardCount(strategyId, awardId, awardCount);
        }

        // 3.1 默认装配配置【全量抽奖概率】
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwardEntities);

        // 3.2 权重策略配置 - 适用于 rule_weight 权重规则配置【4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109】
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (null == ruleWeight) return true;

        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRule(strategyId, ruleWeight);
        // 业务异常，策略规则中 rule_weight 权重规则已适用但未配置
        if (null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }

        Map<String, List<Integer>> ruleWeightValueMap = strategyRuleEntity.getRuleWeightValues();
        for (String key : ruleWeightValueMap.keySet()) {
            List<Integer> ruleWeightValues = ruleWeightValueMap.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(entity -> !ruleWeightValues.contains(entity.getAwardId()));
            assembleLotteryStrategy(String.valueOf(strategyId).concat(Constants.UNDERLINE).concat(key), strategyAwardEntitiesClone);
        }

        return true;
    }

    /**
     * 计算公式；
     * 1. 找到范围内最小的概率值，比如 0.1、0.02、0.003，需要找到的值是 0.003
     * 2. 基于1找到的最小值，0.003 就可以计算出百分比、千分比的整数值。这里就是1000
     * 3. 那么「概率 * 1000」分别占比100个、20个、3个，总计是123个
     * 4. 后续的抽奖就用123作为随机数的范围值，生成的值100个都是0.1概率的奖品、20个是概率0.02的奖品、最后是3个是0.003的奖品。
     */
    private void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        // 1. 获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 2. 循环计算找到概率范围值
        BigDecimal rateRange = BigDecimal.valueOf(convert(minAwardRate.doubleValue()));

        // 3. 生成策略奖品概率查找表「这里指需要在list集合中，存放上对应的奖品占位即可，占位越多等于概率越高」
        List<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();
            // 计算出每个概率值需要存放到查找表的数量，循环填充
            for (int i = 0; i < rateRange.multiply(awardRate).intValue(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        // 4. 对存储的奖品进行乱序操作
        Collections.shuffle(strategyAwardSearchRateTables);

        // 5. 生成出Map集合，key值，对应的就是后续的概率值。通过概率来获得对应的奖品ID
        Map<Integer, Integer> shuffleStrategyAwardSearchRateTable = new LinkedHashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTables.get(i));
        }

        // 6. 存放到 Redis
        repository.storeStrategyAwardSearchRateTable(key, shuffleStrategyAwardSearchRateTable.size(), shuffleStrategyAwardSearchRateTable);
    }

    /**
     * 转换计算，只根据小数位来计算。如【0.01返回100】、【0.009返回1000】、【0.0018返回10000】
     */
    private double convertOld1(double min) {
        if (0 == min) return 1D;

        double current = min;
        double max = 1;
        while (current % 1 != 0) {
            current = current * 10;
            max = max * 10;
        }
        return max;
    }

    private double convertOld2(double min) {
        if (min == 0) return 1D;

        String minStr = Double.toString(min);
        int decimalPlaces = 0;

        int decimalPointIndex = minStr.indexOf('.');
        if (decimalPointIndex != -1) {
            decimalPlaces = minStr.length() - decimalPointIndex - 1;
        }

        return Math.pow(10, decimalPlaces);
    }

     private double convert(double min) {
        if (0 == min) return 1D;

        String minStr = String.valueOf(min);

        // 小数点前
        String beginVale = minStr.substring(0, minStr.indexOf("."));
        int beginLength = 0;
        if (Double.parseDouble(beginVale) > 0) {
            beginLength = minStr.substring(0, minStr.indexOf(".")).length();
        }

        // 小数点后
        String endValue = minStr.substring(minStr.indexOf(".") + 1);
        int endLength = 0;
        if (Double.parseDouble(endValue) > 0) {
            endLength = minStr.substring(minStr.indexOf(".") + 1).length();
        }

        return Math.pow(10, beginLength + endLength);
    }

    /**
     * 缓存奖品库存到Redis
     *
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     * @param awardCount 奖品库存
     */
    private void cacheStrategyAwardCount(Long strategyId, Integer awardId, Integer awardCount) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        repository.cacheStrategyAwardCount(cacheKey, awardCount);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        return getRandomAwardId(String.valueOf(strategyId));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat(Constants.UNDERLINE).concat(ruleWeightValue);
        return getRandomAwardId(key);
    }

    @Override
    public Integer getRandomAwardId(String key) {
        // 1. 获取奖品配置列表
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(Long.parseLong(key.split(Constants.UNDERLINE)[0]));
        
        // 2. 根据奖品数量选择不同的抽奖算法
        int awardCount = strategyAwardEntities.size();
        if (awardCount <= 8) {
            return randomByLoop(key, strategyAwardEntities);
        } else if (awardCount <= 16) {
            return randomByBinarySearch(key, strategyAwardEntities);
        } else {
            return randomByMultiThread(key, strategyAwardEntities);
        }
    }
    
    /**
     * O(n)时间复杂度的抽奖算法，适用于奖品数量较少的情况（<=8）
     */
    private Integer randomByLoop(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        int rateRange = repository.getRateRange(key);
        int randomValue = secureRandom.nextInt(rateRange);
        
        int cumulativeProbability = 0;
        for (StrategyAwardEntity award : strategyAwardEntities) {
            cumulativeProbability += rateRange * award.getAwardRate().doubleValue();
            if (randomValue < cumulativeProbability) {
                return award.getAwardId();
            }
        }
        
        // 默认返回第一个奖品
        return strategyAwardEntities.get(0).getAwardId();
    }
    
    /**
     * O(logn)时间复杂度的抽奖算法，适用于奖品数量中等的情况（<=16）
     */
    private Integer randomByBinarySearch(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        int rateRange = repository.getRateRange(key);
        int randomValue = secureRandom.nextInt(rateRange);
        
        // 构建累积概率数组
        int[] cumulativeProbabilities = new int[strategyAwardEntities.size()];
        int cumulative = 0;
        for (int i = 0; i < strategyAwardEntities.size(); i++) {
            cumulative += rateRange * strategyAwardEntities.get(i).getAwardRate().doubleValue();
            cumulativeProbabilities[i] = cumulative;
        }
        
        // 二分查找
        int low = 0;
        int high = cumulativeProbabilities.length - 1;
        while (low < high) {
            int mid = (low + high) / 2;
            if (randomValue < cumulativeProbabilities[mid]) {
                high = mid;
            } else {
                low = mid + 1;
            }
        }
        
        return strategyAwardEntities.get(low).getAwardId();
    }
    
    /**
     * 多线程计算的抽奖算法，适用于奖品数量较多的情况（>16）
     */
    private Integer randomByMultiThread(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        int rateRange = repository.getRateRange(key);
        int randomValue = secureRandom.nextInt(rateRange);
        
        // 将奖品列表分成多个部分，每个线程处理一部分
        int threadCount = Runtime.getRuntime().availableProcessors();
        int batchSize = (strategyAwardEntities.size() + threadCount - 1) / threadCount;
        
        // 使用CountDownLatch等待所有线程完成
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // 用于存储结果
        AtomicInteger result = new AtomicInteger(-1);
        
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        
        try {
            for (int i = 0; i < threadCount; i++) {
                final int startIndex = i * batchSize;
                final int endIndex = Math.min((i + 1) * batchSize, strategyAwardEntities.size());
                
                executorService.submit(() -> {
                    try {
                        int cumulativeProbability = 0;
                        for (int j = startIndex; j < endIndex; j++) {
                            StrategyAwardEntity award = strategyAwardEntities.get(j);
                            cumulativeProbability += rateRange * award.getAwardRate().doubleValue();
                            if (randomValue < cumulativeProbability) {
                                result.set(award.getAwardId());
                                // 中断所有线程
                                executorService.shutdownNow();
                                return;
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            // 等待所有线程完成
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
        }
        
        // 如果没有找到结果，默认返回第一个奖品
        return result.get() != -1 ? result.get() : strategyAwardEntities.get(0).getAwardId();
    }

    @Override
    public Boolean subtractionAwardStock(Long strategyId, Integer awardId, Date endDateTime) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        return repository.subtractionAwardStock(cacheKey, endDateTime);
    }

}

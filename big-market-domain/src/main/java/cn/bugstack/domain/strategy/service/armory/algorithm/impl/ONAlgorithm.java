package cn.bugstack.domain.strategy.service.armory.algorithm.impl;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.service.armory.algorithm.ILotteryAlgorithm;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description O(n)时间复杂度抽奖算法
 * 适合场景：奖品概率非常大的时候，达到几十万以上
 * 优化策略：根据奖品数量选择不同的查找方式
 * - <=8: 顺序查找
 * - <=16: 二分查找
 * - >16: 多线程查找
 * @create 2024-01-01 10:00
 */
@Slf4j
@Component
public class ONAlgorithm implements ILotteryAlgorithm {
    
    @Resource
    private IStrategyRepository repository;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    // 线程池，用于大数量奖品的多线程查找
    private final ExecutorService executorService = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors(),
        r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true); // 设置为守护线程
            return thread;
        }
    );
    
    @Override
    public void init(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        // 排序奖品列表，按概率从小到大排序
        List<StrategyAwardEntity> sortedEntities = strategyAwardEntities.stream()
                .sorted(Comparator.comparing(StrategyAwardEntity::getAwardRate))
                .collect(Collectors.toList());
        
        // 计算累计概率
        BigDecimal cumulativeRate = BigDecimal.ZERO;
        List<BigDecimal> cumulativeRates = new ArrayList<>();
        for (StrategyAwardEntity entity : sortedEntities) {
            cumulativeRate = cumulativeRate.add(entity.getAwardRate());
            cumulativeRates.add(cumulativeRate);
        }
        
        // 保存到缓存
        repository.storeStrategyCumulativeRates(key, sortedEntities, cumulativeRates);
    }
    
    @Override
    public Integer getRandomAwardId(String key) {
        // 获取奖品列表和累计概率
        List<StrategyAwardEntity> sortedEntities = repository.getStrategySortedAwards(key);
        List<BigDecimal> cumulativeRates = repository.getStrategyCumulativeRates(key);
        
        if (sortedEntities == null || sortedEntities.isEmpty() || 
            cumulativeRates == null || cumulativeRates.isEmpty()) {
            log.warn("抽奖策略未初始化，key：{}", key);
            return null;
        }
        
        // 生成0-1之间的随机数
        double random = secureRandom.nextDouble();
        BigDecimal randomRate = BigDecimal.valueOf(random);
        
        // 根据奖品数量选择不同的查找方式
        int size = sortedEntities.size();
        if (size <= 8) {
            return sequentialSearch(sortedEntities, cumulativeRates, randomRate);
        } else if (size <= 16) {
            return binarySearch(sortedEntities, cumulativeRates, randomRate);
        } else {
            return multiThreadSearch(sortedEntities, cumulativeRates, randomRate);
        }
    }
    
    @Override
    public boolean isSupport(List<StrategyAwardEntity> strategyAwardEntities) {
        // ON算法适合奖品数量较多的情况
        // 这里简单判断是否有概率大于0.0001（万分位）或者奖品数量较多
        return strategyAwardEntities.size() > 8 ||
               strategyAwardEntities.stream()
                   .anyMatch(entity -> entity.getAwardRate().compareTo(BigDecimal.valueOf(0.0001)) > 0);
    }
    
    /**
     * 顺序查找
     */
    private Integer sequentialSearch(List<StrategyAwardEntity> sortedEntities, 
                                     List<BigDecimal> cumulativeRates, 
                                     BigDecimal randomRate) {
        for (int i = 0; i < cumulativeRates.size(); i++) {
            if (randomRate.compareTo(cumulativeRates.get(i)) <= 0) {
                return sortedEntities.get(i).getAwardId();
            }
        }
        return null;
    }
    
    /**
     * 二分查找
     */
    private Integer binarySearch(List<StrategyAwardEntity> sortedEntities, 
                                 List<BigDecimal> cumulativeRates, 
                                 BigDecimal randomRate) {
        int low = 0;
        int high = cumulativeRates.size() - 1;
        
        while (low <= high) {
            int mid = (low + high) / 2;
            BigDecimal midRate = cumulativeRates.get(mid);
            
            if (randomRate.compareTo(midRate) <= 0) {
                // 检查是否是第一个满足条件的
                if (mid == 0 || randomRate.compareTo(cumulativeRates.get(mid - 1)) > 0) {
                    return sortedEntities.get(mid).getAwardId();
                } else {
                    high = mid - 1;
                }
            } else {
                low = mid + 1;
            }
        }
        
        return null;
    }
    
    /**
     * 多线程查找
     */
    private Integer multiThreadSearch(List<StrategyAwardEntity> sortedEntities, 
                                      List<BigDecimal> cumulativeRates, 
                                      BigDecimal randomRate) {
        int size = sortedEntities.size();
        int threadCount = Runtime.getRuntime().availableProcessors();
        int batchSize = size / threadCount;
        
        List<Future<Integer>> futures = new ArrayList<>();
        
        // 提交任务给线程池
        for (int i = 0; i < threadCount; i++) {
            final int start = i * batchSize;
            final int end = (i == threadCount - 1) ? size : (i + 1) * batchSize;
            
            futures.add(executorService.submit(() -> {
                for (int j = start; j < end; j++) {
                    if (randomRate.compareTo(cumulativeRates.get(j)) <= 0) {
                        return sortedEntities.get(j).getAwardId();
                    }
                }
                return null;
            }));
        }
        
        // 获取结果
        for (Future<Integer> future : futures) {
            try {
                Integer result = future.get();
                if (result != null) {
                    return result;
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("多线程查找奖品异常", e);
            }
        }
        
        return null;
    }
    
}
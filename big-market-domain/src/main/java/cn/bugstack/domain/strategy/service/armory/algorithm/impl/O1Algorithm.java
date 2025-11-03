package cn.bugstack.domain.strategy.service.armory.algorithm.impl;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.service.armory.algorithm.ILotteryAlgorithm;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description O(1)时间复杂度抽奖算法
 * 适合场景：概率配置在万分位及以下的频繁配置
 * @create 2024-01-01 10:00
 */
@Slf4j
@Component
public class O1Algorithm implements ILotteryAlgorithm {
    
    @Resource
    private IStrategyRepository repository;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    @Override
    public void init(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        // 1. 获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        // 2. 计算概率范围值
        BigDecimal rateRange = BigDecimal.valueOf(convert(minAwardRate.doubleValue()));
        
        // 3. 生成策略奖品概率查找表
        List<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();
            
            // 计算出每个概率值需要存放到查找表的数量，循环填充
            int count = rateRange.multiply(awardRate).intValue();
            for (int i = 0; i < count; i++) {
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
    
    @Override
    public Integer getRandomAwardId(String key) {
        // 从 Redis 中获取概率范围
        int rateRange = repository.getRateRange(key);
        if (rateRange <= 0) {
            log.warn("抽奖策略未初始化，key：{}", key);
            return null;
        }
        
        // 生成随机值
        int random = secureRandom.nextInt(rateRange);
        
        // 通过随机值获取奖品ID
        return repository.getStrategyAwardAssemble(key, random);
    }
    
    @Override
    public boolean isSupport(List<StrategyAwardEntity> strategyAwardEntities) {
        // O(1)算法适合概率配置在万分位及以下的情况
        // 这里简单判断是否有概率小于等于0.0001（万分位）
        return strategyAwardEntities.stream()
                .anyMatch(entity -> entity.getAwardRate().compareTo(BigDecimal.valueOf(0.0001)) <= 0);
    }
    
    /**
     * 转换计算，只根据小数位来计算。如【0.01返回100】、【0.009返回1000】、【0.0018返回10000】
     */
    private double convert(double min) {
        if (0 == min) return 1D;
        
        // 将小数转换为字符串，处理科学计数法，使用较高精度
        String minStr = String.format("%.10f", min).replaceAll("0*$", "").replaceAll("\\.$", "");
        
        // 找到小数点的位置
        int decimalPointIndex = minStr.indexOf(".");
        if (decimalPointIndex == -1) {
            return 1D; // 没有小数部分
        }
        
        // 计算小数位数
        int decimalPlaces = minStr.length() - decimalPointIndex - 1;
        
        return Math.pow(10, decimalPlaces);
    }
    
}
package cn.bugstack.domain.strategy.service.armory.algorithm;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖算法接口
 * @create 2024-01-01 10:00
 */
public interface ILotteryAlgorithm {
    
    /**
     * 初始化算法
     * @param key 策略ID或策略ID+权重值
     * @param strategyAwardEntities 策略奖品列表
     */
    void init(String key, List<StrategyAwardEntity> strategyAwardEntities);
    
    /**
     * 获取随机奖品ID
     * @param key 策略ID或策略ID+权重值
     * @return 奖品ID
     */
    Integer getRandomAwardId(String key);
    
    /**
     * 判断算法是否支持当前策略
     * @param strategyAwardEntities 策略奖品列表
     * @return 是否支持
     */
    boolean isSupport(List<StrategyAwardEntity> strategyAwardEntities);
    
}
package cn.bugstack.domain.strategy.service.armory.algorithm;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖算法工厂
 * @create 2024-01-01 10:00
 */
@Component
public class LotteryAlgorithmFactory {
    
    @Autowired
    private List<ILotteryAlgorithm> lotteryAlgorithms;
    
    // 缓存算法实例，避免重复创建
    private final Map<String, ILotteryAlgorithm> algorithmCache = new ConcurrentHashMap<>();
    
    /**
     * 获取适合的抽奖算法
     * @param key 策略ID或策略ID+权重值
     * @param strategyAwardEntities 策略奖品列表
     * @return 适合的抽奖算法
     */
    public ILotteryAlgorithm getAlgorithm(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        // 先从缓存中获取
        ILotteryAlgorithm algorithm = algorithmCache.get(key);
        
        if (algorithm == null) {
            // 缓存中没有，根据策略选择合适的算法
            algorithm = selectAlgorithm(strategyAwardEntities);
            
            // 缓存算法实例
            algorithmCache.put(key, algorithm);
            
            // 初始化算法
            algorithm.init(key, strategyAwardEntities);
        }
        
        return algorithm;
    }
    
    /**
     * 根据策略选择合适的算法
     * @param strategyAwardEntities 策略奖品列表
     * @return 合适的抽奖算法
     */
    private ILotteryAlgorithm selectAlgorithm(List<StrategyAwardEntity> strategyAwardEntities) {
        // 遍历所有算法，找到第一个支持当前策略的算法
        for (ILotteryAlgorithm algorithm : lotteryAlgorithms) {
            if (algorithm.isSupport(strategyAwardEntities)) {
                return algorithm;
            }
        }
        
        // 默认使用O(1)算法
        return lotteryAlgorithms.stream()
                .filter(algorithm -> algorithm.getClass().getSimpleName().equals("O1Algorithm"))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 清除指定策略的算法缓存
     * @param key 策略ID或策略ID+权重值
     */
    public void clearAlgorithmCache(String key) {
        algorithmCache.remove(key);
    }
    
}
package cn.bugstack.domain.strategy.service.armory.algorithm;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.service.armory.algorithm.impl.O1Algorithm;
import cn.bugstack.domain.strategy.service.armory.algorithm.impl.ONAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LotteryAlgorithmFactoryTest {
    
    private LotteryAlgorithmFactory lotteryAlgorithmFactory;
    
    @Mock
    private O1Algorithm o1Algorithm;
    
    @Mock
    private ONAlgorithm onAlgorithm;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 手动创建工厂实例并注入算法
        lotteryAlgorithmFactory = new LotteryAlgorithmFactory();
        List<ILotteryAlgorithm> algorithms = new ArrayList<>();
        algorithms.add(o1Algorithm);
        algorithms.add(onAlgorithm);
        
        // 使用反射注入算法列表
        try {
            java.lang.reflect.Field field = LotteryAlgorithmFactory.class.getDeclaredField("lotteryAlgorithms");
            field.setAccessible(true);
            field.set(lotteryAlgorithmFactory, algorithms);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to inject algorithms into factory");
        }
    }
    
    @Test
    void testGetAlgorithmForO1() {
        // 创建适合O1算法的策略
        List<StrategyAwardEntity> o1Strategy = new ArrayList<>();
        o1Strategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(101)
                .awardTitle("一等奖")
                .awardSubtitle("")
                .awardCount(10)
                .awardCountSurplus(10)
                .awardRate(new BigDecimal("0.0001"))
                .sort(1)
                .ruleModels("")
                .build());
        o1Strategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(102)
                .awardTitle("谢谢参与")
                .awardSubtitle("")
                .awardCount(0)
                .awardCountSurplus(0)
                .awardRate(new BigDecimal("0.9999"))
                .sort(2)
                .ruleModels("")
                .build());
        
        // 设置算法支持情况
        when(o1Algorithm.isSupport(o1Strategy)).thenReturn(true);
        when(onAlgorithm.isSupport(o1Strategy)).thenReturn(false);
        
        // 执行测试
        String key = "strategy_o1";
        ILotteryAlgorithm algorithm = lotteryAlgorithmFactory.getAlgorithm(key, o1Strategy);
        
        // 验证结果
        assertNotNull(algorithm);
        assertEquals(o1Algorithm, algorithm);
        verify(o1Algorithm, times(1)).init(key, o1Strategy);
    }
    
    @Test
    void testGetAlgorithmForON() {
        // 创建适合ON算法的策略
        List<StrategyAwardEntity> onStrategy = new ArrayList<>();
        onStrategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(101)
                .awardTitle("一等奖")
                .awardSubtitle("")
                .awardCount(10)
                .awardCountSurplus(10)
                .awardRate(new BigDecimal("0.10"))
                .sort(1)
                .ruleModels("")
                .build());
        onStrategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(102)
                .awardTitle("二等奖")
                .awardSubtitle("")
                .awardCount(50)
                .awardCountSurplus(50)
                .awardRate(new BigDecimal("0.20"))
                .sort(2)
                .ruleModels("")
                .build());
        onStrategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(103)
                .awardTitle("三等奖")
                .awardSubtitle("")
                .awardCount(100)
                .awardCountSurplus(100)
                .awardRate(new BigDecimal("0.30"))
                .sort(3)
                .ruleModels("")
                .build());
        onStrategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(104)
                .awardTitle("谢谢参与")
                .awardSubtitle("")
                .awardCount(0)
                .awardCountSurplus(0)
                .awardRate(new BigDecimal("0.40"))
                .sort(4)
                .ruleModels("")
                .build());
        
        // 设置算法支持情况
        when(o1Algorithm.isSupport(onStrategy)).thenReturn(false);
        when(onAlgorithm.isSupport(onStrategy)).thenReturn(true);
        
        // 执行测试
        String key = "strategy_on";
        ILotteryAlgorithm algorithm = lotteryAlgorithmFactory.getAlgorithm(key, onStrategy);
        
        // 验证结果
        assertNotNull(algorithm);
        assertEquals(onAlgorithm, algorithm);
        verify(onAlgorithm, times(1)).init(key, onStrategy);
    }
    
    @Test
    void testGetAlgorithmWithMultipleCandidates() {
        // 创建同时适合多种算法的策略
        List<StrategyAwardEntity> mixedStrategy = new ArrayList<>();
        mixedStrategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(101)
                .awardTitle("一等奖")
                .awardSubtitle("")
                .awardCount(10)
                .awardCountSurplus(10)
                .awardRate(new BigDecimal("0.0001"))
                .sort(1)
                .ruleModels("")
                .build());
        mixedStrategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(102)
                .awardTitle("二等奖")
                .awardSubtitle("")
                .awardCount(50)
                .awardCountSurplus(50)
                .awardRate(new BigDecimal("0.10"))
                .sort(2)
                .ruleModels("")
                .build());
        mixedStrategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(103)
                .awardTitle("谢谢参与")
                .awardSubtitle("")
                .awardCount(0)
                .awardCountSurplus(0)
                .awardRate(new BigDecimal("0.8999"))
                .sort(3)
                .ruleModels("")
                .build());
        
        // 设置算法支持情况
        when(o1Algorithm.isSupport(mixedStrategy)).thenReturn(true);
        when(onAlgorithm.isSupport(mixedStrategy)).thenReturn(true);
        
        // 执行测试
        String key = "strategy_mixed";
        ILotteryAlgorithm algorithm = lotteryAlgorithmFactory.getAlgorithm(key, mixedStrategy);
        
        // 验证结果 - 应该选择第一个支持的算法（O1）
        assertNotNull(algorithm);
        assertEquals(o1Algorithm, algorithm);
        verify(o1Algorithm, times(1)).init(key, mixedStrategy);
    }
    
    @Test
    void testGetAlgorithmFromCache() {
        // 创建策略
        List<StrategyAwardEntity> strategy = new ArrayList<>();
        strategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(101)
                .awardTitle("一等奖")
                .awardSubtitle("")
                .awardCount(10)
                .awardCountSurplus(10)
                .awardRate(new BigDecimal("0.0001"))
                .sort(1)
                .ruleModels("")
                .build());
        strategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(102)
                .awardTitle("谢谢参与")
                .awardSubtitle("")
                .awardCount(0)
                .awardCountSurplus(0)
                .awardRate(new BigDecimal("0.9999"))
                .sort(2)
                .ruleModels("")
                .build());
        
        // 设置算法支持情况
        when(o1Algorithm.isSupport(strategy)).thenReturn(true);
        when(onAlgorithm.isSupport(strategy)).thenReturn(false);
        
        // 第一次调用 - 应该初始化算法
        String key = "strategy_cache";
        ILotteryAlgorithm algorithm1 = lotteryAlgorithmFactory.getAlgorithm(key, strategy);
        
        // 第二次调用 - 应该从缓存中获取
        ILotteryAlgorithm algorithm2 = lotteryAlgorithmFactory.getAlgorithm(key, strategy);
        
        // 验证结果
        assertNotNull(algorithm1);
        assertNotNull(algorithm2);
        assertEquals(algorithm1, algorithm2);
        verify(o1Algorithm, times(1)).init(key, strategy); // 只初始化一次
    }
    
    @Test
    void testClearAlgorithmCache() {
        // 创建策略
        List<StrategyAwardEntity> strategy = new ArrayList<>();
        strategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(101)
                .awardTitle("一等奖")
                .awardSubtitle("")
                .awardCount(10)
                .awardCountSurplus(10)
                .awardRate(new BigDecimal("0.0001"))
                .sort(1)
                .ruleModels("")
                .build());
        strategy.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(102)
                .awardTitle("谢谢参与")
                .awardSubtitle("")
                .awardCount(0)
                .awardCountSurplus(0)
                .awardRate(new BigDecimal("0.9999"))
                .sort(2)
                .ruleModels("")
                .build());
        
        // 设置算法支持情况
        when(o1Algorithm.isSupport(strategy)).thenReturn(true);
        when(onAlgorithm.isSupport(strategy)).thenReturn(false);
        
        // 第一次调用 - 应该初始化算法
        String key = "strategy_clear_cache";
        ILotteryAlgorithm algorithm1 = lotteryAlgorithmFactory.getAlgorithm(key, strategy);
        
        // 清除缓存
        lotteryAlgorithmFactory.clearAlgorithmCache(key);
        
        // 重置mock
        reset(o1Algorithm);
        when(o1Algorithm.isSupport(strategy)).thenReturn(true);
        when(onAlgorithm.isSupport(strategy)).thenReturn(false);
        
        // 第二次调用 - 应该重新初始化算法
        ILotteryAlgorithm algorithm2 = lotteryAlgorithmFactory.getAlgorithm(key, strategy);
        
        // 验证结果
        assertNotNull(algorithm1);
        assertNotNull(algorithm2);
        verify(o1Algorithm, times(1)).init(key, strategy); // 重新初始化
    }
}
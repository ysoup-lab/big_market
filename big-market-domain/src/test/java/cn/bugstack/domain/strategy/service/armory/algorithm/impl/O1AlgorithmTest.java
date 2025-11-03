package cn.bugstack.domain.strategy.service.armory.algorithm.impl;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.algorithm.ILotteryAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class O1AlgorithmTest {
    
    @Mock
    private IStrategyRepository repository;
    
    @InjectMocks
    private O1Algorithm o1Algorithm;
    
    private List<StrategyAwardEntity> strategyAwardEntities;
    
    @Before
    public void setUp() {
        // 创建测试奖品列表
        strategyAwardEntities = new ArrayList<>();
        strategyAwardEntities.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(101)
                .awardTitle("一等奖")
                .awardSubtitle("")
                .awardCount(10)
                .awardCountSurplus(10)
                .awardRate(new BigDecimal("0.01"))
                .sort(1)
                .ruleModels("")
                .build());
        strategyAwardEntities.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(102)
                .awardTitle("二等奖")
                .awardSubtitle("")
                .awardCount(50)
                .awardCountSurplus(50)
                .awardRate(new BigDecimal("0.05"))
                .sort(2)
                .ruleModels("")
                .build());
        strategyAwardEntities.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(103)
                .awardTitle("三等奖")
                .awardSubtitle("")
                .awardCount(100)
                .awardCountSurplus(100)
                .awardRate(new BigDecimal("0.10"))
                .sort(3)
                .ruleModels("")
                .build());
        strategyAwardEntities.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(104)
                .awardTitle("谢谢参与")
                .awardSubtitle("")
                .awardCount(0)
                .awardCountSurplus(0)
                .awardRate(new BigDecimal("0.84"))
                .sort(4)
                .ruleModels("")
                .build());
    }
    
    @Test
    public void testInit() {
        // 执行初始化
        String key = "strategy_1";
        o1Algorithm.init(key, strategyAwardEntities);
        
        // 验证调用了storeStrategyAwardSearchRateTable方法
        verify(repository, times(1)).storeStrategyAwardSearchRateTable(
            eq(key), 
            anyInt(), 
            anyMap()
        );
    }
    
    @Test
    public void testGetRandomAwardId() {
        // 初始化策略
        String key = "strategy_1";
        o1Algorithm.init(key, strategyAwardEntities);
        
        // 模拟缓存数据
        int rateRange = 100; // 基于最小概率0.01计算得出
        Map<Integer, Integer> rateTable = new HashMap<>();
        // 谢谢参与: 84% of 100 = 84 entries
        for (int i = 0; i < 84; i++) {
            rateTable.put(i, 104);
        }
        // 三等奖: 10% of 100 = 10 entries
        for (int i = 84; i < 94; i++) {
            rateTable.put(i, 103);
        }
        // 二等奖: 5% of 100 = 5 entries
        for (int i = 94; i < 99; i++) {
            rateTable.put(i, 102);
        }
        // 一等奖: 1% of 100 = 1 entry
        rateTable.put(99, 101);
        
        when(repository.getRateRange(key)).thenReturn(rateRange);
        when(repository.getStrategyAwardAssemble(eq(key), anyInt())).thenAnswer(invocation -> {
            Integer random = invocation.getArgument(1);
            return rateTable.get(random);
        });
        
        // 执行多次抽奖，验证结果分布
        int[] counts = new int[4];
        int totalTests = 10000;
        
        for (int i = 0; i < totalTests; i++) {
            Integer awardId = o1Algorithm.getRandomAwardId(key);
            assertNotNull(awardId);
            
            if (awardId == 101) counts[0]++;
            else if (awardId == 102) counts[1]++;
            else if (awardId == 103) counts[2]++;
            else if (awardId == 104) counts[3]++;
            else fail("Unexpected awardId: " + awardId);
        }
        
        // 验证概率分布是否大致符合预期
        System.out.printf("一等奖概率: %.2f%% (预期: 1%%)\n", (counts[0] / (double) totalTests) * 100);
        System.out.printf("二等奖概率: %.2f%% (预期: 5%%)\n", (counts[1] / (double) totalTests) * 100);
        System.out.printf("三等奖概率: %.2f%% (预期: 10%%)\n", (counts[2] / (double) totalTests) * 100);
        System.out.printf("谢谢参与概率: %.2f%% (预期: 84%%)\n", (counts[3] / (double) totalTests) * 100);
        
        // 允许一定的误差范围
        boolean condition1 = counts[0] >= 80 && counts[0] <= 120;
        assertTrue("一等奖概率不在预期范围内", condition1);
        boolean condition2 = counts[1] >= 450 && counts[1] <= 550;
        assertTrue("二等奖概率不在预期范围内", condition2);
        boolean condition3 = counts[2] >= 900 && counts[2] <= 1100;
        assertTrue("三等奖概率不在预期范围内", condition3);
        boolean condition4 = counts[3] >= 8200 && counts[3] <= 8600;
        assertTrue("谢谢参与概率不在预期范围内", condition4);
    }
    
    @Test
    public void testIsSupport() {
        // 测试不包含小概率奖品的情况
        boolean condition5 = o1Algorithm.isSupport(strategyAwardEntities);
        assertFalse(condition5);
        
        // 测试不包含小概率奖品的情况
        List<StrategyAwardEntity> noSmallProbability = new ArrayList<>();
        noSmallProbability.add(StrategyAwardEntity.builder()
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
        noSmallProbability.add(StrategyAwardEntity.builder()
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
        noSmallProbability.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(103)
                .awardTitle("谢谢参与")
                .awardSubtitle("")
                .awardCount(0)
                .awardCountSurplus(0)
                .awardRate(new BigDecimal("0.70"))
                .sort(3)
                .ruleModels("")
                .build());
        
        // 测试包含小概率奖品的情况
        List<StrategyAwardEntity> hasSmallProbability = new ArrayList<>();
        hasSmallProbability.add(StrategyAwardEntity.builder()
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
        hasSmallProbability.add(StrategyAwardEntity.builder()
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
        hasSmallProbability.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(103)
                .awardTitle("谢谢参与")
                .awardSubtitle("")
                .awardCount(0)
                .awardCountSurplus(0)
                .awardRate(new BigDecimal("0.7999"))
                .sort(3)
                .ruleModels("")
                .build());
        
        boolean condition6 = o1Algorithm.isSupport(hasSmallProbability);
        assertTrue(condition6);
    }
}
package cn.bugstack.domain.strategy.service.armory.algorithm.impl;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ONAlgorithmTest {
    
    @Mock
    private IStrategyRepository repository;
    
    @InjectMocks
    private ONAlgorithm onAlgorithm;
    
    private List<StrategyAwardEntity> smallStrategyAwardEntities;
    private List<StrategyAwardEntity> mediumStrategyAwardEntities;
    private List<StrategyAwardEntity> largeStrategyAwardEntities;
    
    @Before
    public void setUp() {
        
        // 创建小数量奖品列表（<=8）
        smallStrategyAwardEntities = new ArrayList<>();
        smallStrategyAwardEntities.add(StrategyAwardEntity.builder()
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
        smallStrategyAwardEntities.add(StrategyAwardEntity.builder()
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
        smallStrategyAwardEntities.add(StrategyAwardEntity.builder()
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
        smallStrategyAwardEntities.add(StrategyAwardEntity.builder()
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
        
        // 创建中数量奖品列表（8 < x <=16）
        mediumStrategyAwardEntities = new ArrayList<>(smallStrategyAwardEntities);
        for (int i = 5; i <= 16; i++) {
            mediumStrategyAwardEntities.add(StrategyAwardEntity.builder()
                    .strategyId(1L)
                    .awardId(100 + i)
                    .awardTitle("纪念奖" + i)
                    .awardSubtitle("")
                    .awardCount(1000)
                    .awardCountSurplus(1000)
                    .awardRate(new BigDecimal("0.001"))
                    .sort(i)
                    .ruleModels("")
                    .build());
        }
        
        // 创建大数量奖品列表（>16）
        largeStrategyAwardEntities = new ArrayList<>(mediumStrategyAwardEntities);
        for (int i = 17; i <= 20; i++) {
            largeStrategyAwardEntities.add(StrategyAwardEntity.builder()
                    .strategyId(1L)
                    .awardId(100 + i)
                    .awardTitle("纪念奖" + i)
                    .awardSubtitle("")
                    .awardCount(2000)
                    .awardCountSurplus(2000)
                    .awardRate(new BigDecimal("0.0005"))
                    .sort(i)
                    .ruleModels("")
                    .build());
        }
    }
    
    @Test
    public void testInit() {
        // 执行初始化
        String key = "strategy_2";
        onAlgorithm.init(key, smallStrategyAwardEntities);
        
        // 验证调用了storeStrategyCumulativeRates方法
        verify(repository, times(1)).storeStrategyCumulativeRates(
            eq(key), 
            anyList(), 
            anyList()
        );
    }
    
    @Test
    public void testGetRandomAwardIdWithSmallAwards() {
        // 模拟缓存数据
        String key = "strategy_small";
        List<BigDecimal> cumulativeRates = List.of(
            new BigDecimal("0.01"),  // 101
            new BigDecimal("0.06"),  // 102
            new BigDecimal("0.16"),  // 103
            new BigDecimal("1.00")   // 104
        );
        
        when(repository.getStrategySortedAwards(key)).thenReturn(smallStrategyAwardEntities);
        when(repository.getStrategyCumulativeRates(key)).thenReturn(cumulativeRates);
        
        // 执行多次抽奖，验证结果分布
        int[] counts = new int[4];
        int totalTests = 10000;
        
        for (int i = 0; i < totalTests; i++) {
            Integer awardId = onAlgorithm.getRandomAwardId(key);
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
        assertTrue("一等奖概率不在预期范围内", counts[0] >= 80 && counts[0] <= 120);
        assertTrue("二等奖概率不在预期范围内", counts[1] >= 450 && counts[1] <= 550);
        assertTrue("三等奖概率不在预期范围内", counts[2] >= 900 && counts[2] <= 1100);
        assertTrue("谢谢参与概率不在预期范围内", counts[3] >= 8200 && counts[3] <= 8600);
    }
    
    @Test
    public void testGetRandomAwardIdWithMediumAwards() {
        // 模拟缓存数据
        String key = "strategy_medium";
        
        // 计算累计概率
        List<BigDecimal> cumulativeRates = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;
        for (StrategyAwardEntity entity : mediumStrategyAwardEntities) {
            cumulative = cumulative.add(entity.getAwardRate());
            cumulativeRates.add(cumulative);
        }
        
        when(repository.getStrategySortedAwards(key)).thenReturn(mediumStrategyAwardEntities);
        when(repository.getStrategyCumulativeRates(key)).thenReturn(cumulativeRates);
        
        // 执行多次抽奖，验证结果有效性
        int totalTests = 1000;
        
        for (int i = 0; i < totalTests; i++) {
            Integer awardId = onAlgorithm.getRandomAwardId(key);
            assertNotNull(awardId);
            
            // 验证奖品ID在有效范围内
            assertTrue("奖品ID不在有效范围内", awardId >= 101 && awardId <= 116);
        }
        
        System.out.printf("中数量奖品抽奖测试完成，共测试%d次，全部通过\n", totalTests);
    }
    
    @Test
    public void testGetRandomAwardIdWithLargeAwards() {
        // 模拟缓存数据
        String key = "strategy_large";
        
        // 计算累计概率
        List<BigDecimal> cumulativeRates = new ArrayList<>();
        BigDecimal cumulative = BigDecimal.ZERO;
        for (StrategyAwardEntity entity : largeStrategyAwardEntities) {
            cumulative = cumulative.add(entity.getAwardRate());
            cumulativeRates.add(cumulative);
        }
        
        when(repository.getStrategySortedAwards(key)).thenReturn(largeStrategyAwardEntities);
        when(repository.getStrategyCumulativeRates(key)).thenReturn(cumulativeRates);
        
        // 执行多次抽奖，验证结果有效性
        int totalTests = 1000;
        
        for (int i = 0; i < totalTests; i++) {
            Integer awardId = onAlgorithm.getRandomAwardId(key);
            assertNotNull(awardId);
            
            // 验证奖品ID在有效范围内
            assertTrue("奖品ID不在有效范围内", awardId >= 101 && awardId <= 120);
        }
        
        System.out.printf("大数量奖品抽奖测试完成，共测试%d次，全部通过\n", totalTests);
    }
    
    @Test
    public void testIsSupport() {
        // 测试包含大概率奖品的情况
        assertTrue(onAlgorithm.isSupport(smallStrategyAwardEntities));
        
        // 测试奖品数量较多的情况
        assertTrue(onAlgorithm.isSupport(mediumStrategyAwardEntities));
        assertTrue(onAlgorithm.isSupport(largeStrategyAwardEntities));
        
        // 测试不包含大概率奖品且数量较少的情况
        List<StrategyAwardEntity> noLargeProbability = new ArrayList<>();
        noLargeProbability.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(101)
                .awardTitle("一等奖")
                .awardSubtitle("")
                .awardCount(10)
                .awardCountSurplus(10)
                .awardRate(new BigDecimal("0.0000"))
                .sort(1)
                .ruleModels("")
                .build());
        noLargeProbability.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(102)
                .awardTitle("二等奖")
                .awardSubtitle("")
                .awardCount(50)
                .awardCountSurplus(50)
                .awardRate(new BigDecimal("0.0000"))
                .sort(2)
                .ruleModels("")
                .build());
        noLargeProbability.add(StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(103)
                .awardTitle("谢谢参与")
                .awardSubtitle("")
                .awardCount(0)
                .awardCountSurplus(0)
                .awardRate(new BigDecimal("0.0000"))
                .sort(3)
                .ruleModels("")
                .build());
        
        assertFalse(onAlgorithm.isSupport(noLargeProbability));
    }
}
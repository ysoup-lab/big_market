package cn.bugstack.test.domain.strategy;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.StrategyArmoryDispatch;
import cn.bugstack.types.common.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StrategyArmoryDispatchTest {
    
    @InjectMocks
    private StrategyArmoryDispatch strategyArmoryDispatch;
    
    @Mock
    private IStrategyRepository strategyRepository;
    
    private List<StrategyAwardEntity> createTestAwards(int count) {
        List<StrategyAwardEntity> awards = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            StrategyAwardEntity award = StrategyAwardEntity.builder()
                .strategyId(1L)
                .awardId(i)
                .awardTitle("奖品" + i)
                .awardCount(100)
                .awardCountSurplus(100)
                .awardRate(new BigDecimal("0.1"))
                .build();
            awards.add(award);
        }
        return awards;
    }
    
    @Before
    public void setUp() {
        // 模拟奖品配置查询
        when(strategyRepository.queryStrategyAwardList(anyLong()))
            .thenAnswer(invocation -> {
                Long strategyId = invocation.getArgument(0);
                // 这里可以根据strategyId返回不同的奖品配置
                return createTestAwards(8); // 默认返回8个奖品
            });
            
        // 模拟概率范围查询
        when(strategyRepository.getRateRange(anyString()))
            .thenReturn(10000); // 假设概率范围是10000
    }
    
    @Test
    public void testRandomByLoop() {
        // 测试O(n)算法，奖品数量<=8
        when(strategyRepository.queryStrategyAwardList(anyLong()))
            .thenReturn(createTestAwards(8));
            
        Integer awardId = strategyArmoryDispatch.getRandomAwardId("1");
        assertNotNull(awardId);
        System.out.println("O(n)算法抽奖结果: " + awardId);
    }
    
    @Test
    public void testRandomByBinarySearch() {
        // 测试O(logn)算法，奖品数量<=16
        when(strategyRepository.queryStrategyAwardList(anyLong()))
            .thenReturn(createTestAwards(16));
            
        Integer awardId = strategyArmoryDispatch.getRandomAwardId("1");
        assertNotNull(awardId);
        System.out.println("O(logn)算法抽奖结果: " + awardId);
    }
    
    @Test
    public void testRandomByMultiThread() {
        // 测试多线程算法，奖品数量>16
        when(strategyRepository.queryStrategyAwardList(anyLong()))
            .thenReturn(createTestAwards(20));
            
        Integer awardId = strategyArmoryDispatch.getRandomAwardId("1");
        assertNotNull(awardId);
        System.out.println("多线程算法抽奖结果: " + awardId);
    }
    
    @Test
    public void testRandomWithWeight() {
        // 测试带权重的抽奖
        String key = "1_4000";
        
        // 模拟带权重的奖品配置
        List<StrategyAwardEntity> weightedAwards = new ArrayList<>();
        weightedAwards.add(StrategyAwardEntity.builder()
            .strategyId(1L)
            .awardId(1)
            .awardTitle("权重奖品1")
            .awardCount(100)
            .awardCountSurplus(100)
            .awardRate(new BigDecimal("0.5"))
            .build());
        weightedAwards.add(StrategyAwardEntity.builder()
            .strategyId(1L)
            .awardId(2)
            .awardTitle("权重奖品2")
            .awardCount(100)
            .awardCountSurplus(100)
            .awardRate(new BigDecimal("0.3"))
            .build());
        weightedAwards.add(StrategyAwardEntity.builder()
            .strategyId(1L)
            .awardId(3)
            .awardTitle("权重奖品3")
            .awardCount(100)
            .awardCountSurplus(100)
            .awardRate(new BigDecimal("0.2"))
            .build());
            
        when(strategyRepository.queryStrategyAwardList(anyLong()))
            .thenReturn(weightedAwards);
            
        Integer awardId = strategyArmoryDispatch.getRandomAwardId(key);
        assertNotNull(awardId);
        System.out.println("带权重抽奖结果: " + awardId);
    }
    
    @Test
    public void testProbabilityDistribution() {
        // 测试概率分布是否符合预期
        when(strategyRepository.queryStrategyAwardList(anyLong()))
            .thenReturn(createTestAwards(3));
            
        // 模拟三个奖品的概率分别为0.6, 0.3, 0.1
        List<StrategyAwardEntity> awards = createTestAwards(3);
        awards.get(0).setAwardRate(new BigDecimal("0.6"));
        awards.get(1).setAwardRate(new BigDecimal("0.3"));
        awards.get(2).setAwardRate(new BigDecimal("0.1"));
        
        when(strategyRepository.queryStrategyAwardList(anyLong()))
            .thenReturn(awards);
            
        // 运行多次抽奖，统计结果
        int totalTests = 10000;
        int[] counts = new int[3];
        
        for (int i = 0; i < totalTests; i++) {
            Integer awardId = strategyArmoryDispatch.getRandomAwardId("1");
            counts[awardId - 1]++;
        }
        
        // 打印结果
        System.out.println("概率分布测试结果:");
        System.out.println("奖品1期望概率: 0.6，实际概率: " + (counts[0] / (double) totalTests));
        System.out.println("奖品2期望概率: 0.3，实际概率: " + (counts[1] / (double) totalTests));
        System.out.println("奖品3期望概率: 0.1，实际概率: " + (counts[2] / (double) totalTests));
        
        // 简单验证概率是否大致符合预期
        assert counts[0] > 5500 && counts[0] < 6500;
        assert counts[1] > 2500 && counts[1] < 3500;
        assert counts[2] > 500 && counts[2] < 1500;
    }
}
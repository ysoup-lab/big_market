package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RuleWeightLogicChainTest {

    @InjectMocks
    private RuleWeightLogicChain ruleWeightLogicChain;

    @Mock
    private IStrategyRepository strategyRepository;

    @Mock
    private IStrategyDispatch strategyDispatch;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置默认用户积分为4500
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 4500L);
    }

    @Test
    public void testLogic_RuleValueNull() {
        // 模拟查询不到规则值
        when(strategyRepository.queryStrategyRuleValue(anyLong(), anyString())).thenReturn(null);

        // 调用测试方法
        Integer awardId = ruleWeightLogicChain.logic("user001", 100001L);

        // 验证结果
        assertNull(awardId);
        verify(strategyRepository, times(1)).queryStrategyRuleValue(100001L, "rule_weight");
        verify(strategyDispatch, never()).getRandomAwardId(anyLong(), anyString());
    }

    @Test
    public void testLogic_RuleValueEmpty() {
        // 模拟查询到空规则值
        when(strategyRepository.queryStrategyRuleValue(anyLong(), anyString())).thenReturn("");

        // 调用测试方法
        Integer awardId = ruleWeightLogicChain.logic("user001", 100001L);

        // 验证结果
        assertNull(awardId);
        verify(strategyRepository, times(1)).queryStrategyRuleValue(100001L, "rule_weight");
        verify(strategyDispatch, never()).getRandomAwardId(anyLong(), anyString());
    }

    @Test
    public void testLogic_AnalyticalValueEmpty() {
        // 模拟查询到无效格式的规则值
        when(strategyRepository.queryStrategyRuleValue(anyLong(), anyString())).thenReturn("invalid_format");

        // 调用测试方法
        Integer awardId = ruleWeightLogicChain.logic("user001", 100001L);

        // 验证结果
        assertNull(awardId);
        verify(strategyRepository, times(1)).queryStrategyRuleValue(100001L, "rule_weight");
        verify(strategyDispatch, never()).getRandomAwardId(anyLong(), anyString());
    }

    @Test
    public void testLogic_NoMatchingWeight() {
        // 模拟查询到规则值，但用户积分不足
        when(strategyRepository.queryStrategyRuleValue(anyLong(), anyString())).thenReturn("5000:102,103,104");
        // 设置用户积分低于最低权重
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 4000L);

        // 调用测试方法
        Integer awardId = ruleWeightLogicChain.logic("user001", 100001L);

        // 验证结果
        assertNull(awardId);
        verify(strategyRepository, times(1)).queryStrategyRuleValue(100001L, "rule_weight");
        verify(strategyDispatch, never()).getRandomAwardId(anyLong(), anyString());
    }

    @Test
    public void testLogic_WithMatchingWeight() {
        // 模拟查询到规则值
        String ruleValue = "4000:102,103,104 5000:102,103,104,105";
        when(strategyRepository.queryStrategyRuleValue(anyLong(), anyString())).thenReturn(ruleValue);
        // 模拟抽奖结果
        when(strategyDispatch.getRandomAwardId(anyLong(), anyString())).thenReturn(102);

        // 调用测试方法
        Integer awardId = ruleWeightLogicChain.logic("user001", 100001L);

        // 验证结果
        assertEquals(Integer.valueOf(102), awardId);
        verify(strategyRepository, times(1)).queryStrategyRuleValue(100001L, "rule_weight");
        verify(strategyDispatch, times(1)).getRandomAwardId(100001L, "4000:102,103,104");
    }

    @Test
    public void testLogic_InvalidRuleFormat() {
        // 模拟查询到格式无效的规则值
        String ruleValue = "4000:102,103,104 invalid_rule";
        when(strategyRepository.queryStrategyRuleValue(anyLong(), anyString())).thenReturn(ruleValue);
        // 模拟抽奖结果
        when(strategyDispatch.getRandomAwardId(anyLong(), anyString())).thenReturn(102);

        // 调用测试方法
        Integer awardId = ruleWeightLogicChain.logic("user001", 100001L);

        // 验证结果
        assertEquals(Integer.valueOf(102), awardId);
        verify(strategyRepository, times(1)).queryStrategyRuleValue(100001L, "rule_weight");
        verify(strategyDispatch, times(1)).getRandomAwardId(100001L, "4000:102,103,104");
    }

    @Test
    public void testLogic_NumberFormatException() {
        // 模拟查询到包含非数字的规则值
        String ruleValue = "invalid_number:102,103,104";
        when(strategyRepository.queryStrategyRuleValue(anyLong(), anyString())).thenReturn(ruleValue);

        // 调用测试方法
        Integer awardId = ruleWeightLogicChain.logic("user001", 100001L);

        // 验证结果
        assertNull(awardId);
        verify(strategyRepository, times(1)).queryStrategyRuleValue(100001L, "rule_weight");
        verify(strategyDispatch, never()).getRandomAwardId(anyLong(), anyString());
    }
}
package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import cn.bugstack.types.common.Constants;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuleWeightLogicChainTest {

    @Mock
    private IStrategyRepository repository;

    @Mock
    private IStrategyDispatch strategyDispatch;

    @Mock
    private ILogicChain nextChain;

    @InjectMocks
    private RuleWeightLogicChain ruleWeightLogicChain;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置用户积分为5000
        ruleWeightLogicChain.userScore = 5000L;
    }

    /**
     * 测试规则值为null的情况
     */
    @Test
    public void testLogicRuleValueNull() {
        // 模拟repository返回null
        when(repository.queryStrategyRuleValue(1L, "rule_weight")).thenReturn(null);
        // 模拟下一个责任链返回100
        when(nextChain.logic("test_user", 1L)).thenReturn(100);
        // 设置下一个责任链
        ruleWeightLogicChain.appendNext(nextChain);

        Integer awardId = ruleWeightLogicChain.logic("test_user", 1L);

        // 应该调用下一个责任链并返回100
        assertEquals(100, awardId);
        verify(repository, times(1)).queryStrategyRuleValue(1L, "rule_weight");
        verify(nextChain, times(1)).logic("test_user", 1L);
    }

    /**
     * 测试规则值为空字符串的情况
     */
    @Test
    public void testLogicRuleValueEmpty() {
        // 模拟repository返回空字符串
        when(repository.queryStrategyRuleValue(1L, "rule_weight")).thenReturn("");
        // 模拟下一个责任链返回100
        when(nextChain.logic("test_user", 1L)).thenReturn(100);
        // 设置下一个责任链
        ruleWeightLogicChain.appendNext(nextChain);

        Integer awardId = ruleWeightLogicChain.logic("test_user", 1L);

        // 应该调用下一个责任链并返回100
        assertEquals(100, awardId);
        verify(repository, times(1)).queryStrategyRuleValue(1L, "rule_weight");
        verify(nextChain, times(1)).logic("test_user", 1L);
    }

    /**
     * 测试解析后的权重规则为空的情况
     */
    @Test
    public void testLogicAnalyticalValueEmpty() {
        // 模拟repository返回无效格式的规则值
        when(repository.queryStrategyRuleValue(1L, "rule_weight")).thenReturn("invalid_format");
        // 模拟下一个责任链返回100
        when(nextChain.logic("test_user", 1L)).thenReturn(100);
        // 设置下一个责任链
        ruleWeightLogicChain.appendNext(nextChain);

        // 应该抛出IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            ruleWeightLogicChain.logic("test_user", 1L);
        });
        verify(repository, times(1)).queryStrategyRuleValue(1L, "rule_weight");
    }

    /**
     * 测试没有下一个责任链的情况
     */
    @Test
    public void testLogicNoNextChain() {
        // 模拟repository返回null
        when(repository.queryStrategyRuleValue(1L, "rule_weight")).thenReturn(null);

        Integer awardId = ruleWeightLogicChain.logic("test_user", 1L);

        // 应该返回null，因为没有下一个责任链
        assertNull(awardId);
        verify(repository, times(1)).queryStrategyRuleValue(1L, "rule_weight");
    }

    /**
     * 测试正常权重抽奖情况
     */
    @Test
    public void testLogicNormal() {
        // 模拟repository返回规则值
        String ruleValue = "4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109";
        when(repository.queryStrategyRuleValue(1L, "rule_weight")).thenReturn(ruleValue);
        // 模拟strategyDispatch返回102
        when(strategyDispatch.getRandomAwardId(1L, "5000:102,103,104,105,106,107")).thenReturn(102);

        Integer awardId = ruleWeightLogicChain.logic("test_user", 1L);

        // 应该返回102
        assertEquals(102, awardId);
        verify(repository, times(1)).queryStrategyRuleValue(1L, "rule_weight");
        verify(strategyDispatch, times(1)).getRandomAwardId(1L, "5000:102,103,104,105,106,107");
    }

    /**
     * 测试权重规则解析
     */
    @Test
    public void testGetAnalyticalValue() {
        String ruleValue = "4000:102,103,104,105 5000:102,103,104,105,106,107";
        
        RuleWeightLogicChain spyChain = spy(new RuleWeightLogicChain());
        ReflectionTestUtils.setField(spyChain, "repository", repository);
        ReflectionTestUtils.setField(spyChain, "strategyDispatch", strategyDispatch);
        
        Map<Long, String> result = spyChain.getAnalyticalValue(ruleValue);
        
        assertEquals(2, result.size());
        assertTrue(result.containsKey(4000L));
        assertTrue(result.containsKey(5000L));
        assertEquals("4000:102,103,104,105", result.get(4000L));
        assertEquals("5000:102,103,104,105,106,107", result.get(5000L));
    }

    /**
     * 测试空规则值解析
     */
    @Test
    public void testGetAnalyticalValueEmpty() {
        RuleWeightLogicChain spyChain = spy(new RuleWeightLogicChain());
        ReflectionTestUtils.setField(spyChain, "repository", repository);
        ReflectionTestUtils.setField(spyChain, "strategyDispatch", strategyDispatch);
        
        Map<Long, String> result = spyChain.getAnalyticalValue(null);
        
        assertTrue(result.isEmpty());
        
        result = spyChain.getAnalyticalValue("");
        
        assertTrue(result.isEmpty());
    }

    /**
     * 测试包含空规则项的解析
     */
    @Test
    public void testGetAnalyticalValueWithEmptyItem() {
        String ruleValue = "4000:102,103,104,105   5000:102,103,104,105,106,107";
        
        RuleWeightLogicChain spyChain = spy(new RuleWeightLogicChain());
        ReflectionTestUtils.setField(spyChain, "repository", repository);
        ReflectionTestUtils.setField(spyChain, "strategyDispatch", strategyDispatch);
        
        Map<Long, String> result = spyChain.getAnalyticalValue(ruleValue);
        
        assertEquals(2, result.size());
        assertTrue(result.containsKey(4000L));
        assertTrue(result.containsKey(5000L));
    }
}
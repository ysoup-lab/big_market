package cn.bugstack.domain.strategy.service.rule.chain.impl;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.types.common.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RuleWeightLogicChainTest {

    @Mock
    private IStrategyRepository repository;

    @Mock
    private IStrategyDispatch strategyDispatch;

    @InjectMocks
    private RuleWeightLogicChain ruleWeightLogicChain;

    @BeforeEach
    void setUp() {
        // 设置默认的用户积分
        ruleWeightLogicChain.userScore = 5000L;
    }

    @Test
    void testLogic_WhenUserIdIsNull_ShouldReturnNull() {
        // Arrange
        String userId = null;
        Long strategyId = 1L;

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertNull(result);
        verifyNoInteractions(repository, strategyDispatch);
    }

    @Test
    void testLogic_WhenStrategyIdIsNull_ShouldReturnNull() {
        // Arrange
        String userId = "user123";
        Long strategyId = null;

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertNull(result);
        verifyNoInteractions(repository, strategyDispatch);
    }

    @Test
    void testLogic_WhenRuleValueIsNull_ShouldPassToNextChain() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn(null);

        // Create a mock next chain
        RuleWeightLogicChain nextChain = mock(RuleWeightLogicChain.class);
        when(nextChain.logic(userId, strategyId)).thenReturn(101);
        ruleWeightLogicChain.appendNext(nextChain);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertEquals(101, result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verify(nextChain).logic(userId, strategyId);
        verifyNoInteractions(strategyDispatch);
    }

    @Test
    void testLogic_WhenRuleValueIsEmpty_ShouldPassToNextChain() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn("");

        // Create a mock next chain
        RuleWeightLogicChain nextChain = mock(RuleWeightLogicChain.class);
        when(nextChain.logic(userId, strategyId)).thenReturn(101);
        ruleWeightLogicChain.appendNext(nextChain);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertEquals(101, result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verify(nextChain).logic(userId, strategyId);
        verifyNoInteractions(strategyDispatch);
    }

    @Test
    void testLogic_WhenRuleValueHasSomeInvalidRules_ShouldReturnAwardId() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";
        String invalidRuleValue = "4000:102,103,104 5000";
        Integer expectedAwardId = 102;

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn(invalidRuleValue);
        when(strategyDispatch.getRandomAwardId(strategyId, "4000:102,103,104")).thenReturn(expectedAwardId);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertEquals(expectedAwardId, result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verify(strategyDispatch).getRandomAwardId(strategyId, "4000:102,103,104");
        verifyNoMoreInteractions(repository, strategyDispatch);
    }

    @Test
    void testLogic_WhenNoMatchingWeightRange_ShouldPassToNextChain() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";
        String ruleValue = "6000:102,103,104 7000:105,106";

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn(ruleValue);

        // Create a mock next chain
        RuleWeightLogicChain nextChain = mock(RuleWeightLogicChain.class);
        when(nextChain.logic(userId, strategyId)).thenReturn(101);
        ruleWeightLogicChain.appendNext(nextChain);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertEquals(101, result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verify(nextChain).logic(userId, strategyId);
        verifyNoInteractions(strategyDispatch);
    }

    @Test
    void testLogic_WhenMatchingWeightRange_ShouldReturnAwardId() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";
        String ruleValue = "4000:102,103,104 5000:105,106,107";
        Integer expectedAwardId = 105;

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn(ruleValue);
        when(strategyDispatch.getRandomAwardId(strategyId, "5000:105,106,107")).thenReturn(expectedAwardId);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertEquals(expectedAwardId, result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verify(strategyDispatch).getRandomAwardId(strategyId, "5000:105,106,107");
        verifyNoMoreInteractions(repository, strategyDispatch);
    }

    @Test
    void testLogic_WhenNextChainIsNull_ShouldReturnNull() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";
        String ruleValue = "6000:102,103,104";

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn(ruleValue);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertNull(result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verifyNoInteractions(strategyDispatch);
    }

    @Test
    void testLogic_WhenRuleValueIsNull_ShouldReturnNull() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn(null);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertNull(result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verifyNoInteractions(strategyDispatch);
    }

    @Test
    void testLogic_WhenRuleValueIsEmpty_ShouldReturnNull() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn("");

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertNull(result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verifyNoInteractions(strategyDispatch);
    }

    @Test
    void testLogic_WhenRuleValueHasEmptyRule_ShouldReturnAwardId() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";
        String ruleValue = "4000:102,103   5000:104,105";
        Integer expectedAwardId = 104;

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn(ruleValue);
        when(strategyDispatch.getRandomAwardId(strategyId, "5000:104,105")).thenReturn(expectedAwardId);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertEquals(expectedAwardId, result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verify(strategyDispatch).getRandomAwardId(strategyId, "5000:104,105");
        verifyNoMoreInteractions(repository, strategyDispatch);
    }

    @Test
    void testLogic_WhenRuleValueHasInvalidFormat_ShouldReturnAwardId() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";
        String ruleValue = "4000:102,103 invalid 5000";
        Integer expectedAwardId = 102;

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn(ruleValue);
        when(strategyDispatch.getRandomAwardId(strategyId, "4000:102,103")).thenReturn(expectedAwardId);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertEquals(expectedAwardId, result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verify(strategyDispatch).getRandomAwardId(strategyId, "4000:102,103");
        verifyNoMoreInteractions(repository, strategyDispatch);
    }

    @Test
    void testLogic_WhenRuleValueHasNonNumericKey_ShouldReturnAwardId() {
        // Arrange
        String userId = "user123";
        Long strategyId = 1L;
        String ruleModel = "rule_weight";
        String ruleValue = "4000:102,103 abc:104,105 5000:106,107";
        Integer expectedAwardId = 106;

        when(repository.queryStrategyRuleValue(strategyId, ruleModel)).thenReturn(ruleValue);
        when(strategyDispatch.getRandomAwardId(strategyId, "5000:106,107")).thenReturn(expectedAwardId);

        // Act
        Integer result = ruleWeightLogicChain.logic(userId, strategyId);

        // Assert
        assertEquals(expectedAwardId, result);
        verify(repository).queryStrategyRuleValue(strategyId, ruleModel);
        verify(strategyDispatch).getRandomAwardId(strategyId, "5000:106,107");
        verifyNoMoreInteractions(repository, strategyDispatch);
    }
}
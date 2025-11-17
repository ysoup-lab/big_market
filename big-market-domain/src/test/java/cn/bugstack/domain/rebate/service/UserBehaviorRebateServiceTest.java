package cn.bugstack.domain.rebate.service;

import cn.bugstack.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.bugstack.domain.rebate.repository.IRebateRepository;
import cn.bugstack.domain.task.model.entity.TaskEntity;
import cn.bugstack.types.event.BaseEvent;
import cn.bugstack.domain.rebate.event.UserBehaviorRebateMessageEvent;
import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserBehaviorRebateServiceTest {

    @InjectMocks
    private UserBehaviorRebateService userBehaviorRebateService;

    @Mock
    private IRebateRepository rebateRepository;

    @Mock
    private UserBehaviorRebateMessageEvent userBehaviorRebateMessageEvent;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserBehaviorRebateOrder() {
        // 1. 准备测试数据
        String userId = "123456";         
        String behaviorType = "SHARE";    
        String bizId = "share_789";

        // 2. 模拟依赖行为
        when(userBehaviorRebateMessageEvent.topic()).thenReturn("rebate_topic");
        
        BaseEvent.EventMessage<UserBehaviorRebateOrderEntity> mockMessage = BaseEvent.EventMessage.<UserBehaviorRebateOrderEntity>builder()
                .id("mock_msg_id")
                .timestamp(System.currentTimeMillis())
                .data(new UserBehaviorRebateOrderEntity())
                .build();
        when(userBehaviorRebateMessageEvent.buildEventMessage(any())).thenReturn(mockMessage);

        // 3. 执行测试
        String orderId = userBehaviorRebateService.createUserBehaviorRebateOrder(userId, behaviorType, bizId);

        // 4. 验证结果
        assertNotNull(orderId);
        
        // 5. 验证交互
        verify(rebateRepository, times(1)).saveUserBehaviorRebateOrder(any());
        verify(rebateRepository, times(1)).saveTask(any(TaskEntity.class));
    }

    @Test
    public void testQueryUserBehaviorRebateOrder() {
        // 1. 准备测试数据
        String orderId = "order_123456";
        
        // 2. 模拟依赖行为
        UserBehaviorRebateOrderEntity mockOrder = new UserBehaviorRebateOrderEntity();
        mockOrder.setOrderId(orderId);
        when(rebateRepository.queryUserBehaviorRebateOrder(orderId)).thenReturn(mockOrder);

        // 3. 执行测试
        UserBehaviorRebateOrderEntity result = userBehaviorRebateService.queryUserBehaviorRebateOrder(orderId);

        // 4. 验证结果
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
    }
}
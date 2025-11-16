package cn.bugstack.trigger.service;

import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.bugstack.domain.activity.model.vo.ActivityVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.IRaffleActivityPartakeService;
import cn.bugstack.domain.award.service.IAwardService;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.trigger.application.command.PartakeActivityCommand;
import cn.bugstack.trigger.application.response.PartakeActivityResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class RaffleActivityServiceImplTest {

    @InjectMocks
    private RaffleActivityServiceImpl raffleActivityServiceImpl;

    @Mock
    private IActivityRepository activityRepository;

    @Mock
    private IRaffleActivityPartakeService raffleActivityPartakeService;

    @Mock
    private IRaffleStrategy raffleStrategy;

    @Mock
    private IAwardService awardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPartakeActivity() {
        // 准备测试数据
        PartakeActivityCommand command = new PartakeActivityCommand();
        command.setActivityId(10001L);
        command.setUserId("test_user_id");
        command.setStrategyId(10001L);

        ActivityVO activityVO = new ActivityVO();
        activityVO.setActivityId(10001L);
        activityVO.setStrategyId(10001L);

        UserRaffleOrderEntity userRaffleOrderEntity = new UserRaffleOrderEntity();
        userRaffleOrderEntity.setOrderId("test_order_id");
        userRaffleOrderEntity.setActivityId(10001L);
        userRaffleOrderEntity.setStrategyId(10001L);
        userRaffleOrderEntity.setUserId("test_user_id");

        // 设置mock对象的行为
        when(activityRepository.queryActivityById(10001L)).thenReturn(activityVO);
        when(raffleActivityPartakeService.createOrder(command)).thenReturn(userRaffleOrderEntity);

        // 执行测试
        PartakeActivityResponse response = raffleActivityServiceImpl.partakeActivity(command);

        // 验证结果
        Assertions.assertNotNull(response);
        Assertions.assertEquals("0000", response.getCode());
        Assertions.assertEquals("调用成功", response.getInfo());
        Assertions.assertEquals("test_order_id", response.getOrderId());
        Assertions.assertEquals(10001L, response.getActivityId());
        Assertions.assertEquals(10001L, response.getStrategyId());
    }
}
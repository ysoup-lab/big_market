package cn.bugstack.domain.rebate.event;

import cn.bugstack.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.bugstack.types.event.BaseEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户行为返利消息事件
 * @create 2024-04-30 18:00
 */
@Component
public class UserBehaviorRebateMessageEvent extends BaseEvent<UserBehaviorRebateOrderEntity> {

    @Value("${spring.rabbitmq.topic.user_behavior_rebate}")
    private String topic;

    @Override
    public EventMessage<UserBehaviorRebateOrderEntity> buildEventMessage(UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity) {
        return EventMessage.<UserBehaviorRebateOrderEntity>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(userBehaviorRebateOrderEntity)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

}
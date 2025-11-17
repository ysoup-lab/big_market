package cn.bugstack.trigger.listener;

import cn.bugstack.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityOrderEntity;
import cn.bugstack.domain.activity.model.valobj.OrderStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.rebate.event.SendRebateMessageEvent;
import cn.bugstack.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 行为返利消息消费者
 * @create 2024-04-30 15:31
 */
@Slf4j
@Component
public class RebateMessageCustomer {

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @Resource
    private IActivityRepository activityRepository;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void listener(String message) {
        try {
            log.info("监听用户行为返利消息 topic: {} message: {}", topic, message);
            // 转换对象
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {}.getType());
            SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();
            
            // 解析返利配置（格式：activityId|sku|count）
            String[] rebateConfigArray = rebateMessage.getRebateConfig().split("\\|");
            if (rebateConfigArray.length != 3) {
                log.error("返利配置格式错误 rebateConfig: {}", rebateMessage.getRebateConfig());
                return;
            }
            Long activityId = Long.parseLong(rebateConfigArray[0]);
            Long sku = Long.parseLong(rebateConfigArray[1]);
            Integer count = Integer.parseInt(rebateConfigArray[2]);
            
            // 创建活动订单实体
            ActivityOrderEntity activityOrderEntity = ActivityOrderEntity.builder()
                    .userId(rebateMessage.getUserId())
                    .sku(sku)
                    .activityId(activityId)
                    .activityName("行为返利")
                    .strategyId(activityId) // 策略ID等于活动ID
                    .orderId("rebate_" + System.currentTimeMillis() + "_" + rebateMessage.getUserId())
                    .orderTime(new Date())
                    .totalCount(count)
                    .dayCount(count)
                    .monthCount(count)
                    .state(OrderStateVO.completed) // 完成状态
                    .outBusinessNo(rebateMessage.getBizId()) // 使用业务ID作为外部业务号
                    .build();
            
            // 创建账户额度下单聚合对象
            CreateQuotaOrderAggregate createOrderAggregate = CreateQuotaOrderAggregate.builder()
                    .userId(rebateMessage.getUserId())
                    .activityId(activityId)
                    .totalCount(count)
                    .dayCount(count)
                    .monthCount(count)
                    .activityOrderEntity(activityOrderEntity)
                    .build();
            
            // 调用活动仓库服务执行订单保存和账户更新
            activityRepository.doSaveOrder(createOrderAggregate);
            
            log.info("行为返利结算完成 userId: {} activityId: {} sku: {} count: {}", 
                    rebateMessage.getUserId(), activityId, sku, count);
        } catch (Exception e) {
            log.error("监听用户行为返利消息，消费失败 topic: {} message: {}", topic, message);
            throw e;
        }
    }

}
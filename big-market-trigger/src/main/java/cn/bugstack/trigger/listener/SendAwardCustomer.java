package cn.bugstack.trigger.listener;

import cn.bugstack.domain.award.event.SendAwardMessageEvent;
import cn.bugstack.domain.credit.service.IUserCreditService;
import cn.bugstack.infrastructure.persistent.dao.IAwardDao;
import cn.bugstack.infrastructure.persistent.po.Award;
import cn.bugstack.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户奖品记录消息消费者
 * @create 2024-04-06 12:09
 */
@Slf4j
@Component
public class SendAwardCustomer {

    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;

    @Resource
    private IAwardDao awardDao;

    @Resource
    private IUserCreditService userCreditService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_award}"))
    public void listener(String message) {
        try {
            log.info("监听用户奖品发送消息 topic: {} message: {}", topic, message);
            // 1. 转换消息
            BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage>>() {
            }.getType());
            SendAwardMessageEvent.SendAwardMessage sendAwardMessage = eventMessage.getData();

            // 2. 查询奖品信息
            Award award = awardDao.queryAwardInfo(sendAwardMessage.getAwardId());
            if (award == null) {
                log.error("监听用户奖品发送消息，奖品不存在 awardId: {} topic: {} message: {}", sendAwardMessage.getAwardId(), topic, message);
                return;
            }

            // 3. 判断奖品类型，处理积分奖品
            if ("user_credit_random".equals(award.getAwardKey()) || "user_credit_blacklist".equals(award.getAwardKey())) {
                // 解析积分数量，配置格式为 "min,max" 或固定值
                String awardConfig = award.getAwardConfig();
                Double integralAmount;
                if (awardConfig.contains(",")) {
                    // 随机积分范围
                    String[] range = awardConfig.split(",");
                    int min = Integer.parseInt(range[0]);
                    int max = Integer.parseInt(range[1]);
                    // 生成随机积分
                    integralAmount = Math.random() * (max - min + 1) + min;
                    // 保留两位小数
                    integralAmount = Math.round(integralAmount * 100.0) / 100.0;
                } else {
                    // 固定积分值
                    integralAmount = Double.parseDouble(awardConfig);
                }

                // 4. 发放积分
                boolean success = userCreditService.grantIntegral(sendAwardMessage.getUserId(), integralAmount, "award_" + sendAwardMessage.getAwardId());
                if (success) {
                    log.info("监听用户奖品发送消息，积分发放成功 userId: {} awardId: {} integralAmount: {} topic: {} message: {}",
                            sendAwardMessage.getUserId(), sendAwardMessage.getAwardId(), integralAmount, topic, message);
                } else {
                    log.error("监听用户奖品发送消息，积分发放失败 userId: {} awardId: {} integralAmount: {} topic: {} message: {}",
                            sendAwardMessage.getUserId(), sendAwardMessage.getAwardId(), integralAmount, topic, message);
                }
            } else {
                // 其他奖品类型暂不处理
                log.info("监听用户奖品发送消息，非积分奖品类型，暂不处理 awardKey: {} topic: {} message: {}", award.getAwardKey(), topic, message);
            }
        } catch (Exception e) {
            log.error("监听用户奖品发送消息，消费失败 topic: {} message: {}", topic, message, e);
            throw e;
        }
    }

}

package cn.bugstack.trigger.listener;

import cn.bugstack.domain.activity.model.entity.SkuRechargeEntity;
import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.credit.model.entity.CreditAdjustmentEntity;
import cn.bugstack.domain.credit.model.valobj.AdjustTypeVO;
import cn.bugstack.domain.credit.model.valobj.TradeTypeVO;
import cn.bugstack.domain.credit.service.ICreditAdjustmentService;
import cn.bugstack.domain.rebate.event.SendRebateMessageEvent;
import cn.bugstack.domain.rebate.model.valobj.RebateTypeVO;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.event.BaseEvent;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 监听；行为返利消息
 * @create 2024-05-01 13:58
 */
@Slf4j
@Component
public class RebateMessageCustomer {

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    @Resource
    private ICreditAdjustmentService creditAdjustmentService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void listener(String message) {
        try {
            log.info("监听用户行为返利消息 topic: {} message: {}", topic, message);
            // 1. 转换消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {
            }.getType());
            SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();
            
            // 2. 处理积分奖励
            if (RebateTypeVO.INTEGRAL.getCode().equals(rebateMessage.getRebateType())) {
                CreditAdjustmentEntity creditAdjustmentEntity = new CreditAdjustmentEntity();
                creditAdjustmentEntity.setUserId(rebateMessage.getUserId());
                creditAdjustmentEntity.setAdjustType(AdjustTypeVO.ADD.getCode());
                creditAdjustmentEntity.setTradeType(TradeTypeVO.REBATE.getCode());
                creditAdjustmentEntity.setAmount(Integer.valueOf(rebateMessage.getRebateConfig()));
                creditAdjustmentEntity.setBizId(rebateMessage.getBizId());
                creditAdjustmentEntity.setBizDesc(rebateMessage.getBizDesc());
                creditAdjustmentService.adjustCredit(creditAdjustmentEntity);
                log.info("监听用户行为返利消息 - 积分奖励处理完成 topic: {} message: {}", topic, message);
                return;
            }
            
            // 3. 处理SKU奖励
            if (RebateTypeVO.SKU.getCode().equals(rebateMessage.getRebateType())) {
                SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                skuRechargeEntity.setUserId(rebateMessage.getUserId());
                skuRechargeEntity.setSku(Long.valueOf(rebateMessage.getRebateConfig()));
                skuRechargeEntity.setOutBusinessNo(rebateMessage.getBizId());
                raffleActivityAccountQuotaService.createOrder(skuRechargeEntity);
                log.info("监听用户行为返利消息 - SKU奖励处理完成 topic: {} message: {}", topic, message);
                return;
            }
            
            log.info("监听用户行为返利消息 - 未知奖励类型暂不处理 topic: {} message: {}", topic, message);
        } catch (AppException e) {
            if (ResponseCode.INDEX_DUP.getCode().equals(e.getCode())) {
                log.warn("监听用户行为返利消息，消费重复 topic: {} message: {}", topic, message, e);
                return;
            }
            throw e;
        } catch (Exception e) {
            log.error("监听用户行为返利消息，消费失败 topic: {} message: {}", topic, message, e);
            throw e;
        }
    }

}

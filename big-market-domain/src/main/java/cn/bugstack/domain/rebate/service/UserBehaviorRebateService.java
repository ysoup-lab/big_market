package cn.bugstack.domain.rebate.service;

import cn.bugstack.domain.rebate.event.UserBehaviorRebateMessageEvent;
import cn.bugstack.types.event.BaseEvent;
import cn.bugstack.domain.rebate.model.aggregate.CreateRebateOrderAggregate;
import cn.bugstack.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.bugstack.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.bugstack.domain.rebate.model.valobj.RebateTypeVO;
import cn.bugstack.domain.rebate.repository.IRebateRepository;
import cn.bugstack.domain.task.model.entity.TaskEntity;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户行为返利服务实现
 * @create 2024-04-30 18:00
 */
@Service
public class UserBehaviorRebateService implements IUserBehaviorRebateService {

    @Resource
    private IRebateRepository rebateRepository;
    @Resource
    private UserBehaviorRebateMessageEvent userBehaviorRebateMessageEvent;
    
    public UserBehaviorRebateService(IRebateRepository rebateRepository) {
        this.rebateRepository = rebateRepository;
    }
    
    @Override
    public String createUserBehaviorRebateOrder(String userId, String behaviorType, String bizId) {
        // 1. 构建用户行为返利订单实体
        UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity = UserBehaviorRebateOrderEntity.builder()
                .userId(userId)
                .orderId(RandomStringUtils.randomNumeric(12)) // 生成12位订单号
                .behaviorType(BehaviorTypeVO.valueOf(behaviorType.toUpperCase()))
                .rebateDesc(getRebateDesc(behaviorType))
                .rebateType(getRebateType(behaviorType))
                .rebateConfig(getRebateConfig(behaviorType))
                .bizId(bizId)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        
        // 2. 构建任务实体（这里需要根据实际业务逻辑构建，示例中简化处理）
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setUserId(userId);
        taskEntity.setTopic("user_behavior_rebate");
        taskEntity.setMessageId(RandomStringUtils.randomNumeric(12));
        taskEntity.setMessage(JSON.toJSONString(userBehaviorRebateOrderEntity));
        
        // 3. 构建聚合对象
        CreateRebateOrderAggregate createRebateOrderAggregate = CreateRebateOrderAggregate.builder()
                .userId(userId)
                .userBehaviorRebateOrderEntity(userBehaviorRebateOrderEntity)
                .build();
        
        // 4. 保存订单和任务（事务处理）
        rebateRepository.saveUserBehaviorRebateOrder(createRebateOrderAggregate);
        
        // 5. 构建消息并发送
        BaseEvent.EventMessage<UserBehaviorRebateOrderEntity> messageEvent = userBehaviorRebateMessageEvent.buildEventMessage(userBehaviorRebateOrderEntity);
        
        // 6. 构建任务实体
        TaskEntity newTaskEntity = new TaskEntity();
        newTaskEntity.setUserId(userBehaviorRebateOrderEntity.getUserId());
        newTaskEntity.setTopic(userBehaviorRebateMessageEvent.topic());
        newTaskEntity.setMessageId(messageEvent.getId());
        newTaskEntity.setMessage(JSON.toJSONString(messageEvent.getData()));
        
        // 7. 保存任务
        rebateRepository.saveTask(newTaskEntity);
        
        return userBehaviorRebateOrderEntity.getOrderId();
    }
    
    @Override
    public UserBehaviorRebateOrderEntity queryUserBehaviorRebateOrder(String orderId) {
        return rebateRepository.queryUserBehaviorRebateOrder(orderId);
    }
    
    /**
     * 获取返利描述
     * 
     * @param behaviorType 行为类型
     * @return 返利描述
     */
    private String getRebateDesc(String behaviorType) {
        // 根据行为类型获取返利描述，这里可以从配置中读取
        if ("sign".equals(behaviorType)) {
            return "签到返利";
        } else if ("openai_pay".equals(behaviorType)) {
            return "OpenAI支付返利";
        }
        return "未知返利";
    }
    
    /**
     * 获取返利类型
     * 
     * @param behaviorType 行为类型
     * @return 返利类型
     */
    private RebateTypeVO getRebateType(String behaviorType) {
        // 根据行为类型获取返利类型，这里可以从配置中读取
        if ("sign".equals(behaviorType)) {
            return RebateTypeVO.SKU;
        } else if ("openai_pay".equals(behaviorType)) {
            return RebateTypeVO.INTEGRAL;
        }
        return RebateTypeVO.SKU;
    }
    
    /**
     * 获取返利配置
     * 
     * @param behaviorType 行为类型
     * @return 返利配置
     */
    private String getRebateConfig(String behaviorType) {
        // 根据行为类型获取返利配置，这里可以从配置中读取
        if ("sign".equals(behaviorType)) {
            return "9011"; // SKU值
        } else if ("openai_pay".equals(behaviorType)) {
            return "10"; // 积分值
        }
        return "0";
    }
    
    /**
     * 发送MQ消息
     * 
     * @param userBehaviorRebateOrderEntity 用户行为返利订单实体
     */
    private void sendMqMessage(UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity) {
        // 这里实现MQ消息发送逻辑
        // 示例：rabbitTemplate.convertAndSend("user_behavior_rebate_exchange", "user_behavior_rebate_key", userBehaviorRebateOrderEntity);
    }

}
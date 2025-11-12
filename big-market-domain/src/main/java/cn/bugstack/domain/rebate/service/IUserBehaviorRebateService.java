package cn.bugstack.domain.rebate.service;

import cn.bugstack.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户行为返利服务接口
 * @create 2024-04-30 18:00
 */
public interface IUserBehaviorRebateService {

    /**
     * 创建用户行为返利订单
     * 
     * @param userId 用户ID
     * @param behaviorType 行为类型（sign 签到、openai_pay 支付）
     * @param bizId 业务ID - 拼接的唯一值
     * @return 订单ID
     */
    String createUserBehaviorRebateOrder(String userId, String behaviorType, String bizId);
    
    /**
     * 查询用户行为返利订单
     * 
     * @param orderId 订单ID
     * @return 用户行为返利订单实体
     */
    UserBehaviorRebateOrderEntity queryUserBehaviorRebateOrder(String orderId);

}
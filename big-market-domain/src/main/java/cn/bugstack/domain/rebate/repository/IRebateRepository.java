package cn.bugstack.domain.rebate.repository;

import cn.bugstack.domain.rebate.model.aggregate.CreateRebateOrderAggregate;
import cn.bugstack.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.bugstack.domain.task.model.entity.TaskEntity;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 返利仓储接口
 * @create 2024-04-30 18:00
 */
public interface IRebateRepository {

    /**
     * 保存用户行为返利订单
     * 
     * @param createRebateOrderAggregate 创建返利订单聚合对象
     */
    void saveUserBehaviorRebateOrder(CreateRebateOrderAggregate createRebateOrderAggregate);
    
    /**
     * 根据订单ID查询用户行为返利订单
     * 
     * @param orderId 订单ID
     * @return 用户行为返利订单实体
     */
    UserBehaviorRebateOrderEntity queryUserBehaviorRebateOrder(String orderId);
    
    /**
     * 根据业务ID查询用户行为返利订单
     * 
     * @param bizId 业务ID
     * @return 用户行为返利订单实体
     */
    UserBehaviorRebateOrderEntity queryUserBehaviorRebateOrderByBizId(String bizId);
    
    /**
     * 保存任务实体
     * 
     * @param taskEntity 任务实体
     */
    void saveTask(TaskEntity taskEntity);
}
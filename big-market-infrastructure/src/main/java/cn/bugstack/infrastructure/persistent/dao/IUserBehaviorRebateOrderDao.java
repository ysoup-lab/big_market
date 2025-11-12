package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserBehaviorRebateOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户行为返利流水订单DAO
 * @create 2024-04-30 18:00
 */
@Mapper
public interface IUserBehaviorRebateOrderDao {

    /**
     * 插入用户行为返利订单
     * 
     * @param userBehaviorRebateOrder 用户行为返利订单PO
     */
    void insert(UserBehaviorRebateOrder userBehaviorRebateOrder);
    
    /**
     * 根据订单ID查询用户行为返利订单
     * 
     * @param orderId 订单ID
     * @return 用户行为返利订单PO
     */
    UserBehaviorRebateOrder queryByOrderId(String orderId);
    
    /**
     * 根据业务ID查询用户行为返利订单
     * 
     * @param bizId 业务ID
     * @return 用户行为返利订单PO
     */
    UserBehaviorRebateOrder queryByBizId(String bizId);

}
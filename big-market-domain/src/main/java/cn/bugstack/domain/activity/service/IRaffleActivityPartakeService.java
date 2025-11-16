package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖活动参与服务
 * @create 2024-04-04 19:50
 */
public interface IRaffleActivityPartakeService {

    /**
     * 创建抽奖单；用户参与抽奖活动，扣减活动账户库存，产生抽奖单。如存在未被使用的抽奖单则直接返回已存在的抽奖单。
     *
     * @param partakeRaffleActivityEntity 参与抽奖活动实体对象
     * @return 用户抽奖订单实体对象
     */
    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);

    /**
     * 更新用户抽奖订单状态
     *
     * @param orderId 订单ID
     * @param state   订单状态（create-创建、used-已使用、cancel-已作废）
     */
    void updateOrderState(String orderId, String state);

}

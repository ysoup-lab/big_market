package cn.bugstack.domain.activity.service.partake;

import cn.bugstack.domain.activity.model.entity.PartakeActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 活动参与服务接口
 * @create 2024-03-09 10:05
 */
public interface IActivityPartakeService {

    /**
     * 用户参与活动
     * <p>
     * 1. 扣减用户账户总额度
     * 2. 扣减用户账户月额度
     * 3. 扣减用户账户日额度
     * 4. 生成参与活动的订单流水
     *
     * @param partakeActivityEntity 用户参与活动实体对象
     * @return 用户抽奖订单实体
     */
    UserRaffleOrderEntity partakeActivity(PartakeActivityEntity partakeActivityEntity);

}
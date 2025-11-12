package cn.bugstack.domain.rebate.model.aggregate;

import cn.bugstack.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.bugstack.domain.task.model.entity.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 创建返利订单聚合对象
 * @create 2024-04-30 18:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRebateOrderAggregate {

    /** 用户ID */
    private String userId;
    /** 用户行为返利订单实体 */
    private UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity;
    /** 任务实体 */
    private TaskEntity taskEntity;

}
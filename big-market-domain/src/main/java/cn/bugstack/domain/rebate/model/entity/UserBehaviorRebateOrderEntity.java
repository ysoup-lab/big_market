package cn.bugstack.domain.rebate.model.entity;

import cn.bugstack.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.bugstack.domain.rebate.model.valobj.RebateTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户行为返利订单实体
 * @create 2024-04-30 18:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBehaviorRebateOrderEntity {

    /** 用户ID */
    private String userId;
    /** 订单ID */
    private String orderId;
    /** 行为类型 */
    private BehaviorTypeVO behaviorType;
    /** 返利描述 */
    private String rebateDesc;
    /** 返利类型 */
    private RebateTypeVO rebateType;
    /** 返利配置 */
    private String rebateConfig;
    /** 业务ID - 拼接的唯一值 */
    private String bizId;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
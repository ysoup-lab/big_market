package cn.bugstack.domain.activity.model.entity;

import lombok.Data;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户抽奖订单实体对象
 * @create 2024-03-09 10:05
 */
@Data
public class UserRaffleOrderEntity {

    /** 订单ID */
    private String orderId;

    /** 用户ID */
    private String userId;

    /** 活动ID */
    private Long activityId;

    /** 活动名称 */
    private String activityName;

    /** 活动SKU */
    private Long sku;

    /** 策略ID */
    private Long strategyId;

    /** 总次数 */
    private Integer totalCount;

    /** 日次数 */
    private Integer dayCount;

    /** 月次数 */
    private Integer monthCount;

    /** 订单状态 */
    private String state;

    /** 外部业务号 */
    private String outBusinessNo;

}

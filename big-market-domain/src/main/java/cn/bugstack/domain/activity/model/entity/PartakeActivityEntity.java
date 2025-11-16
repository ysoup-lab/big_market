package cn.bugstack.domain.activity.model.entity;

import lombok.Data;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户参与活动实体对象
 * @create 2024-03-09 10:05
 */
@Data
public class PartakeActivityEntity {

    /** 用户ID */
    private String userId;

    /** 活动ID */
    private Long activityId;

    /** 活动SKU */
    private Long sku;

}

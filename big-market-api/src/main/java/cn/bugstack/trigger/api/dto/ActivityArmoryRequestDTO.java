package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 活动装配请求参数
 * @create 2024-04-06 11:05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityArmoryRequestDTO {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动SKU
     */
    private Long sku;

}

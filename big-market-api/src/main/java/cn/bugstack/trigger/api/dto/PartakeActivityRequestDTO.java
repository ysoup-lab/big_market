package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 参与抽奖活动请求参数
 * @create 2024-04-06 11:07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartakeActivityRequestDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动SKU
     */
    private Long sku;

    /**
     * 业务ID
     */
    private String outBusinessNo;

}

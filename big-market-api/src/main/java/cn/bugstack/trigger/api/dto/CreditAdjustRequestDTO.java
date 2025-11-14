package cn.bugstack.trigger.api.dto;

import lombok.Data;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分调整请求DTO
 * @create 2024-05-20 11:10
 */
@Data
public class CreditAdjustRequestDTO {
    
    /** 用户ID */
    private String userId;
    /** 调整类型：increase/decrease */
    private String adjustType;
    /** 交易类型：behavior_rebate/product_exchange/system_adjust/refund/payment */
    private String tradeType;
    /** 调整积分 */
    private Integer amount;
    /** 业务ID */
    private String bizId;
    /** 业务描述 */
    private String bizDesc;
    
}
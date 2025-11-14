package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分调整记录表
 * @create 2024-05-20 10:45
 */
@Data
public class CreditAdjustment {
    
    /** 主键ID */
    private Long id;
    /** 积分调整ID */
    private String creditAdjustmentId;
    /** 用户ID */
    private String userId;
    /** 积分账户ID */
    private String creditAccountId;
    /** 调整类型 */
    private String adjustType;
    /** 交易类型 */
    private String tradeType;
    /** 调整积分 */
    private Integer amount;
    /** 调整前积分 */
    private Integer beforeAmount;
    /** 调整后积分 */
    private Integer afterAmount;
    /** 业务ID */
    private String bizId;
    /** 业务描述 */
    private String bizDesc;
    /** 创建时间 */
    private Date createTime;
    
}
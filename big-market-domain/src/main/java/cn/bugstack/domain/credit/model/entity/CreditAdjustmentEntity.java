package cn.bugstack.domain.credit.model.entity;

import cn.bugstack.domain.credit.model.valobj.AdjustTypeVO;
import cn.bugstack.domain.credit.model.valobj.TradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分调整实体对象
 * @create 2024-05-20 10:05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditAdjustmentEntity {
    
    /** 积分调整ID */
    private String creditAdjustmentId;
    /** 用户ID */
    private String userId;
    /** 积分账户ID */
    private String creditAccountId;
    /** 调整类型 */
    private AdjustTypeVO adjustType;
    /** 交易类型 */
    private TradeTypeVO tradeType;
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
    private String createTime;
    
}
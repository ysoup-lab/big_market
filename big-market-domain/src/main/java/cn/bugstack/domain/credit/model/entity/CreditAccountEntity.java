package cn.bugstack.domain.credit.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分账户实体对象
 * @create 2024-05-20 10:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditAccountEntity {
    
    /** 用户ID */
    private String userId;
    /** 积分账户ID */
    private String creditAccountId;
    /** 总积分 */
    private Integer totalCredits;
    /** 可用积分 */
    private Integer availableCredits;
    /** 冻结积分 */
    private Integer frozenCredits;
    /** 创建时间 */
    private String createTime;
    /** 更新时间 */
    private String updateTime;
    
}
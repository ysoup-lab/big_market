package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分账户表
 * @create 2024-05-20 10:40
 */
@Data
public class CreditAccount {
    
    /** 主键ID */
    private Long id;
    /** 积分账户ID */
    private String creditAccountId;
    /** 用户ID */
    private String userId;
    /** 总积分 */
    private Integer totalCredits;
    /** 可用积分 */
    private Integer availableCredits;
    /** 冻结积分 */
    private Integer frozenCredits;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
    
}
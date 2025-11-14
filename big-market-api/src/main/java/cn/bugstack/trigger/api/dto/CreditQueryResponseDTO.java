package cn.bugstack.trigger.api.dto;

import lombok.Data;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分查询响应DTO
 * @create 2024-05-20 11:15
 */
@Data
public class CreditQueryResponseDTO {
    
    /** 用户ID */
    private String userId;
    /** 总积分 */
    private Integer totalCredits;
    /** 可用积分 */
    private Integer availableCredits;
    /** 冻结积分 */
    private Integer frozenCredits;
    
}
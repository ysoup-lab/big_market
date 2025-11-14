package cn.bugstack.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分调整类型
 * @create 2024-05-20 10:10
 */
@Getter
@AllArgsConstructor
public enum AdjustTypeVO {
    
    INCREASE("increase", "增加积分"),
    DECREASE("decrease", "减少积分");
    
    private final String code;
    private final String desc;
    
}
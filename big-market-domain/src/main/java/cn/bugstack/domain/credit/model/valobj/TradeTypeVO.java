package cn.bugstack.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分交易类型
 * @create 2024-05-20 10:15
 */
@Getter
@AllArgsConstructor
public enum TradeTypeVO {
    
    BEHAVIOR_REBATE("behavior_rebate", "行为返利"),
    PRODUCT_EXCHANGE("product_exchange", "商品兑换"),
    SYSTEM_ADJUST("system_adjust", "系统调整"),
    REFUND("refund", "退款"),
    PAYMENT("payment", "支付");
    
    private final String code;
    private final String desc;
    
}
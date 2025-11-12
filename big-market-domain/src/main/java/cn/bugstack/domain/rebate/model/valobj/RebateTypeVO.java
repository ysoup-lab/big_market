package cn.bugstack.domain.rebate.model.valobj;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 返利类型值对象
 * @create 2024-04-30 18:00
 */
public enum RebateTypeVO {
    
    SKU("sku", "活动库存充值商品"),
    INTEGRAL("integral", "用户活动积分"),
    // 可以根据需要扩展其他返利类型
    ;
    
    private final String code;
    private final String desc;
    
    RebateTypeVO(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}
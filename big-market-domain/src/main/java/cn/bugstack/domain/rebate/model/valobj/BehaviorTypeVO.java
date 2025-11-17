package cn.bugstack.domain.rebate.model.valobj;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 行为类型值对象
 * @create 2024-04-30 18:00
 */
public enum BehaviorTypeVO {
    
    SIGN("sign", "签到"),
    OPENAI_PAY("openai_pay", "OpenAI支付"),
    // 可以根据需要扩展其他行为类型
    ;
    
    private final String code;
    private final String desc;
    
    BehaviorTypeVO(String code, String desc) {
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
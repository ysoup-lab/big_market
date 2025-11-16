package cn.bugstack.trigger.api.dto;

import cn.bugstack.types.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 参与抽奖活动响应参数
 * @create 2024-04-06 11:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartakeActivityResponseDTO {

    /**
     * 抽奖单号
     */
    private String orderId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 策略ID
     */
    private Long strategyId;

    /**
     * 奖品ID
     */
    private Integer awardId;

    /**
     * 奖品名称
     */
    private String awardName;

    /**
     * 奖品内容
     */
    private String awardContent;

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应信息
     */
    private String info;

    public boolean isSuccess() {
        return ResponseCode.SUCCESS.getCode().equals(code);
    }

}

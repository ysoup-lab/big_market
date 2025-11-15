package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 抽奖结果响应DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaffleResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 抽奖策略ID
     */
    private Long strategyId;

    /**
     * 中奖奖品ID
     */
    private Integer awardId;

    /**
     * 中奖奖品标题
     */
    private String awardTitle;

    /**
     * 中奖奖品副标题
     */
    private String awardSubtitle;

    /**
     * 中奖奖品内容
     */
    private String awardContent;

    /**
     * 中奖概率
     */
    private Integer awardRate;

}
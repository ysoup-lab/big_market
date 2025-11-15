package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 查询奖品列表响应DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardListResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    private Long strategyId;

    /**
     * 奖品列表
     */
    private List<AwardInfoDTO> awardList;

    /**
     * 奖品信息DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AwardInfoDTO {

        /**
         * 奖品ID
         */
        private Integer awardId;

        /**
         * 奖品标题
         */
        private String awardTitle;

        /**
         * 奖品副标题
         */
        private String awardSubtitle;

        /**
         * 奖品内容
         */
        private String awardContent;

        /**
         * 中奖概率
         */
        private Integer awardRate;

        /**
         * 奖品库存
         */
        private Integer awardStock;

        /**
         * 奖品剩余库存
         */
        private Integer awardStockSurplus;

    }

}
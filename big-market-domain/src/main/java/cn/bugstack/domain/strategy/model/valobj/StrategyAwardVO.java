package cn.bugstack.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 抽奖奖品值对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardVO {
    /** 策略ID */
    private Long strategyId;
    /** 奖品ID */
    private Integer awardId;
    /** 奖品库存总量 */
    private Integer awardCount;
    /** 奖品库存剩余 */
    private Integer awardCountSurplus;
    /** 奖品中奖概率 */
    private BigDecimal awardRate;
    /** 奖品名称 */
    private String awardName;
    /** 奖品描述 */
    private String awardDesc;
}

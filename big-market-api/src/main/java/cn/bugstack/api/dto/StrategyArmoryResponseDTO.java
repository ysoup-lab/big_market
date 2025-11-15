package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 策略装配响应DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyArmoryResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    private Long strategyId;

    /**
     * 装配状态：0-未装配，1-已装配
     */
    private Integer status;

    /**
     * 装配描述
     */
    private String message;

}
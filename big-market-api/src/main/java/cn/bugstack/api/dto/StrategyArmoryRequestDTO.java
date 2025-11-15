package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 策略装配请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyArmoryRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    private Long strategyId;

}
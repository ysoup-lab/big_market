package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 抽奖请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaffleRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 策略ID
     */
    private Long strategyId;

}
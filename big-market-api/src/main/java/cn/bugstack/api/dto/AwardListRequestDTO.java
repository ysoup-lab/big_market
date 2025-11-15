package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 查询奖品列表请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardListRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 策略ID
     */
    private Long strategyId;

}
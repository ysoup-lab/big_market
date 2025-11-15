package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * 抽奖奖品接口
 */
public interface IRaffleAward {

    /**
     * 根据策略ID查询奖品列表
     * @param strategyId 策略ID
     * @return 奖品列表
     */
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    /**
     * 根据策略ID和奖品ID查询奖品信息
     * @param strategyId 策略ID
     * @param awardId 奖品ID
     * @return 奖品信息
     */
    StrategyAwardEntity queryStrategyAward(Long strategyId, Integer awardId);

}
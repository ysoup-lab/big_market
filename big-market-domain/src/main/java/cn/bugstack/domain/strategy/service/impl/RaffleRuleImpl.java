package cn.bugstack.domain.strategy.service.impl;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.IRaffleRule;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖规则接口实现类
 * @create 2024-04-20 09:17
 */
@Service
public class RaffleRuleImpl implements IRaffleRule {

    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public Map<String, Integer> queryAwardRuleLockCount(String[] treeIds) {
        return strategyRepository.queryAwardRuleLockCount(treeIds);
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        return strategyRepository.queryStrategyIdByActivityId(activityId);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return strategyRepository.queryStrategyRuleValue(strategyId, ruleModel);
    }

}
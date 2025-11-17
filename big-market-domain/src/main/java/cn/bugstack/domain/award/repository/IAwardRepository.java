package cn.bugstack.domain.award.repository;

import cn.bugstack.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.bugstack.domain.award.model.entity.AwardEntity;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 奖品表仓储接口
 * @create 2024-05-23 20:33
 */
public interface IAwardRepository {

    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    AwardEntity queryAwardInfo(Integer awardId);

}

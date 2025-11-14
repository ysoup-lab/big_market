package cn.bugstack.domain.activity.service.rule.impl;

import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.model.valobj.ActivityStateVO;
import cn.bugstack.domain.activity.service.rule.AbstractActionChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 活动规则过滤【日期、状态】
 * @create 2024-03-23 10:23
 */
@Slf4j
@Component("activity_base_action")
public class ActivityBaseActionChain extends AbstractActionChain {

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {

        log.info("活动责任链-基础信息【有效期、状态】校验开始。");

        // 1. 校验活动状态
        if (!ActivityStateVO.create.getCode().equals(activityEntity.getState().getCode())) {
            log.error("活动状态不正确，当前状态：{}", activityEntity.getState().getCode());
            return false;
        }

        // 2. 校验活动时间
        Date now = new Date();
        if (now.before(activityEntity.getBeginDateTime()) || now.after(activityEntity.getEndDateTime())) {
            log.error("活动时间不在有效期内，当前时间：{}，活动开始时间：{}，活动结束时间：{}", now, activityEntity.getBeginDateTime(), activityEntity.getEndDateTime());
            return false;
        }

        // 3. 继续执行下一个责任链
        return next().action(activitySkuEntity, activityEntity, activityCountEntity);
    }

}

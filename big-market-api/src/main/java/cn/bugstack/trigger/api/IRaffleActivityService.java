package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.ActivityDrawRequestDTO;
import cn.bugstack.trigger.api.dto.ActivityDrawResponseDTO;
import cn.bugstack.types.model.Response;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖活动服务
 * @create 2024-04-13 09:16
 */
public interface IRaffleActivityService {

    /**
     * 活动装配，数据预热缓存
     *
     * @param activityId 活动ID
     * @return 装配结果
     */
    Response<Boolean> armory(Long activityId);

    /**
     * 活动抽奖接口
     *
     * @param request 请求对象
     * @return 返回结果
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO request);

    /**
     * 日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    Response<Boolean> calendarSignRebate(String userId);

    /**
     * 是否签到过接口
     *
     * @param userId 用户ID
     * @return 是否签到过结果
     */
    Response<Boolean> isCalendarSignRebate(String userId);

    /**
     * 查询账户额度接口
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @return 账户额度信息
     */
    Response<cn.bugstack.domain.activity.model.entity.ActivityAccountEntity> queryUserActivityAccount(String userId, Long activityId);



}

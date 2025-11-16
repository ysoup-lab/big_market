package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.ActivityArmoryRequestDTO;
import cn.bugstack.trigger.api.dto.ActivityArmoryResponseDTO;
import cn.bugstack.trigger.api.dto.PartakeActivityRequestDTO;
import cn.bugstack.trigger.api.dto.PartakeActivityResponseDTO;
import cn.bugstack.types.model.Response;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖活动服务接口
 * @create 2024-04-06 11:00
 */
public interface IRaffleActivityService {

    /**
     * 活动装配接口
     *
     * @param requestDTO 活动装配请求参数
     * @return 装配结果
     */
    Response<ActivityArmoryResponseDTO> armoryActivity(ActivityArmoryRequestDTO requestDTO);

    /**
     * 参与抽奖活动
     *
     * @param requestDTO 参与抽奖活动请求参数
     * @return 抽奖结果
     */
    Response<PartakeActivityResponseDTO> partakeActivity(PartakeActivityRequestDTO requestDTO);

}
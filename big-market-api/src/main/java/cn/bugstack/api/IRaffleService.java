package cn.bugstack.api;

import cn.bugstack.api.dto.*;
import cn.bugstack.types.dto.Response;

/**
 * 抽奖服务接口
 */
public interface IRaffleService {

    /**
     * 装配策略接口
     * 将抽奖策略装配到缓存
     * @param requestDTO 策略装配请求DTO
     * @return 策略装配响应DTO
     */
    Response<StrategyArmoryResponseDTO> assembleStrategy(StrategyArmoryRequestDTO requestDTO);

    /**
     * 查询奖品列表配置
     * @param requestDTO 查询奖品列表请求DTO
     * @return 查询奖品列表响应DTO
     */
    Response<AwardListResponseDTO> queryAwardList(AwardListRequestDTO requestDTO);

    /**
     * 随机抽奖接口
     * @param requestDTO 抽奖请求DTO
     * @return 抽奖结果响应DTO
     */
    Response<RaffleResponseDTO> raffle(RaffleRequestDTO requestDTO);

}
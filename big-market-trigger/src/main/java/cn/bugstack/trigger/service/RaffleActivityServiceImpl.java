package cn.bugstack.trigger.service;

import cn.bugstack.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.bugstack.domain.activity.model.valobj.UserRaffleOrderStateVO;
import cn.bugstack.domain.activity.service.IRaffleActivityPartakeService;
import cn.bugstack.domain.activity.service.armory.IActivityArmory;
import cn.bugstack.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.service.IAwardService;
import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.trigger.api.IRaffleActivityService;
import cn.bugstack.trigger.api.dto.*;
import cn.bugstack.types.model.Response;
import cn.bugstack.types.enums.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖活动服务实现类
 * @create 2024-04-06 11:15
 */
@Service
public class RaffleActivityServiceImpl implements IRaffleActivityService {

    @Resource
    private IActivityArmory activityArmory;

    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private IAwardService awardService;

    @Override
    public Response<ActivityArmoryResponseDTO> armoryActivity(ActivityArmoryRequestDTO requestDTO) {
        // 执行活动装配
        boolean result = activityArmory.assembleActivitySku(requestDTO.getSku());

        // 封装响应结果
        ActivityArmoryResponseDTO responseDTO = ActivityArmoryResponseDTO.builder()
                .success(result)
                .activityId(requestDTO.getActivityId())
                .sku(requestDTO.getSku())
                .build();

        return Response.<ActivityArmoryResponseDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(responseDTO)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<PartakeActivityResponseDTO> partakeActivity(PartakeActivityRequestDTO requestDTO) {
        // 1. 创建抽奖订单
        PartakeRaffleActivityEntity partakeRaffleActivityEntity = PartakeRaffleActivityEntity.builder()
                .userId(requestDTO.getUserId())
                .activityId(requestDTO.getActivityId())
                .build();

        UserRaffleOrderEntity userRaffleOrderEntity = raffleActivityPartakeService.createOrder(partakeRaffleActivityEntity);
        if (userRaffleOrderEntity == null) {
            return Response.<PartakeActivityResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info("创建抽奖订单失败")
                    .build();
        }

        // 2. 执行抽奖
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId(requestDTO.getUserId())
                .strategyId(userRaffleOrderEntity.getStrategyId())
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
        if (raffleAwardEntity == null) {
            return Response.<PartakeActivityResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info("抽奖失败")
                    .build();
        }

        // 3. 保存抽奖结果
        UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                .userId(requestDTO.getUserId())
                .activityId(requestDTO.getActivityId())
                .strategyId(userRaffleOrderEntity.getStrategyId())
                .orderId(userRaffleOrderEntity.getOrderId())
                .awardId(raffleAwardEntity.getAwardId())
                .awardTitle("默认奖品")
                .build();

        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .build();

        // 保存中奖记录
        awardService.saveUserAwardRecord(userAwardRecordAggregate);

        // 4. 更新抽奖订单状态为已使用
        raffleActivityPartakeService.updateOrderState(userRaffleOrderEntity.getOrderId(), UserRaffleOrderStateVO.used.getCode());

        // 5. 封装响应结果
        PartakeActivityResponseDTO responseDTO = PartakeActivityResponseDTO.builder()
                .orderId(userRaffleOrderEntity.getOrderId())
                .activityId(requestDTO.getActivityId())
                .strategyId(userRaffleOrderEntity.getStrategyId())
                .awardId(raffleAwardEntity.getAwardId())
                .build();

        return Response.<PartakeActivityResponseDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(responseDTO)
                .build();
    }
}

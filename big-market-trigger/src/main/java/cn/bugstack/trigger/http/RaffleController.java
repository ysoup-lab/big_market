package cn.bugstack.trigger.http;

import cn.bugstack.api.IRaffleService;
import cn.bugstack.api.dto.*;
import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.service.IRaffleAward;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.types.dto.Response;
import cn.bugstack.types.enums.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽奖服务HTTP接口实现
 */
@RestController
public class RaffleController implements IRaffleService {

    @Autowired
    private IStrategyArmory strategyArmory;

    @Autowired
    private IRaffleAward raffleAward;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Override
    @PostMapping("/api/raffle/assembleStrategy")
    public Response<StrategyArmoryResponseDTO> assembleStrategy(@RequestBody StrategyArmoryRequestDTO requestDTO) {
        try {
            boolean success = strategyArmory.assembleLotteryStrategy(requestDTO.getStrategyId());
            StrategyArmoryResponseDTO responseDTO = new StrategyArmoryResponseDTO();
            responseDTO.setStrategyId(requestDTO.getStrategyId());
            responseDTO.setStatus(success ? 1 : 0);
            responseDTO.setMessage(success ? "策略装配成功" : "策略装配失败");
            return Response.success(responseDTO);
        } catch (Exception e) {
            return Response.fail(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    @PostMapping("/api/raffle/queryAwardList")
    public Response<AwardListResponseDTO> queryAwardList(@RequestBody AwardListRequestDTO requestDTO) {
        try {
            List<StrategyAwardEntity> awardEntityList = raffleAward.queryStrategyAwardList(requestDTO.getStrategyId());
            List<AwardListResponseDTO.AwardInfoDTO> awardInfoList = awardEntityList.stream()
                    .map(award -> {
                        AwardListResponseDTO.AwardInfoDTO infoDTO = new AwardListResponseDTO.AwardInfoDTO();
                        infoDTO.setAwardId(award.getAwardId());
                        // 移除不存在的属性设置
                        infoDTO.setAwardRate(award.getAwardRate().intValue());
                        infoDTO.setAwardStock(award.getAwardCount());
                        infoDTO.setAwardStockSurplus(award.getAwardCountSurplus());
                        return infoDTO;
                    })
                    .collect(Collectors.toList());

            AwardListResponseDTO responseDTO = new AwardListResponseDTO();
            responseDTO.setStrategyId(requestDTO.getStrategyId());
            responseDTO.setAwardList(awardInfoList);

            return Response.success(responseDTO);
        } catch (Exception e) {
            return Response.fail(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

    @Override
    @PostMapping("/api/raffle/raffle")
    public Response<RaffleResponseDTO> raffle(@RequestBody RaffleRequestDTO requestDTO) {
        try {
            RaffleFactorEntity raffleFactorEntity = new RaffleFactorEntity();
            raffleFactorEntity.setUserId(requestDTO.getUserId());
            raffleFactorEntity.setStrategyId(requestDTO.getStrategyId());

            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
            if (raffleAwardEntity == null || raffleAwardEntity.getAwardId() == null) {
                return Response.fail(ResponseCode.UN_ERROR.getCode(), "抽奖失败");
            }

            StrategyAwardEntity strategyAwardEntity = raffleAward.queryStrategyAward(requestDTO.getStrategyId(), raffleAwardEntity.getAwardId());
            if (strategyAwardEntity == null) {
                return Response.fail(ResponseCode.UN_ERROR.getCode(), "未找到奖品信息");
            }

            RaffleResponseDTO responseDTO = new RaffleResponseDTO();
            responseDTO.setStrategyId(requestDTO.getStrategyId());
            responseDTO.setAwardId(strategyAwardEntity.getAwardId());
            // 移除不存在的属性设置
            responseDTO.setAwardRate(strategyAwardEntity.getAwardRate().intValue());

            return Response.success(responseDTO);
        } catch (Exception e) {
            return Response.fail(ResponseCode.UN_ERROR.getCode(), e.getMessage());
        }
    }

}
package cn.bugstack.trigger.http;

import cn.bugstack.domain.credit.model.entity.CreditAdjustmentEntity;
import cn.bugstack.domain.credit.model.valobj.AdjustTypeVO;
import cn.bugstack.domain.credit.model.valobj.TradeTypeVO;
import cn.bugstack.domain.credit.service.ICreditAdjustmentService;
import cn.bugstack.trigger.api.ICreditService;
import cn.bugstack.trigger.api.dto.CreditAdjustRequestDTO;
import cn.bugstack.trigger.api.dto.CreditQueryResponseDTO;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分服务API控制器
 * @create 2024-05-20 11:30
 */
@Slf4j
@RestController()
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/credit/")
public class CreditController implements ICreditService {
    
    @Resource
    private ICreditAdjustmentService creditAdjustmentService;
    
    @Override
    @RequestMapping(value = "adjust", method = RequestMethod.POST)
    public Response<Boolean> adjustCredit(@RequestBody CreditAdjustRequestDTO request) {
        try {
            log.info("积分调整，开始 request: {}", JSON.toJSONString(request));
            
            // 转换为领域实体
            CreditAdjustmentEntity adjustmentEntity = CreditAdjustmentEntity.builder()
                    .userId(request.getUserId())
                    .adjustType(AdjustTypeVO.valueOf(request.getAdjustType().toUpperCase()))
                    .tradeType(TradeTypeVO.valueOf(request.getTradeType().toUpperCase()))
                    .amount(request.getAmount())
                    .bizId(request.getBizId())
                    .bizDesc(request.getBizDesc())
                    .build();
            
            // 执行积分调整
            boolean result = creditAdjustmentService.adjustCredit(adjustmentEntity);
            
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
            
            log.info("积分调整，完成 request: {}, result: {}", JSON.toJSONString(request), result);
            return response;
            
        } catch (Exception e) {
            log.error("积分调整，失败 request: {}", JSON.toJSONString(request), e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    
    @Override
    @RequestMapping(value = "query", method = RequestMethod.GET)
    public Response<CreditQueryResponseDTO> queryCredit(@RequestParam String userId) {
        try {
            log.info("查询用户积分，开始 userId: {}", userId);
            
            // 查询用户积分账户
            var creditAccount = creditAdjustmentService.queryCreditAccount(userId);
            
            // 转换为响应DTO
            CreditQueryResponseDTO responseDTO = new CreditQueryResponseDTO();
            responseDTO.setUserId(creditAccount.getUserId());
            responseDTO.setTotalCredits(creditAccount.getTotalCredits());
            responseDTO.setAvailableCredits(creditAccount.getAvailableCredits());
            responseDTO.setFrozenCredits(creditAccount.getFrozenCredits());
            
            Response<CreditQueryResponseDTO> response = Response.<CreditQueryResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
            
            log.info("查询用户积分，完成 userId: {}, response: {}", userId, JSON.toJSONString(response));
            return response;
            
        } catch (Exception e) {
            log.error("查询用户积分，失败 userId: {}", userId, e);
            return Response.<CreditQueryResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    
    @Override
    @RequestMapping(value = "freeze", method = RequestMethod.POST)
    public Response<Boolean> freezeCredits(@RequestParam String userId, 
                                           @RequestParam Integer amount, 
                                           @RequestParam String bizId, 
                                           @RequestParam String bizDesc) {
        try {
            log.info("冻结积分，开始 userId: {}, amount: {}, bizId: {}, bizDesc: {}", 
                    userId, amount, bizId, bizDesc);
            
            boolean result = creditAdjustmentService.freezeCredits(userId, amount, bizId, bizDesc);
            
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
            
            log.info("冻结积分，完成 userId: {}, result: {}", userId, result);
            return response;
            
        } catch (Exception e) {
            log.error("冻结积分，失败 userId: {}, amount: {}, bizId: {}", userId, amount, bizId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    
    @Override
    @RequestMapping(value = "unfreeze", method = RequestMethod.POST)
    public Response<Boolean> unfreezeCredits(@RequestParam String userId, 
                                             @RequestParam Integer amount, 
                                             @RequestParam String bizId) {
        try {
            log.info("解冻积分，开始 userId: {}, amount: {}, bizId: {}", userId, amount, bizId);
            
            boolean result = creditAdjustmentService.unfreezeCredits(userId, amount, bizId);
            
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
            
            log.info("解冻积分，完成 userId: {}, result: {}", userId, result);
            return response;
            
        } catch (Exception e) {
            log.error("解冻积分，失败 userId: {}, amount: {}, bizId: {}", userId, amount, bizId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    
    @Override
    @RequestMapping(value = "consume", method = RequestMethod.POST)
    public Response<Boolean> consumeCredits(@RequestParam String userId, 
                                            @RequestParam Integer amount, 
                                            @RequestParam String bizId, 
                                            @RequestParam String bizDesc) {
        try {
            log.info("消费积分，开始 userId: {}, amount: {}, bizId: {}, bizDesc: {}", 
                    userId, amount, bizId, bizDesc);
            
            boolean result = creditAdjustmentService.consumeCredits(userId, amount, bizId, bizDesc);
            
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(result)
                    .build();
            
            log.info("消费积分，完成 userId: {}, result: {}", userId, result);
            return response;
            
        } catch (Exception e) {
            log.error("消费积分，失败 userId: {}, amount: {}, bizId: {}", userId, amount, bizId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    
}
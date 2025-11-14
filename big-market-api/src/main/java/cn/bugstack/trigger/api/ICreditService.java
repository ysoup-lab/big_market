package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.CreditAdjustRequestDTO;
import cn.bugstack.trigger.api.dto.CreditQueryResponseDTO;
import cn.bugstack.types.model.Response;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分服务API接口
 * @create 2024-05-20 11:20
 */
public interface ICreditService {
    
    /**
     * 积分调整接口
     * 
     * @param request 请求对象
     * @return 调整结果
     */
    Response<Boolean> adjustCredit(CreditAdjustRequestDTO request);
    
    /**
     * 查询用户积分接口
     * 
     * @param userId 用户ID
     * @return 积分查询结果
     */
    Response<CreditQueryResponseDTO> queryCredit(String userId);
    
    /**
     * 冻结积分接口
     * 
     * @param userId 用户ID
     * @param amount 冻结金额
     * @param bizId 业务ID
     * @param bizDesc 业务描述
     * @return 冻结结果
     */
    Response<Boolean> freezeCredits(String userId, Integer amount, String bizId, String bizDesc);
    
    /**
     * 解冻积分接口
     * 
     * @param userId 用户ID
     * @param amount 解冻金额
     * @param bizId 业务ID
     * @return 解冻结果
     */
    Response<Boolean> unfreezeCredits(String userId, Integer amount, String bizId);
    
    /**
     * 消费积分接口
     * 
     * @param userId 用户ID
     * @param amount 消费金额
     * @param bizId 业务ID
     * @param bizDesc 业务描述
     * @return 消费结果
     */
    Response<Boolean> consumeCredits(String userId, Integer amount, String bizId, String bizDesc);
    
}
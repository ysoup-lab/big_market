package cn.bugstack.domain.credit.service;

import cn.bugstack.domain.credit.model.entity.CreditAdjustmentEntity;
import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;
import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分调整服务接口
 * @create 2024-05-20 10:20
 */
public interface ICreditAdjustmentService {
    
    /**
     * 创建积分账户
     * 
     * @param userId 用户ID
     * @return 积分账户实体
     */
    CreditAccountEntity createCreditAccount(String userId);
    
    /**
     * 查询积分账户
     * 
     * @param userId 用户ID
     * @return 积分账户实体
     */
    CreditAccountEntity queryCreditAccount(String userId);
    
    /**
     * 积分调整
     * 
     * @param creditAdjustmentEntity 积分调整实体
     * @return 调整结果
     */
    boolean adjustCredit(CreditAdjustmentEntity creditAdjustmentEntity);
    
    /**
     * 查询积分调整记录
     * 
     * @param userId 用户ID
     * @param bizId 业务ID
     * @return 积分调整记录列表
     */
    List<CreditAdjustmentEntity> queryCreditAdjustmentRecords(String userId, String bizId);
    
    /**
     * 冻结积分
     * 
     * @param userId 用户ID
     * @param amount 冻结金额
     * @param bizId 业务ID
     * @param bizDesc 业务描述
     * @return 冻结结果
     */
    boolean freezeCredits(String userId, Integer amount, String bizId, String bizDesc);
    
    /**
     * 解冻积分
     * 
     * @param userId 用户ID
     * @param amount 解冻金额
     * @param bizId 业务ID
     * @return 解冻结果
     */
    boolean unfreezeCredits(String userId, Integer amount, String bizId);
    
    /**
     * 消费积分
     * 
     * @param userId 用户ID
     * @param amount 消费金额
     * @param bizId 业务ID
     * @param bizDesc 业务描述
     * @return 消费结果
     */
    boolean consumeCredits(String userId, Integer amount, String bizId, String bizDesc);
    
}
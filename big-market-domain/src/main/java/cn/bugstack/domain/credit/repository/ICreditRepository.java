package cn.bugstack.domain.credit.repository;

import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;
import cn.bugstack.domain.credit.model.entity.CreditAdjustmentEntity;
import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分仓库接口
 * @create 2024-05-20 10:30
 */
public interface ICreditRepository {
    
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
     * 更新积分账户
     * 
     * @param creditAccountEntity 积分账户实体
     * @return 更新结果
     */
    boolean updateCreditAccount(CreditAccountEntity creditAccountEntity);
    
    /**
     * 记录积分调整
     * 
     * @param creditAdjustmentEntity 积分调整实体
     * @return 记录结果
     */
    boolean recordCreditAdjustment(CreditAdjustmentEntity creditAdjustmentEntity);
    
    /**
     * 查询积分调整记录
     * 
     * @param userId 用户ID
     * @param bizId 业务ID
     * @return 积分调整记录列表
     */
    List<CreditAdjustmentEntity> queryCreditAdjustmentRecords(String userId, String bizId);
    
}
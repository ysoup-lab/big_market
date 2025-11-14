package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;
import cn.bugstack.domain.credit.model.entity.CreditAdjustmentEntity;
import cn.bugstack.domain.credit.model.valobj.AdjustTypeVO;
import cn.bugstack.domain.credit.repository.ICreditRepository;
import cn.bugstack.infrastructure.persistent.dao.ICreditAccountDao;
import cn.bugstack.infrastructure.persistent.dao.ICreditAdjustmentDao;
import cn.bugstack.infrastructure.persistent.po.CreditAccount;
import cn.bugstack.infrastructure.persistent.po.CreditAdjustment;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分仓库实现
 * @create 2024-05-20 11:00
 */
@Slf4j
@Component
public class CreditRepository implements ICreditRepository {
    
    @Resource
    private ICreditAccountDao creditAccountDao;
    @Resource
    private ICreditAdjustmentDao creditAdjustmentDao;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Override
    public CreditAccountEntity createCreditAccount(String userId) {
        try {
            dbRouter.doRouter(userId);
            
            // 生成积分账户ID
            String creditAccountId = RandomStringUtils.randomNumeric(12);
            
            // 创建积分账户PO
            CreditAccount creditAccount = new CreditAccount();
            creditAccount.setCreditAccountId(creditAccountId);
            creditAccount.setUserId(userId);
            creditAccount.setTotalCredits(0);
            creditAccount.setAvailableCredits(0);
            creditAccount.setFrozenCredits(0);
            creditAccount.setCreateTime(new Date());
            creditAccount.setUpdateTime(new Date());
            
            // 插入积分账户
            creditAccountDao.insert(creditAccount);
            
            // 转换为实体对象并返回
            return CreditAccountEntity.builder()
                    .userId(creditAccount.getUserId())
                    .creditAccountId(creditAccount.getCreditAccountId())
                    .totalCredits(creditAccount.getTotalCredits())
                    .availableCredits(creditAccount.getAvailableCredits())
                    .frozenCredits(creditAccount.getFrozenCredits())
                    .createTime(String.valueOf(creditAccount.getCreateTime()))
                    .updateTime(String.valueOf(creditAccount.getUpdateTime()))
                    .build();
                    
        } catch (Exception e) {
            log.error("创建积分账户失败 userId: {}", userId, e);
            throw new AppException(ResponseCode.SYSTEM_ERROR.getCode(), "创建积分账户失败");
        }
    }
    
    @Override
    public CreditAccountEntity queryCreditAccount(String userId) {
        try {
            dbRouter.doRouter(userId);
            
            // 查询积分账户PO
            CreditAccount creditAccount = creditAccountDao.queryByUserId(userId);
            
            if (creditAccount == null) {
                return null;
            }
            
            // 转换为实体对象并返回
            return CreditAccountEntity.builder()
                    .userId(creditAccount.getUserId())
                    .creditAccountId(creditAccount.getCreditAccountId())
                    .totalCredits(creditAccount.getTotalCredits())
                    .availableCredits(creditAccount.getAvailableCredits())
                    .frozenCredits(creditAccount.getFrozenCredits())
                    .createTime(String.valueOf(creditAccount.getCreateTime()))
                    .updateTime(String.valueOf(creditAccount.getUpdateTime()))
                    .build();
                    
        } catch (Exception e) {
            log.error("查询积分账户失败 userId: {}", userId, e);
            throw new AppException(ResponseCode.SYSTEM_ERROR.getCode(), "查询积分账户失败");
        }
    }
    
    @Override
    public boolean updateCreditAccount(CreditAccountEntity creditAccountEntity) {
        try {
            dbRouter.doRouter(creditAccountEntity.getUserId());
            
            // 转换为PO对象
            CreditAccount creditAccount = new CreditAccount();
            creditAccount.setCreditAccountId(creditAccountEntity.getCreditAccountId());
            creditAccount.setUserId(creditAccountEntity.getUserId());
            creditAccount.setTotalCredits(creditAccountEntity.getTotalCredits());
            creditAccount.setAvailableCredits(creditAccountEntity.getAvailableCredits());
            creditAccount.setFrozenCredits(creditAccountEntity.getFrozenCredits());
            creditAccount.setUpdateTime(new Date());
            
            // 更新积分账户
            creditAccountDao.update(creditAccount);
            
            return true;
            
        } catch (Exception e) {
            log.error("更新积分账户失败 creditAccount: {}", JSON.toJSONString(creditAccountEntity), e);
            throw new AppException(ResponseCode.SYSTEM_ERROR.getCode(), "更新积分账户失败");
        }
    }
    
    @Override
    public boolean recordCreditAdjustment(CreditAdjustmentEntity creditAdjustmentEntity) {
        try {
            dbRouter.doRouter(creditAdjustmentEntity.getUserId());
            
            // 生成积分调整ID
            String creditAdjustmentId = RandomStringUtils.randomNumeric(16);
            
            // 转换为PO对象
            CreditAdjustment creditAdjustment = new CreditAdjustment();
            creditAdjustment.setCreditAdjustmentId(creditAdjustmentId);
            creditAdjustment.setUserId(creditAdjustmentEntity.getUserId());
            creditAdjustment.setCreditAccountId(creditAdjustmentEntity.getCreditAccountId());
            creditAdjustment.setAdjustType(creditAdjustmentEntity.getAdjustType().getCode());
            creditAdjustment.setTradeType(creditAdjustmentEntity.getTradeType().getCode());
            creditAdjustment.setAmount(creditAdjustmentEntity.getAmount());
            creditAdjustment.setBeforeAmount(creditAdjustmentEntity.getBeforeAmount());
            creditAdjustment.setAfterAmount(creditAdjustmentEntity.getAfterAmount());
            creditAdjustment.setBizId(creditAdjustmentEntity.getBizId());
            creditAdjustment.setBizDesc(creditAdjustmentEntity.getBizDesc());
            creditAdjustment.setCreateTime(new Date());
            
            // 插入积分调整记录
            creditAdjustmentDao.insert(creditAdjustment);
            
            return true;
            
        } catch (Exception e) {
            log.error("记录积分调整失败 creditAdjustment: {}", JSON.toJSONString(creditAdjustmentEntity), e);
            throw new AppException(ResponseCode.SYSTEM_ERROR.getCode(), "记录积分调整失败");
        }
    }
    
    @Override
    public List<CreditAdjustmentEntity> queryCreditAdjustmentRecords(String userId, String bizId) {
        try {
            dbRouter.doRouter(userId);
            
            // 查询积分调整记录PO
            CreditAdjustment queryPO = new CreditAdjustment();
            queryPO.setUserId(userId);
            queryPO.setBizId(bizId);
            
            List<CreditAdjustment> creditAdjustments = creditAdjustmentDao.queryByUserIdAndBizId(queryPO);
            
            // 转换为实体对象列表并返回
            List<CreditAdjustmentEntity> result = new ArrayList<>();
            for (CreditAdjustment po : creditAdjustments) {
                result.add(CreditAdjustmentEntity.builder()
                        .creditAdjustmentId(po.getCreditAdjustmentId())
                        .userId(po.getUserId())
                        .creditAccountId(po.getCreditAccountId())
                        .adjustType(AdjustTypeVO.valueOf(po.getAdjustType().toUpperCase()))
                        .tradeType(cn.bugstack.domain.credit.model.valobj.TradeTypeVO.valueOf(po.getTradeType().toUpperCase()))
                        .amount(po.getAmount())
                        .beforeAmount(po.getBeforeAmount())
                        .afterAmount(po.getAfterAmount())
                        .bizId(po.getBizId())
                        .bizDesc(po.getBizDesc())
                        .createTime(String.valueOf(po.getCreateTime()))
                        .build());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("查询积分调整记录失败 userId: {}, bizId: {}", userId, bizId, e);
            throw new AppException(ResponseCode.SYSTEM_ERROR.getCode(), "查询积分调整记录失败");
        }
    }
    
}
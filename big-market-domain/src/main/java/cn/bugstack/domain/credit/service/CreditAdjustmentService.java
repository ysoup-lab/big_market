package cn.bugstack.domain.credit.service;

import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;
import cn.bugstack.domain.credit.model.entity.CreditAdjustmentEntity;
import cn.bugstack.domain.credit.model.valobj.AdjustTypeVO;
import cn.bugstack.domain.credit.model.valobj.TradeTypeVO;
import cn.bugstack.domain.credit.repository.ICreditRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分调整服务实现
 * @create 2024-05-20 10:25
 */
@Service
public class CreditAdjustmentService implements ICreditAdjustmentService {
    
    @Resource
    private ICreditRepository creditRepository;
    
    @Override
    public CreditAccountEntity createCreditAccount(String userId) {
        return creditRepository.createCreditAccount(userId);
    }
    
    @Override
    public CreditAccountEntity queryCreditAccount(String userId) {
        return creditRepository.queryCreditAccount(userId);
    }
    
    @Override
    public boolean adjustCredit(CreditAdjustmentEntity creditAdjustmentEntity) {
        // 1. 查询当前积分账户
        CreditAccountEntity creditAccount = creditRepository.queryCreditAccount(creditAdjustmentEntity.getUserId());
        if (creditAccount == null) {
            // 如果积分账户不存在，则创建
            creditAccount = creditRepository.createCreditAccount(creditAdjustmentEntity.getUserId());
        }
        
        // 2. 计算调整后的积分
        Integer afterAmount = creditAccount.getAvailableCredits();
        if (AdjustTypeVO.INCREASE.equals(creditAdjustmentEntity.getAdjustType())) {
            afterAmount += creditAdjustmentEntity.getAmount();
        } else if (AdjustTypeVO.DECREASE.equals(creditAdjustmentEntity.getAdjustType())) {
            if (afterAmount < creditAdjustmentEntity.getAmount()) {
                // 积分不足
                return false;
            }
            afterAmount -= creditAdjustmentEntity.getAmount();
        }
        
        // 3. 更新积分账户
        creditAccount.setAvailableCredits(afterAmount);
        creditAccount.setTotalCredits(afterAmount + creditAccount.getFrozenCredits());
        
        // 4. 记录积分调整
        creditAdjustmentEntity.setBeforeAmount(creditAccount.getAvailableCredits());
        creditAdjustmentEntity.setAfterAmount(afterAmount);
        creditAdjustmentEntity.setCreditAccountId(creditAccount.getCreditAccountId());
        
        // 5. 执行更新和记录
        return creditRepository.updateCreditAccount(creditAccount) && 
               creditRepository.recordCreditAdjustment(creditAdjustmentEntity);
    }
    
    @Override
    public List<CreditAdjustmentEntity> queryCreditAdjustmentRecords(String userId, String bizId) {
        return creditRepository.queryCreditAdjustmentRecords(userId, bizId);
    }
    
    @Override
    public boolean freezeCredits(String userId, Integer amount, String bizId, String bizDesc) {
        // 1. 查询积分账户
        CreditAccountEntity creditAccount = creditRepository.queryCreditAccount(userId);
        if (creditAccount == null || creditAccount.getAvailableCredits() < amount) {
            return false;
        }
        
        // 2. 冻结积分
        creditAccount.setAvailableCredits(creditAccount.getAvailableCredits() - amount);
        creditAccount.setFrozenCredits(creditAccount.getFrozenCredits() + amount);
        
        // 3. 记录积分调整
        CreditAdjustmentEntity adjustmentEntity = CreditAdjustmentEntity.builder()
                .userId(userId)
                .creditAccountId(creditAccount.getCreditAccountId())
                .adjustType(AdjustTypeVO.DECREASE)
                .tradeType(TradeTypeVO.SYSTEM_ADJUST)
                .amount(amount)
                .beforeAmount(creditAccount.getAvailableCredits() + amount)
                .afterAmount(creditAccount.getAvailableCredits())
                .bizId(bizId)
                .bizDesc(bizDesc)
                .build();
        
        return creditRepository.updateCreditAccount(creditAccount) && 
               creditRepository.recordCreditAdjustment(adjustmentEntity);
    }
    
    @Override
    public boolean unfreezeCredits(String userId, Integer amount, String bizId) {
        // 1. 查询积分账户
        CreditAccountEntity creditAccount = creditRepository.queryCreditAccount(userId);
        if (creditAccount == null || creditAccount.getFrozenCredits() < amount) {
            return false;
        }
        
        // 2. 解冻积分
        creditAccount.setFrozenCredits(creditAccount.getFrozenCredits() - amount);
        creditAccount.setAvailableCredits(creditAccount.getAvailableCredits() + amount);
        
        // 3. 记录积分调整
        CreditAdjustmentEntity adjustmentEntity = CreditAdjustmentEntity.builder()
                .userId(userId)
                .creditAccountId(creditAccount.getCreditAccountId())
                .adjustType(AdjustTypeVO.INCREASE)
                .tradeType(TradeTypeVO.SYSTEM_ADJUST)
                .amount(amount)
                .beforeAmount(creditAccount.getAvailableCredits() - amount)
                .afterAmount(creditAccount.getAvailableCredits())
                .bizId(bizId)
                .bizDesc("积分解冻")
                .build();
        
        return creditRepository.updateCreditAccount(creditAccount) && 
               creditRepository.recordCreditAdjustment(adjustmentEntity);
    }
    
    @Override
    public boolean consumeCredits(String userId, Integer amount, String bizId, String bizDesc) {
        // 1. 查询积分账户
        CreditAccountEntity creditAccount = creditRepository.queryCreditAccount(userId);
        if (creditAccount == null || creditAccount.getAvailableCredits() < amount) {
            return false;
        }
        
        // 2. 消费积分
        creditAccount.setAvailableCredits(creditAccount.getAvailableCredits() - amount);
        creditAccount.setTotalCredits(creditAccount.getTotalCredits() - amount);
        
        // 3. 记录积分调整
        CreditAdjustmentEntity adjustmentEntity = CreditAdjustmentEntity.builder()
                .userId(userId)
                .creditAccountId(creditAccount.getCreditAccountId())
                .adjustType(AdjustTypeVO.DECREASE)
                .tradeType(TradeTypeVO.PAYMENT)
                .amount(amount)
                .beforeAmount(creditAccount.getAvailableCredits() + amount)
                .afterAmount(creditAccount.getAvailableCredits())
                .bizId(bizId)
                .bizDesc(bizDesc)
                .build();
        
        return creditRepository.updateCreditAccount(creditAccount) && 
               creditRepository.recordCreditAdjustment(adjustmentEntity);
    }
    
}
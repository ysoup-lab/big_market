package cn.bugstack.infrastructure.repository;

import cn.bugstack.domain.integral.model.aggregate.IntegralAdjustmentAggregate;
import cn.bugstack.domain.integral.model.entity.IntegralAccountEntity;
import cn.bugstack.domain.integral.model.entity.IntegralAdjustmentOrderEntity;
import cn.bugstack.domain.integral.model.valobj.AccountStatusVO;
import cn.bugstack.domain.integral.repository.IIntegralRepository;
import cn.bugstack.infrastructure.persistent.dao.IUserCreditAccountDao;
import cn.bugstack.infrastructure.persistent.po.UserCreditAccount;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Repository
public class IntegralRepository implements IIntegralRepository {

    @Resource
    private IUserCreditAccountDao userCreditAccountDao;

    @Override
    public IntegralAccountEntity queryIntegralAccount(String userId) {
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);

        if (userCreditAccount == null) {
            return null;
        }

        return IntegralAccountEntity.builder()
                .id(userCreditAccount.getId())
                .userId(userCreditAccount.getUserId())
                .totalAmount(userCreditAccount.getTotalAmount())
                .availableAmount(userCreditAccount.getAvailableAmount())
                .accountStatus(AccountStatusVO.valueOf(userCreditAccount.getAccountStatus().toUpperCase()))
                .createTime(userCreditAccount.getCreateTime())
                .updateTime(userCreditAccount.getUpdateTime())
                .build();
    }

    @Override
    public void saveIntegralAdjustmentRecord(IntegralAdjustmentAggregate integralAdjustmentAggregate) {
        // TODO: 实现积分调额记录的保存逻辑
        // 需要更新积分账户和创建调额订单
    }

    @Override
    public List<IntegralAdjustmentOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo) {
        // TODO: 实现根据外部业务号查询调额订单的逻辑
        return Collections.emptyList();
    }
}
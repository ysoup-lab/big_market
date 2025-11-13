package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.credit.repository.IUserCreditRepository;
import cn.bugstack.infrastructure.persistent.dao.IUserCreditAccountDao;
import cn.bugstack.infrastructure.persistent.po.UserCreditAccount;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 用户积分仓储实现
 * @create 2024-05-25 16:00
 */
@Repository
public class UserCreditRepository implements IUserCreditRepository {

    @Resource
    private IUserCreditAccountDao userCreditAccountDao;

    @Override
    public boolean increaseCredit(String userId, Double amount) {
        // 1. 查询用户积分账户
        UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userId);
        if (userCreditAccount == null) {
            // 2. 创建用户积分账户
            userCreditAccount = UserCreditAccount.builder()
                    .userId(userId)
                    .totalAmount(BigDecimal.ZERO)
                    .availableAmount(BigDecimal.ZERO)
                    .accountStatus("open")
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            userCreditAccountDao.insertUserCreditAccount(userCreditAccount);
        }

        // 3. 增加用户积分
        int updateCount = userCreditAccountDao.increaseUserCredit(userId, amount);
        return updateCount > 0;
    }

    @Override
    public Double queryCreditBalance(String userId) {
        UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userId);
        if (userCreditAccount == null) {
            return 0.0;
        }
        return userCreditAccount.getAvailableAmount().doubleValue();
    }

}
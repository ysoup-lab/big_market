package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserCreditAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @description 用户积分账户DAO
 * @create 2024-05-25 14:00
 */
@Mapper
public interface IUserCreditAccountDao {

    /**
     * 查询用户积分账户
     * @param userId 用户ID
     * @return 用户积分账户
     */
    UserCreditAccount queryUserCreditAccount(@Param("userId") String userId);

    /**
     * 新增用户积分账户
     * @param userCreditAccount 用户积分账户
     */
    void insertUserCreditAccount(UserCreditAccount userCreditAccount);

    /**
     * 更新用户积分账户
     * @param userCreditAccount 用户积分账户
     * @return 更新数量
     */
    int updateUserCreditAccount(UserCreditAccount userCreditAccount);

    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param amount 积分数量
     * @return 更新数量
     */
    int increaseUserCredit(@Param("userId") String userId, @Param("amount") Double amount);

}
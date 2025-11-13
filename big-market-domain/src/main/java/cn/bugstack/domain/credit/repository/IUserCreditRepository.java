package cn.bugstack.domain.credit.repository;

/**
 * @description 用户积分仓储接口
 * @create 2024-05-25 16:00
 */
public interface IUserCreditRepository {

    /**
     * 给用户增加积分
     * @param userId 用户ID
     * @param amount 积分数量
     * @return 是否增加成功
     */
    boolean increaseCredit(String userId, Double amount);

    /**
     * 查询用户积分余额
     * @param userId 用户ID
     * @return 积分余额
     */
    Double queryCreditBalance(String userId);

}
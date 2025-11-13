package cn.bugstack.domain.credit.service;

/**
 * @description 用户积分服务接口
 * @create 2024-05-25 16:00
 */
public interface IUserCreditService {

    /**
     * 给用户发放积分
     * @param userId 用户ID
     * @param integralAmount 积分数量
     * @param bizId 业务ID（用于幂等控制）
     * @return 是否发放成功
     */
    boolean grantIntegral(String userId, Double integralAmount, String bizId);

    /**
     * 查询用户积分余额
     * @param userId 用户ID
     * @return 积分余额
     */
    Double queryIntegralBalance(String userId);

}
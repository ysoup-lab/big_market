package cn.bugstack.domain.credit.service;

import cn.bugstack.domain.credit.repository.IUserCreditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @description 用户积分服务实现
 * @create 2024-05-25 16:00
 */
@Slf4j
@Service
public class UserCreditServiceImpl implements IUserCreditService {

    @Resource
    private IUserCreditRepository userCreditRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean grantIntegral(String userId, Double integralAmount, String bizId) {
        // 这里可以添加幂等控制逻辑，比如通过bizId查询是否已经发放过积分
        // 暂时省略幂等控制，假设积分发放是幂等的
        return userCreditRepository.increaseCredit(userId, integralAmount);
    }

    @Override
    public Double queryIntegralBalance(String userId) {
        return userCreditRepository.queryCreditBalance(userId);
    }

}
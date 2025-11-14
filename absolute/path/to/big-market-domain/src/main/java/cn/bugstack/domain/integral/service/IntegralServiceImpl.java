package cn.bugstack.domain.integral.service;

import cn.bugstack.domain.integral.model.aggregate.IntegralAdjustmentAggregate;
import cn.bugstack.domain.integral.model.entity.IntegralAccountEntity;
import cn.bugstack.domain.integral.model.entity.IntegralAdjustmentOrderEntity;
import cn.bugstack.domain.integral.model.valobj.IntegralTypeVO;
import cn.bugstack.domain.integral.repository.IIntegralRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class IntegralServiceImpl implements IIntegralService {

    @Resource
    private IIntegralRepository integralRepository;

    @Override
    public String adjustIntegral(String userId, BigDecimal amount, String type, String description, String outBusinessNo) {
        // 1. 查询用户积分账户
        IntegralAccountEntity integralAccountEntity = integralRepository.queryIntegralAccount(userId);
        
        // 2. 创建积分调额订单
        String orderId = RandomStringUtils.randomNumeric(12);
        IntegralAdjustmentOrderEntity integralAdjustmentOrderEntity = IntegralAdjustmentOrderEntity.builder()
                .orderId(orderId)
                .userId(userId)
                .integralType(IntegralTypeVO.valueOf(type.toUpperCase()))
                .amount(amount)
                .description(description)
                .outBusinessNo(outBusinessNo)
                .createTime(new Date())
                .build();

        // 3. 构建聚合根
        IntegralAdjustmentAggregate integralAdjustmentAggregate = IntegralAdjustmentAggregate.builder()
                .userId(userId)
                .integralAccountEntity(integralAccountEntity)
                .integralAdjustmentOrderEntity(integralAdjustmentOrderEntity)
                .build();

        // 4. 保存积分调额记录
        integralRepository.saveIntegralAdjustmentRecord(integralAdjustmentAggregate);

        // 5. 返回订单ID
        return orderId;
    }

    @Override
    public List<IntegralAdjustmentOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo) {
        return integralRepository.queryOrderByOutBusinessNo(userId, outBusinessNo);
    }
}
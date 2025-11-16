package cn.bugstack.domain.activity.service.partake;

import cn.bugstack.domain.activity.model.entity.*;
import cn.bugstack.domain.activity.model.valobj.OrderStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.RaffleActivitySupport;
import cn.bugstack.domain.activity.service.rule.factory.DefaultActivityChainFactory;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 活动参与服务实现类
 * @create 2024-03-09 10:05
 */
@Service
public class ActivityPartakeService extends RaffleActivitySupport implements IActivityPartakeService {

    public ActivityPartakeService(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        super(activityRepository, defaultActivityChainFactory);
    }

    @Override
    public UserRaffleOrderEntity partakeActivity(PartakeActivityEntity partakeActivityEntity) {
        // 1. 参数校验
        String userId = partakeActivityEntity.getUserId();
        Long activityId = partakeActivityEntity.getActivityId();
        Long sku = partakeActivityEntity.getSku();

        if (StringUtils.isBlank(userId) || null == activityId || null == sku) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2. 查询基础信息
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        ActivityEntity activityEntity = queryRaffleActivityByActivityId(activityId);
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        // 3. 活动动作规则校验 「过滤失败则直接抛异常」
        boolean isPass = defaultActivityChainFactory.openActionChain().action(activitySkuEntity, activityEntity, activityCountEntity);
        if (!isPass) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 4. 扣减用户账户额度
        // 4.1 构建账户扣减实体
        ActivityAccountEntity activityAccountEntity = ActivityAccountEntity.builder()
                .userId(userId)
                .activityId(activityId)
                .totalCount(-1) // 扣减总次数
                .totalCountSurplus(-1) // 扣减总剩余次数
                .dayCount(-1) // 扣减日次数
                .dayCountSurplus(-1) // 扣减日剩余次数
                .monthCount(-1) // 扣减月次数
                .monthCountSurplus(-1) // 扣减月剩余次数
                .build();

        // 4.2 执行账户扣减
        boolean deductResult = activityRepository.deductActivityAccountQuota(activityAccountEntity);
        if (!deductResult) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 5. 生成参与活动的订单流水
        UserRaffleOrderEntity userRaffleOrderEntity = new UserRaffleOrderEntity();
        userRaffleOrderEntity.setOrderId(sku + "_" + System.currentTimeMillis() + "_" + userId.hashCode());
        userRaffleOrderEntity.setUserId(userId);
        userRaffleOrderEntity.setActivityId(activityId);
        userRaffleOrderEntity.setActivityName(activityEntity.getActivityName());
        userRaffleOrderEntity.setTotalCount(activityCountEntity.getTotalCount());
        userRaffleOrderEntity.setDayCount(activityCountEntity.getDayCount());
        userRaffleOrderEntity.setMonthCount(activityCountEntity.getMonthCount());
        userRaffleOrderEntity.setSku(sku);
        userRaffleOrderEntity.setStrategyId(activityEntity.getStrategyId());
        userRaffleOrderEntity.setState(OrderStateVO.completed);
        userRaffleOrderEntity.setOutBusinessNo(null); // 这里可以根据实际业务生成外部业务号

        // 6. 保存订单
        activityRepository.saveUserRaffleOrder(userRaffleOrderEntity);

        // 7. 返回订单信息
        return userRaffleOrderEntity;
    }

}
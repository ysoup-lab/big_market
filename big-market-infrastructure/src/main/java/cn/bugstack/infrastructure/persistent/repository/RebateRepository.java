package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.rebate.model.aggregate.CreateRebateOrderAggregate;
import cn.bugstack.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import cn.bugstack.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.bugstack.domain.rebate.model.valobj.RebateTypeVO;
import cn.bugstack.domain.rebate.repository.IRebateRepository;
import cn.bugstack.domain.task.model.entity.TaskEntity;
import cn.bugstack.infrastructure.persistent.dao.IUserBehaviorRebateOrderDao;
import cn.bugstack.infrastructure.persistent.dao.ITaskDao;
import cn.bugstack.infrastructure.persistent.po.UserBehaviorRebateOrder;
import cn.bugstack.infrastructure.persistent.po.Task;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 返利仓储服务实现
 * @create 2024-04-30 18:00
 */
@Repository
public class RebateRepository implements IRebateRepository {

    @Resource
    private IUserBehaviorRebateOrderDao userBehaviorRebateOrderDao;
    @Resource
    private ITaskDao taskDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IDBRouterStrategy dbRouter;

    @Override
    public void saveUserBehaviorRebateOrder(CreateRebateOrderAggregate createRebateOrderAggregate) {
        // 设置分库分表路由
        dbRouter.doRouter(createRebateOrderAggregate.getUserId());
        
        transactionTemplate.execute(status -> {
            try {
                // 保存用户行为返利订单
                UserBehaviorRebateOrder userBehaviorRebateOrder = UserBehaviorRebateOrder.builder()
                        .userId(createRebateOrderAggregate.getUserBehaviorRebateOrderEntity().getUserId())
                        .orderId(createRebateOrderAggregate.getUserBehaviorRebateOrderEntity().getOrderId())
                        .behaviorType(createRebateOrderAggregate.getUserBehaviorRebateOrderEntity().getBehaviorType().getCode())
                        .rebateDesc(createRebateOrderAggregate.getUserBehaviorRebateOrderEntity().getRebateDesc())
                        .rebateType(createRebateOrderAggregate.getUserBehaviorRebateOrderEntity().getRebateType().getCode())
                        .rebateConfig(createRebateOrderAggregate.getUserBehaviorRebateOrderEntity().getRebateConfig())
                        .bizId(createRebateOrderAggregate.getUserBehaviorRebateOrderEntity().getBizId())
                        .build();
                userBehaviorRebateOrderDao.insert(userBehaviorRebateOrder);
                
                // 保存任务（这里需要根据实际业务逻辑实现，示例中简化处理）
                // taskDao.insert(taskEntity);
                
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException("保存用户行为返利订单失败", e);
            }
        });
    }

    @Override
    public UserBehaviorRebateOrderEntity queryUserBehaviorRebateOrder(String orderId) {
        // 注意：查询时需要设置正确的路由键
        // 这里简化处理，实际应该根据orderId解析出userId或者使用其他路由策略
        UserBehaviorRebateOrder userBehaviorRebateOrder = userBehaviorRebateOrderDao.queryByOrderId(orderId);
        if (userBehaviorRebateOrder == null) {
            return null;
        }
        
        return UserBehaviorRebateOrderEntity.builder()
                .userId(userBehaviorRebateOrder.getUserId())
                .orderId(userBehaviorRebateOrder.getOrderId())
                .behaviorType(BehaviorTypeVO.valueOf(userBehaviorRebateOrder.getBehaviorType().toUpperCase()))
                .rebateDesc(userBehaviorRebateOrder.getRebateDesc())
                .rebateType(RebateTypeVO.valueOf(userBehaviorRebateOrder.getRebateType().toUpperCase()))
                .rebateConfig(userBehaviorRebateOrder.getRebateConfig())
                .bizId(userBehaviorRebateOrder.getBizId())
                .createTime(userBehaviorRebateOrder.getCreateTime())
                .updateTime(userBehaviorRebateOrder.getUpdateTime())
                .build();
    }

    @Override
    public UserBehaviorRebateOrderEntity queryUserBehaviorRebateOrderByBizId(String bizId) {
        // 注意：查询时需要设置正确的路由键
        // 这里简化处理，实际应该根据bizId解析出userId或者使用其他路由策略
        UserBehaviorRebateOrder userBehaviorRebateOrder = userBehaviorRebateOrderDao.queryByBizId(bizId);
        if (userBehaviorRebateOrder == null) {
            return null;
        }
        
        return UserBehaviorRebateOrderEntity.builder()
                .userId(userBehaviorRebateOrder.getUserId())
                .orderId(userBehaviorRebateOrder.getOrderId())
                .behaviorType(BehaviorTypeVO.valueOf(userBehaviorRebateOrder.getBehaviorType().toUpperCase()))
                .rebateDesc(userBehaviorRebateOrder.getRebateDesc())
                .rebateType(RebateTypeVO.valueOf(userBehaviorRebateOrder.getRebateType().toUpperCase()))
                .rebateConfig(userBehaviorRebateOrder.getRebateConfig())
                .bizId(userBehaviorRebateOrder.getBizId())
                .createTime(userBehaviorRebateOrder.getCreateTime())
                .updateTime(userBehaviorRebateOrder.getUpdateTime())
                .build();
    }

    @Override
    public void saveTask(TaskEntity taskEntity) {
        // 设置分库分表路由键
        dbRouter.doRouter(taskEntity.getUserId());
        
        // 构建任务PO
        Task task = Task.builder()
                .userId(taskEntity.getUserId())
                .topic(taskEntity.getTopic())
                .messageId(taskEntity.getMessageId())
                .message(taskEntity.getMessage())
                .state("create")
                .createTime(new java.util.Date())
                .updateTime(new java.util.Date())
                .build();
        
        // 保存任务
        taskDao.insert(task);
    }

}
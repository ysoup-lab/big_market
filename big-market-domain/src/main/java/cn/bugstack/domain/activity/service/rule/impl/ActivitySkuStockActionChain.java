package cn.bugstack.domain.activity.service.rule.impl;

import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.rule.AbstractActionChain;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 商品库存规则节点
 * @create 2024-03-23 10:25
 */
@Slf4j
@Component("activity_sku_stock_action")
public class ActivitySkuStockActionChain extends AbstractActionChain {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-商品库存处理【校验&扣减】开始。");

        // 1. 构建库存相关的Redis Key
        String skuStockKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_KEY + activitySkuEntity.getSku();
        String skuLockKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_LOCK_KEY + activitySkuEntity.getSku();

        // 2. 获取Redis锁
        Object lock = activityRepository.getRedisLock(skuLockKey);
        try {
            // 3. 尝试获取锁，等待时间3秒，锁过期时间10秒
            if (activityRepository.tryRedisLock(lock, 3, 10)) {
                // 4. 校验库存是否充足
                String stockSurplusStr = activityRepository.getRedisValue(skuStockKey);
                if (stockSurplusStr == null) {
                    // 从数据库中加载库存
                    ActivitySkuEntity dbActivitySku = activityRepository.queryActivitySku(activitySkuEntity.getSku());
                    if (dbActivitySku.getStockCountSurplus() <= 0) {
                        log.error("活动SKU库存不足，sku:{}, 剩余库存:{}", activitySkuEntity.getSku(), dbActivitySku.getStockCountSurplus());
                        return false;
                    }
                    // 初始化缓存库存
                    activityRepository.setRedisValue(skuStockKey, String.valueOf(dbActivitySku.getStockCountSurplus()));
                } else {
                    int stockSurplus = Integer.parseInt(stockSurplusStr);
                    if (stockSurplus <= 0) {
                        log.error("活动SKU库存不足，sku:{}，剩余库存:{}", activitySkuEntity.getSku(), stockSurplus);
                        return false;
                    }
                }

                // 5. 使用decr方法扣减库存
                long newStock = activityRepository.decrRedisValue(skuStockKey);
                log.info("活动SKU库存扣减成功，sku:{}, 扣减前库存:{}, 扣减后库存:{}", activitySkuEntity.getSku(), Integer.parseInt(stockSurplusStr), newStock);

                // 6. 处理库存更新消息
                sendMessage(activitySkuEntity.getSku(), newStock);

                // 7. 继续执行下一个责任链
                return next().action(activitySkuEntity, activityEntity, activityCountEntity);
            } else {
                log.error("获取活动SKU库存锁失败，sku:{}", activitySkuEntity.getSku());
                return false;
            }
        } catch (InterruptedException e) {
            log.error("获取活动SKU库存锁中断，sku:{}，异常信息:{}", activitySkuEntity.getSku(), e.getMessage());
            return false;
        } finally {
            // 释放锁
            if (activityRepository.isRedisLockHeldByCurrentThread(lock)) {
                activityRepository.unlockRedisLock(lock);
            }
        }
    }

    private void sendMessage(Long sku, Long surplus) {
        // 库存为0的时候直接更新数据库库存
        if (surplus == 0) {
            activityRepository.updateActivitySkuStock(sku);
            log.info("活动责任链-商品库存处理【库存为0，直接更新数据库库存】 sku: {}", sku);
        }
        // 延迟队列更新趋势库存
        activityRepository.activitySkuStockConsumeSendQueue(sku);
        log.info("活动责任链-商品库存处理【延迟队列更新趋势库存】 sku: {}", sku);
    }

}

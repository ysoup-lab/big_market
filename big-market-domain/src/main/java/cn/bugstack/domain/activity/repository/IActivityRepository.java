package cn.bugstack.domain.activity.repository;

import cn.bugstack.domain.activity.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityCountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 活动仓储接口
 * @create 2024-03-16 10:31
 */
public interface IActivityRepository {

    ActivitySkuEntity queryActivitySku(Long sku);

    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId);

    void doSaveOrder(CreateOrderAggregate createOrderAggregate);

    /**
     * 写入活动SKU库存消费队列
     *
     * @param sku 活动SKU
     */
    void activitySkuStockConsumeSendQueue(Long sku);

    /**
     * 更新活动SKU库存
     *
     * @param sku 活动SKU
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 获取Redis锁
     *
     * @param lockKey 锁的Key
     * @return RLock对象
     */
    Object getRedisLock(String lockKey);

    /**
     * 尝试获取Redis锁
     *
     * @param lock 锁对象
     * @param waitTime 等待时间
     * @param leaseTime 租赁时间
     * @return 是否获取成功
     * @throws InterruptedException
     */
    boolean tryRedisLock(Object lock, long waitTime, long leaseTime) throws InterruptedException;

    /**
     * 释放Redis锁
     *
     * @param lock 锁对象
     */
    void unlockRedisLock(Object lock);

    /**
     * 判断当前线程是否持有Redis锁
     *
     * @param lock 锁对象
     * @return 是否持有锁
     */
    boolean isRedisLockHeldByCurrentThread(Object lock);

    /**
     * 获取Redis缓存中的值
     *
     * @param key 缓存Key
     * @return 缓存值
     */
    String getRedisValue(String key);

    /**
     * 设置Redis缓存中的值
     *
     * @param key 缓存Key
     * @param value 缓存值
     */
    void setRedisValue(String key, String value);

    /**
     * 递减Redis缓存中的值
     *
     * @param key 缓存Key
     * @return 递减后的值
     */
    long decrRedisValue(String key);

}

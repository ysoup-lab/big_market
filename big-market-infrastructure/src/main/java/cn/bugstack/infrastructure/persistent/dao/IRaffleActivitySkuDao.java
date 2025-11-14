package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 商品sku dao
 * @create 2024-03-16 11:04
 */
@Mapper
public interface IRaffleActivitySkuDao {

    RaffleActivitySku queryActivitySku(Long sku);

    @org.apache.ibatis.annotations.Update("UPDATE raffle_activity_sku SET stock_count_surplus = stock_count_surplus - 1 WHERE sku = #{sku} AND stock_count_surplus > 0")
    int updateActivitySkuStock(RaffleActivitySku raffleActivitySku);

}

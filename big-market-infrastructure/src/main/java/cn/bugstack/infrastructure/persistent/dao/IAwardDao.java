package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Award;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @description 奖品DAO
 * @create 2024-05-25 15:00
 */
@Mapper
public interface IAwardDao {

    /**
     * 查询奖品信息
     * @param awardId 奖品ID
     * @return 奖品信息
     */
    Award queryAwardInfo(@Param("awardId") Integer awardId);

}
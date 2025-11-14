package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.CreditAdjustment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分调整记录DAO接口
 * @create 2024-05-20 10:55
 */
@Mapper
public interface ICreditAdjustmentDao {
    
    /**
     * 插入积分调整记录
     * 
     * @param creditAdjustment 积分调整记录PO
     */
    void insert(CreditAdjustment creditAdjustment);
    
    /**
     * 根据用户ID和业务ID查询积分调整记录
     * 
     * @param creditAdjustment 积分调整记录PO
     * @return 积分调整记录列表
     */
    List<CreditAdjustment> queryByUserIdAndBizId(CreditAdjustment creditAdjustment);
    
}
package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.CreditAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 积分账户DAO接口
 * @create 2024-05-20 10:50
 */
@Mapper
public interface ICreditAccountDao {
    
    /**
     * 插入积分账户
     * 
     * @param creditAccount 积分账户PO
     */
    void insert(CreditAccount creditAccount);
    
    /**
     * 根据用户ID查询积分账户
     * 
     * @param userId 用户ID
     * @return 积分账户PO
     */
    CreditAccount queryByUserId(String userId);
    
    /**
     * 更新积分账户
     * 
     * @param creditAccount 积分账户PO
     */
    void update(CreditAccount creditAccount);
    
}
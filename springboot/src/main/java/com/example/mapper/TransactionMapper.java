package com.example.mapper;

import com.example.entity.Forum;
import com.example.entity.transaction;

import java.util.List;

/**
 * 操作records相关数据接口
 */
public interface TransactionMapper {

    /**
     * 新增
     */
    int insert(transaction transaction);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(transaction transaction);

    /**
     * 根据ID查询
     */
    transaction selectById(Integer id);

    /**
     * 查询所有
     */
    List<transaction> selectAll(transaction transaction);

}
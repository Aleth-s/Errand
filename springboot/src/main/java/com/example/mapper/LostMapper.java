package com.example.mapper;

import com.example.entity.lost;
import com.example.entity.transaction;

import java.util.List;

/**
 * 操作records相关数据接口
 */
public interface LostMapper {

    /**
     * 新增
     */
    int insert(lost lost);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(lost lost);

    /**
     * 根据ID查询
     */
    lost selectById(Integer id);

    /**
     * 查询所有
     */
    List<lost> selectAll(lost lost);

}
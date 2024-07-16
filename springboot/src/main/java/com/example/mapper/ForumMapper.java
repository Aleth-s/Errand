package com.example.mapper;

import com.example.entity.Forum;
import com.example.entity.Records;

import java.util.List;

/**
 * 操作records相关数据接口
 */
public interface ForumMapper {

    /**
     * 新增
     */
    int insert(Forum forum);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Forum forum);

    /**
     * 根据ID查询
     */
    Forum selectById(Integer id);

    List<Forum> selectByRole(String role);

    /**
     * 查询所有
     */
    List<Forum> selectAll(Forum forum);

}
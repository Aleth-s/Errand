package com.example.mapper;

import com.example.entity.Forum;
import com.example.entity.Reply;

import java.util.List;

/**
 * 操作records相关数据接口
 */
public interface ReplyMapper {

    /**
     * 新增
     */
    int insert(Reply reply);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Reply reply);

    /**
     * 根据ID查询
     */
    Reply selectById(Integer id);

    /**
     * 查询所有
     */
    List<Reply> selectAll(Reply reply);

}
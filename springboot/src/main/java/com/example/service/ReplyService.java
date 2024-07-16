package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Forum;
import com.example.entity.Reply;
import com.example.mapper.ForumMapper;
import com.example.mapper.ReplyMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收支明细业务处理
 **/
@Service
public class ReplyService {

    @Resource
    private ReplyMapper replyMapper;


    /**
     * 新增
     */
    public void add(Reply reply)
    { reply.setTime(DateUtil.now());

        replyMapper.insert(reply);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        replyMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            replyMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Reply reply) {
        replyMapper.updateById(reply);
    }

    /**
     * 根据ID查询
     */
    public Reply selectById(Integer id) {
        return replyMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Reply> selectAll(Reply reply) {
        return replyMapper.selectAll(reply);
    }

    /**
     * 分页查询
     */
    public PageInfo<Reply> selectPage(Reply reply, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Reply> list = replyMapper.selectAll(reply);
        return PageInfo.of(list);
    }

}
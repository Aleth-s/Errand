package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Forum;
import com.example.entity.Records;
import com.example.mapper.ForumMapper;
import com.example.mapper.RecordsMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 收支明细业务处理
 **/
@Service
public class ForumService {

    @Resource
    private ForumMapper forumMapper;


    /**
     * 新增
     */
    public void add(Forum forum)
    { forum.setTime(DateUtil.now());
        forum.setOtherinfo(0);
        forumMapper.insert(forum);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        forumMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            forumMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Forum forum) {
        forumMapper.updateById(forum);
    }

    /**
     * 根据ID查询
     */
    public Forum selectById(Integer id) {
        return forumMapper.selectById(id);
    }

    public List<Forum> selectByRole(String role) {

        if(role.equals("骑手")) {role="rider";}
        else { role="user";}
        return forumMapper.selectByRole(role);
    }

    /**
     * 查询所有
     */
    public List<Forum> selectAll(Forum forum) {
        return forumMapper.selectAll(forum);
    }

    /**
     * 分页查询
     */
    public PageInfo<Forum> selectPage(Forum forum, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Forum> list = forumMapper.selectAll(forum);
        return PageInfo.of(list);
    }

}
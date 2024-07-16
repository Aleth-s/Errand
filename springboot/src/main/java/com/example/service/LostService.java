package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.User;
import com.example.entity.lost;
import com.example.entity.transaction;
import com.example.mapper.LostMapper;
import com.example.mapper.TransactionMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jdk.nashorn.internal.ir.BlockStatement;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LostService {
    @Resource
    private LostMapper lostMapper;

    @Resource
    private UserService userService;

    /**
     * 新增
     */
    public void add(lost lost)

    {
        lost.setTime(DateUtil.now());
        lost.setPrice(0.0);
        lostMapper.insert(lost);


    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        lostMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            lostMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */

    public void updateById(lost lost) {
        lostMapper.updateById(lost);
    }

    /**
     * 根据ID查询
     */
    public lost selectById(Integer id)
    {
        lost lost = lostMapper.selectById(id);
        User user1 = userService.selectById(lost.getUserId());
        lost.setPost_user(user1);
        User user2 = userService.selectById(lost.getAcceptId());
        lost.setAccept_user(user2);
//        return transactionMapper.selectById(id);
        return lost;
    }

    /**
     * 查询所有
     */
    public List<lost> selectAll(lost lost) {
        return lostMapper.selectAll(lost);
    }

    /**
     * 分页查询
     */
    public PageInfo<lost> selectPage(lost lost, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<lost> list = lostMapper.selectAll(lost);
        return PageInfo.of(list);
    }
}

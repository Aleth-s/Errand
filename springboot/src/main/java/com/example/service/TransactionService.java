package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Forum;
import com.example.entity.User;
import com.example.entity.transaction;
import com.example.mapper.ForumMapper;
import com.example.mapper.TransactionMapper;
import com.example.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javafx.animation.FadeTransition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class TransactionService {
    @Resource
    private TransactionMapper transactionMapper;

    @Resource
    private UserService userService;

    /**
     * 新增
     */
    public void add(transaction transaction)

    {
        transaction.setTime(DateUtil.now());
        transactionMapper.insert(transaction);

    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        transactionMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            transactionMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */

    public void updateById(transaction transcation) {
        transactionMapper.updateById(transcation);
    }

    /**
     * 根据ID查询
     */
    public transaction selectById(Integer id)
    {
        transaction transaction = transactionMapper.selectById(id);
        User user1 = userService.selectById(transaction.getUserId());
        transaction.setPost_user(user1);
        User user2 = userService.selectById(transaction.getAcceptId());
        transaction.setAccept_user(user2);
//        return transactionMapper.selectById(id);
        return transaction;
    }

    /**
     * 查询所有
     */
    public List<transaction> selectAll(transaction transaction) {
        return transactionMapper.selectAll(transaction);
    }

    /**
     * 分页查询
     */
    public PageInfo<transaction> selectPage(transaction transaction, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<transaction> list = transactionMapper.selectAll(transaction);
        return PageInfo.of(list);
    }

}

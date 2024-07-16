package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.example.common.Constants;
import com.example.common.enums.OrderStatusEnum;
import com.example.entity.Account;
import com.example.entity.Comment;
import com.example.entity.Orders;
import com.example.mapper.CommentMapper;
import com.example.utils.RedisUtils;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private OrdersService ordersService;


    @Transactional
    public void add(Comment comment) {
        comment.setTime(DateUtil.now());  // 设置当前的最新的时间
        commentMapper.insert(comment);
        // 更新订单状态
        Integer orderId = comment.getOrderId();
        Orders orders = ordersService.selectById(orderId);
        orders.setStatus(OrderStatusEnum.DONE.getValue());  // 已完成
        ordersService.updateById(orders);

        this.setCache(comment);
    }

    public List<Comment> selectComment(){
        Account currentUser = TokenUtils.getCurrentUser();
//        List<Comment> commentsList = commentMapper.selectComment(currentUser.getId());
//        return commentsList;
        // 先查询缓存
        List<Comment> commentList = RedisUtils.getCacheObject(Constants.REDIS_COMMENT_KEY + currentUser.getId());
        if (CollUtil.isEmpty(commentList)) {  // 以防万一  万一缓存没查询到  从数据库查询下保底
            //  从数据库查询所有的评论信息
            commentList = commentMapper.selectComment(currentUser.getId());
            for (Comment comment : commentList) {
                this.setCache(comment);  // 设置缓存
            }
        }
        return commentList;
    }

    public List<Comment> selectComment2(){
        Account currentUser = TokenUtils.getCurrentUser();
        List<Comment> commentsList = commentMapper.selectComment2(currentUser.getId());
        return commentsList;
    }


    public void deleteById(Integer id) {
        commentMapper.deleteById(id);
    }

    // 更新用户的缓存评论
    public void setCache(Comment comment) {
        // 查询出当前用户的所有评论信息
        List<Comment> commentList = commentMapper.selectUserComment(comment.getUserId());
        List<Comment> acceptCommentList = commentMapper.selectAcceptComment(comment.getAcceptId());
        RedisUtils.setCacheObject(Constants.REDIS_COMMENT_KEY + comment.getUserId(), commentList);  //设置用户缓存
        RedisUtils.setCacheObject(Constants.REDIS_COMMENT_KEY + comment.getAcceptId(), acceptCommentList);  //设置骑手缓存
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.deleteById(id);
        }
    }

    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }


    public  List<Comment>  selectById(Integer id) {

        return commentMapper.selectById(id);
    }

    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(comment);
        return PageInfo.of(list);
    }

}
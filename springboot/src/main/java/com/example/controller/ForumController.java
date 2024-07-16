package com.example.controller;

import com.example.common.Result;
import com.example.entity.Forum;
import com.example.entity.Records;
import com.example.service.ForumService;
import com.example.service.RecordsService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 论坛前端操作接口
 **/
@RestController
@RequestMapping("/forum")
public class ForumController {

    @Resource
    private ForumService forumService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Forum forum) {
        forumService.add(forum);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        forumService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        forumService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Forum forum) {
        forumService.updateById(forum);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Forum forum = forumService.selectById(id);
        return Result.success(forum);
    }

    @GetMapping("/selectByRole/{role}")
    public Result selectByRole(@PathVariable String role) {
        List<Forum> forum = forumService.selectByRole(role);
        return Result.success(forum);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Forum forum) {
        List<Forum> list = forumService.selectAll(forum);
        return Result.success(list);
    }

//    @GetMapping("/selectAll1")
//    public Result selectAll1(Forum forum) {
//        List<Forum> list = forumService.selectAll1(forum);
//        return Result.success(list);
//    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Forum forum,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Forum> page = forumService.selectPage(forum, pageNum, pageSize);
        return Result.success(page);
    }

}
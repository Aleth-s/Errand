package com.example.controller;

import com.example.common.Result;
import com.example.entity.Forum;
import com.example.entity.transaction;
import com.example.service.ForumService;
import com.example.service.TransactionService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
@RestController
@RequestMapping("/transaction")
public class transactionController {

    @Resource
    private TransactionService transactionService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody transaction transaction) {
        transactionService.add(transaction);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        transactionService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        transactionService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody transaction transaction) {
        transactionService.updateById(transaction);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        transaction transaction = transactionService.selectById(id);
        return Result.success(transaction);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(transaction transaction) {
        List<transaction> list = transactionService.selectAll(transaction);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(transaction transaction,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<transaction> page = transactionService.selectPage(transaction, pageNum, pageSize);
        return Result.success(page);
    }
}

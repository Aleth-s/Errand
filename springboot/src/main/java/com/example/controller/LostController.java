package com.example.controller;

import com.example.common.Result;
import com.example.entity.lost;
import com.example.entity.transaction;
import com.example.service.LostService;
import com.example.service.TransactionService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/lost")
public class LostController {

    @Resource
    private LostService lostService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody lost lost) {
        lostService.add(lost);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        lostService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        lostService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody lost lost) {
        lostService.updateById(lost);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        lost lost = lostService.selectById(id);
        return Result.success(lost);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(lost lost) {
        List<lost> list = lostService.selectAll(lost);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(lost lost,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<lost> page = lostService.selectPage(lost, pageNum, pageSize);
        return Result.success(page);
    }
}

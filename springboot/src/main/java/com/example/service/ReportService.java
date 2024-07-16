package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Reply;
import com.example.entity.Report;
import com.example.mapper.ReplyMapper;
import com.example.mapper.ReportMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收支明细业务处理
 **/
@Service
public class ReportService {

    @Resource
    private ReportMapper reportMapper;


    /**
     * 新增
     */
    public void add(Report report)
    { report.setTime(DateUtil.now());

        reportMapper.insert(report);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        reportMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            reportMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Report report) {
        reportMapper.updateById(report);
    }

    /**
     * 根据ID查询
     */
    public Report selectById(Integer id) {
        return reportMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Report> selectAll(Report report) {
        return reportMapper.selectAll(report);
    }

    /**
     * 分页查询
     */
    public PageInfo<Report> selectPage(Report report, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Report> list = reportMapper.selectAll(report);
        return PageInfo.of(list);
    }

}
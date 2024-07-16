package com.example.mapper;

import com.example.entity.Admin;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface AdminMapper {


    int insert(Admin admin);


    int deleteById(Integer id);

    int updateById(Admin admin);


    Admin selectById(Integer id);


    List<Admin> selectAll(Admin admin);

    @Select("select * from admin where username = #{username}")
    Admin selectByUsername(String username);
}
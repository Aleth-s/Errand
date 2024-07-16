package com.example.mapper;

import com.example.entity.Certification;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface CertificationMapper {


    int insert(Certification certification);



    int deleteById(Integer id);


    int updateById(Certification certification);


    Certification selectById(Integer id);


    List<Certification> selectAll(Certification certification);

    Certification selectByUserId(Integer userId);

}
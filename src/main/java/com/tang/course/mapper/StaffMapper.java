package com.tang.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tang.course.pojo.Staff;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
    List<Staff> selectList();

    Staff selectByName(String name);
}

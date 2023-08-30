package com.tang.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tang.course.mapper.StaffMapper;
import com.tang.course.pojo.Staff;
import com.tang.course.service.StaffMapperService;
import org.springframework.stereotype.Service;

@Service
public class StaffMapperServiceImpl extends ServiceImpl<StaffMapper , Staff> implements StaffMapperService {
}

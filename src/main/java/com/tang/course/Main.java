package com.tang.course;

import com.tang.course.service.CourseService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
@MapperScan("com.tang.course.mapper")
public class Main {

    @Resource
    private CourseService courseService;

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void initAll() throws IOException {
//        courseService.scheduleTheUsualShift();
    }
}

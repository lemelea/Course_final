package com.tang.course.test;

import com.tang.course.service.impl.CourseServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;


@SpringBootTest
public class ClassTest {

    @Resource
    private CourseServiceImpl courseService;
    @Test
    public void testScanFiles() throws IOException {
        // 扫描的文件路径
        String path = "C:\\Users\\Administrator\\Desktop\\mode.xlsx";
        // 扫描文件
        courseService.scanFiles(path);
        // 安排大二先让他们分布均匀
        courseService.planToCourseSphone();
        // 把安排的写进值班表
        courseService.adoptionScenarios();
        // 安排大三，如果有的班实在没有大二的，大三就上
        courseService.dfsSortCourseJunior(0 , 0);
        // 把安排的大三写进值班表
        courseService.scheduleJuniorYear();
        // 现在安排剩余还没有安排完的大二 ，大三 和 新来的大一的
        courseService.scheduleEveryoneUp();
        // 最后可能值班人数已经排满了 ， 只能给某一个班安排了
        courseService.finalscheduleEveryoneUp();
        // 显示每一个班对应的人
        courseService.displayResult();
        // 显示最后的安排结果 , 如果显示不是'所有人已经安排完了' ， 说明有人的空闲时间很紧张，只有一天值班有空
        courseService.displaysInformationThatIsNotScheduledLeft();
    }

}

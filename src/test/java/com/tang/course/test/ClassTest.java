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
        // ɨ����ļ�·��
        String path = "C:\\Users\\Administrator\\Desktop\\mode.xlsx";
        // ɨ���ļ�
        courseService.scanFiles(path);
        // ���Ŵ���������Ƿֲ�����
        courseService.planToCourseSphone();
        // �Ѱ��ŵ�д��ֵ���
        courseService.adoptionScenarios();
        // ���Ŵ���������еİ�ʵ��û�д���ģ���������
        courseService.dfsSortCourseJunior(0 , 0);
        // �Ѱ��ŵĴ���д��ֵ���
        courseService.scheduleJuniorYear();
        // ���ڰ���ʣ�໹û�а�����Ĵ�� ������ �� �����Ĵ�һ��
        courseService.scheduleEveryoneUp();
        // ������ֵ�������Ѿ������� �� ֻ�ܸ�ĳһ���ల����
        courseService.finalscheduleEveryoneUp();
        // ��ʾÿһ�����Ӧ����
        courseService.displayResult();
        // ��ʾ���İ��Ž�� , �����ʾ����'�������Ѿ���������' �� ˵�����˵Ŀ���ʱ��ܽ��ţ�ֻ��һ��ֵ���п�
        courseService.displaysInformationThatIsNotScheduledLeft();
    }

}

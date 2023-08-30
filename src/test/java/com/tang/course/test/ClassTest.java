package com.tang.course.test;

import com.tang.course.commons.Course;
import com.tang.course.mapper.StaffMapper;
import com.tang.course.pojo.Staff;
import com.tang.course.service.impl.CourseServiceImpl;
import com.tang.course.utils.Utils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.util.*;

@SpringBootTest
public class ClassTest {


    @Resource
    private StaffMapper staffMapper;
    @Resource
    private CourseServiceImpl courseService;



    @Test
    public void testScanFiles() throws IOException {
        courseService.scanFiles();
//        System.out.println(Course.Junior);
        courseService.dfsSortCourseSophomore(0,0);
        courseService.adoptionScenarios();
        courseService.dfsSortCourseJunior(0 , 0);
        courseService.scheduleJuniorYear();
        courseService.scheduleEveryoneUp();
        courseService.finalscheduleEveryoneUp();
        System.out.println(Course.getCourseMappingStaff.get(0).toString());
    }

    @Test
    public void testMyBatisPlus(){
        Staff staff = staffMapper.selectByName("汤付伟");
        System.out.println("汤付伟");
        System.out.println(staff);
    }

    @Test
    public void testMapper(){
        List<Staff> staff = staffMapper.selectList();
        for(int i = 0 ; i < staff.size() ; i++ ){
            System.out.println(staff.get(i));
        }
    }


    @Test
    public void testNoMapping() throws IOException {
        HashMap<Integer, List<String>> getCourseMappingStaff = Course.getCourseMappingStaff;
        for(Map.Entry<Integer , List<String>> entry : getCourseMappingStaff.entrySet()){
            System.out.print( " 周 " + (entry.getKey() % 7 + 1 ) + " 第 " + (entry.getKey() / 7 + 1 ) + "班 ， 值班学生有 ");
            for(String name : entry.getValue()){
                System.out.print(" " + name);
            }
            System.out.println();
        }
    }

    @Test
    public void testListed(){
        HashMap<Integer, List<String>> courseMappingStaff = new HashMap<>();
        courseMappingStaff.put(1, Arrays.asList("John", "Jane", "Alice"));
        courseMappingStaff.put(2, Arrays.asList("Tom"));
        courseMappingStaff.put(3, Arrays.asList("Mike", "Emily"));

        List<Map.Entry<Integer, List<String>>> sortedList = new ArrayList<>(courseMappingStaff.entrySet());

        sortedList.sort(Comparator.comparingInt(entry -> entry.getValue().size()));

        for (Map.Entry<Integer, List<String>> entry : sortedList) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }


    @Test
    public void testWorkId() throws IOException {
        HashMap<String, Staff> allPeopleInfo = Course.allPeopleInfo;
        for(Map.Entry<String, Staff> entry : allPeopleInfo.entrySet()){
            System.out.println( "姓名 : " + entry.getKey() + " 值班的 id" + entry.getValue().getWorkId());
        }
    }

    @Test
    public void test(){
        System.out.println( 0 % 5 );

//        courseService.initStaff();
//
//        for(Map.Entry<String , Staff> staffEntry : course.getAllPeopleInfo().entrySet()){
//            System.out.println(staffEntry.getValue());
//        }
    }

    @Test
    public void testN() throws IOException {

//        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"));
//
//        SqlSession sqlSession = build.openSession();
//
//        StaffMapper mapper = sqlSession.getMapper(StaffMapper.class);
//
//        List<Staff> staff = mapper.selectList();
//
//        for(Staff staff1 : staff){
//            System.out.println(staff1);
//        }
    }

    @Test
    public void testShulffe(){
        List<Integer> lists = new ArrayList<>();
        lists.add(10);
        lists.add(12);
        lists.add(13);
        lists.add(14);
        lists.add(15);
        Utils.shuffleNumber(lists);
        System.out.println(lists);
    }

    @Test
    public void testMap(){

        Course.noCourseStaffId.put(1 , Arrays.asList("小白" , "小黑"));
        Course.noCourseStaffId.put(2 , Arrays.asList("小红" , "小白" , "小黑"));
        Course.noCourseStaffId.put(3 , Arrays.asList("小白" ));

        List<HashMap.Entry<Integer, List<String>>> sortedList = Course.noCourseStaffId.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                .toList();

        // 输出排序后的结果
        for (HashMap.Entry<Integer, List<String>> entry : sortedList) {
            System.out.println("Key: " + entry.getKey() + ", List: " + entry.getValue());
        }
    }
    
    
    @Test
    public void teststaff(){
//        Staff staff = new Staff( 1 , "汤付伟" , 3 , 2 , new ArrayList<>() , new ArrayList<>());
//        List<Integer> freeTime = staff.getFreeTime();
//        freeTime.add(1);
//        freeTime.add(2);
//        freeTime.add(3);
//        freeTime.add(4);
//        HashMap<Integer , Staff> mapStaff  = new HashMap<>();
//
//
//        freeTime.add(10);
//        mapStaff.put( 1 , staff);
//
//        staff.setId(2);
//
//        System.out.println(mapStaff.get(1));

    }

}

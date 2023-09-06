package com.tang.course.commons;

import com.tang.course.pojo.Staff;
import java.util.HashMap;
import java.util.List;




public class Course {


    /*
     *   用来最后的值班课表 ， 其中Integer代表的是星期几的第几节课，这里采用短位模的思想， List用来存放这一班的人
     * */
    public static final HashMap<Integer, List<String>> getCourseMappingStaff = new HashMap<Integer, List<String>>();


    /*
     *   用来映射每一班对应的人数
     * */
    public static final HashMap<Integer, Integer> getWorkMappingStaffNumber = new HashMap<Integer, Integer>();


    /*
     *   用来映射每一个人的信息
     * */
    public static final HashMap<String, Staff> allPeopleInfo = new HashMap<String, Staff>();

    /*
     *
     *   对应这一班没有课的学生id
     * */
    public static final HashMap<Integer, List<String>> noCourseStaffId = new HashMap<Integer, List<String>>();


    /*
     *   大二
     * */
    public static HashMap<Integer, List<String>> Sophomore = new HashMap<>();

    /*
     *   大三
     * */
    public static HashMap<Integer, List<String>> Junior = new HashMap<>();

}

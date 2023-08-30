package com.tang.course.commons;

import com.tang.course.pojo.Staff;
import org.apache.ibatis.binding.MapperMethod;

import java.util.HashMap;
import java.util.List;




public class Course {


    /*
    *   用来最后的值班课表 ， 其中Integer代表的是星期几的第几节课，这里采用短位模的思想， List用来存放这一班的人
    * */
    public static final HashMap<Integer , List<String>> getCourseMappingStaff = new HashMap<Integer, List<String>>();



    /*
    *   用来映射每一班对应的人数
    * */
    public static final HashMap< Integer , Integer > getWorkMappingStaffNumber = new HashMap<Integer, Integer>();


    /*
    *   用来映射每一个人的信息
    * */
    public static final HashMap<String , Staff> allPeopleInfo = new HashMap<String, Staff>();


    /*
    *
    *   对应这一班没有课的学生id
    * */
    public static final HashMap<Integer , List<String>> noCourseStaffId = new HashMap<Integer, List<String>>();


    /*
    *   用来存放课表的
    *   横轴代表第几班，纵轴代表星期几
    * */
    public static final HashMap<Integer , String > day = new HashMap<Integer, String>();


    /*
    *
    * 周六班要单独进行排
    * */
    public static final HashMap<String , List<Integer>> weekOfSat = new HashMap<>();



    /*
    *
    *   周天单独进行
    *
    * */
    public static final HashMap<String , List<Integer>> weekOfSun = new HashMap<>();
    /*
    *   大二
    * */
    public static HashMap<Integer , List<String> > Sophomore = new HashMap<>();

    /*
    *   大三
    * */
    public static HashMap<Integer , List<String> > Junior = new HashMap<>();

    public static void main(String[] args) {

    }



    @Override
    public String toString() {
        return "Course{" +
                "getCourseMappingStaff=" + getCourseMappingStaff +
                ", getWorkMappingStaffNumber=" + getWorkMappingStaffNumber +
                ", allPeopleInfo=" + allPeopleInfo +
                ", noCourseStaffId=" + noCourseStaffId +
                ", day=" + day +
                '}';
    }


}

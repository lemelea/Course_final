package com.tang.course.utils;

import com.tang.course.commons.Course;
import com.tang.course.pojo.Staff;

import java.util.*;

public class Utils {


    private Utils(){}


    /*
    *   对空闲课表进行洗牌 , 查看哪节课是否有人
    * */
    public static List<String> shuffleString(List<String> stringCourse){
        Collections.shuffle(stringCourse);
        return stringCourse;
    }

    public static List<Integer> shuffleNumber(List<Integer> numberCourse){
        Collections.shuffle(numberCourse);
        return numberCourse;
    }

    /*
    *
    *   判断这班是否已经满人了
    *       couseId     对应的是第几班的 Id
    *       size        对应的是这班已经有多少人了
    *
    *   首先要couseId转换为对应的星期几的第几班
    *   size 和 对应的那一班人数进行对比，大于就不能给他排
    *
    * */
    public static boolean overflow( Integer courseId , Integer size , Integer maxSize ){
        return size <= maxSize;
    }


    /*
    *   判断这一班是否有大二的
    * */

    public static boolean IsHasSophomore(List<String> mappingCourse ){

        if(mappingCourse == null )return false;

        for(String name : mappingCourse){
            Staff staff = Course.allPeopleInfo.get(name);
            if(staff.getGrade() == 2 || staff.getGrade() == 3 )return true;
        }

        return false;
    }

    /*
    *
    *   检查今天是否已经存在值班记录
    *
    * */
    public static boolean checkTodayHasWork(String name , int work){
        if(Objects.isNull(name) || name.length() == 0)return false;
        HashMap<Integer, List<String>> getCourseMappingStaff = Course.getCourseMappingStaff;

        int dayOfTheWeek = Math.abs( work % 7) ;

        for(int i = dayOfTheWeek ; i < 35 ; i += 7 ){

            List<String> names = getCourseMappingStaff.get(i);
            if(Objects.isNull(names))continue;
            if(names.contains(name))return false;

        }
        return true;
    }
    /*
    * 
    *   添加到值班中
    * */
    public static void addGetCourseMappingStaff(Integer workId, String name) {
        Course.getCourseMappingStaff.computeIfAbsent(workId, k -> new ArrayList<>());
        List<String> names = Course.getCourseMappingStaff.get(workId);
        names.add(name);
        theNumberOfShiftsMinusOne(name);
//        System.out.println(Course.allPeopleInfo.get(name));
    }

    /*
    *   是否满课
    * */
    public static boolean fullShift(String name) {
        if(name == null || Course.allPeopleInfo.get(name) == null){
            throw new RuntimeException("这个" + name + "姓名在总数据库和Excel表中的名字对应不上");
        }
        return Course.allPeopleInfo.get(name).getCountWork() > 0;
    }

    /*
    *   给姓名为name 的当天的值班数减去1
    * */
    public static void theNumberOfShiftsMinusOne(String name) {
        if(name == null || Course.allPeopleInfo.get(name) == null){
            throw new RuntimeException("这个" + name + "姓名在总数据库和Excel表中的名字对应不上");
        }
        Course.allPeopleInfo.get(name).setCountWork(Course.allPeopleInfo.get(name).getCountWork() - 1);
    }
    /*
    *   给姓名为name 的当天的值班数加上1
    * */
    public static void theNumberOfShiftsUpOne(String name) {
        if(name == null || Course.allPeopleInfo.get(name) == null){
            throw new RuntimeException("这个" + name + "姓名在总数据库和Excel表中的名字对应不上");
        }
        Course.allPeopleInfo.get(name).setCountWork(Course.allPeopleInfo.get(name).getCountWork() + 1);
    }

    /*
    *   相同的专业和班级 ， 不能值同一个班
    *
    * */

    public static boolean sameProfessionalClass(String name , int workId){
        Course.getCourseMappingStaff.computeIfAbsent(workId, k -> new ArrayList<>());
        List<String> names = Course.getCourseMappingStaff.get(workId);
        for(int i = 0 ; i < names.size() ; i++ ){
            if(Course.allPeopleInfo.get(name).getSubject().equals(names.get(i)))return false;
        }
        return true;
    }
}

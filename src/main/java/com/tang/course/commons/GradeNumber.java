package com.tang.course.commons;

public enum GradeNumber {
    /*
    *   每一个年级对应的值班时间
    *
    * */
    GRADE_ONE(1 , 2),
    GRADE_TWO(2 , 2),
    GRADE_THREE(3 , 1);
    private Integer grade;

    private Integer number;


    GradeNumber(Integer grade, Integer number) {
        this.grade = grade;
        this.number = number;
    }

}

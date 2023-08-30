package com.tang.course.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

import java.util.List;


/*
*   用来保存成员的信息
*
* */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dept_staff")
public class Staff {
    // 成员ID
    @TableId
    private Integer id;
    // 姓名

    @TableField("name")
    private String name;
    // 年级，根据年级进行分段排多少班
    @TableField("grade")
    private Integer grade;
    // 每个职员对应的班
    @TableField("countWork")
    private Integer countWork;
    @TableField("subject")
    private String subject;

    // 它的空闲时间表 ， 可以在这里采用洗牌算法进行给他随机分配值班
    private List<Integer> freeTime;
    // 这个人值哪几班
    private List<Integer> workId;

}

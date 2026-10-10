package com.example.jdbc.entity;

import com.example.jdbc.annotation.Column;
import com.example.jdbc.annotation.Id;
import com.example.jdbc.annotation.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 学生实体类，对应 student 表：
 * Id（学号）、name（姓名）、major（专业）、age（年龄）、
 * enrollDate（入学时间）、graduated（是否毕业）、tuition（学费）。
 * 类属性与表字段的对应关系用注解描述，这就是 ORM 的对象-关系映射。
 */
@Table("student")
public class Student {

    /** 学号（主键，自增） */
    @Id
    @Column("id")
    private Integer id;

    /** 姓名 */
    @Column("name")
    private String name;

    /** 专业 */
    @Column("major")
    private String major;

    /** 年龄 */
    @Column("age")
    private Integer age;

    /** 入学时间 */
    @Column("enroll_date")
    private LocalDate enrollDate;

    /** 是否毕业（true 毕业 / false 未毕业） */
    @Column("graduated")
    private Boolean graduated;

    /** 学费 */
    @Column("tuition")
    private BigDecimal tuition;

    public Student() {
    }

    public Student(String name, String major, Integer age, LocalDate enrollDate, Boolean graduated, BigDecimal tuition) {
        this.name = name;
        this.major = major;
        this.age = age;
        this.enrollDate = enrollDate;
        this.graduated = graduated;
        this.tuition = tuition;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDate getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(LocalDate enrollDate) {
        this.enrollDate = enrollDate;
    }

    public Boolean getGraduated() {
        return graduated;
    }

    public void setGraduated(Boolean graduated) {
        this.graduated = graduated;
    }

    public BigDecimal getTuition() {
        return tuition;
    }

    public void setTuition(BigDecimal tuition) {
        this.tuition = tuition;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", major='" + major + '\'' +
                ", age=" + age +
                ", enrollDate=" + enrollDate +
                ", graduated=" + graduated +
                ", tuition=" + tuition +
                '}';
    }
}

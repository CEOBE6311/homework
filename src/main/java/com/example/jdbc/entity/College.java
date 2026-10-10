package com.example.jdbc.entity;

import com.example.jdbc.annotation.Column;
import com.example.jdbc.annotation.Id;
import com.example.jdbc.annotation.Table;

/**
 * 学院实体类，对应 college 表：id（学院ID）、name（学院名称）、code（学院代码）。
 */
@Table("college")
public class College {

    /** 学院ID（主键，自增） */
    @Id
    @Column("id")
    private Integer id;

    /** 学院名称 */
    @Column("name")
    private String name;

    /** 学院代码 */
    @Column("code")
    private String code;

    public College() {
    }

    public College(String name, String code) {
        this.name = name;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "College{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}

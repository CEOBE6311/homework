package com.example.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表名注解：标注实体类对应的数据库表名。
 * 这是理解 ORM 原理的关键之一：用注解描述"类 -> 表"的映射关系。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    /** 数据库表名 */
    String value();
}

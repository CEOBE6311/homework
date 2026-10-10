package com.example.jdbc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列名注解：标注实体类属性对应的数据库字段名。
 * 若 value 为空，则按"驼峰转下划线"规则自动推导（如 enrollDate -> enroll_date）。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    /** 数据库字段名，空串表示由属性名自动推导 */
    String value() default "";
}

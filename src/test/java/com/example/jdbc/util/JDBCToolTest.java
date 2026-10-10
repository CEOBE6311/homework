package com.example.jdbc.util;

import com.example.jdbc.entity.College;
import com.example.jdbc.entity.Student;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JDBCTool 单元测试：验证五个方法（save / update / delete / getOneById / resultSetToList）。
 * 运行前提：本机 MySQL 已创建 datetest 库及 student、college 表（见 schema.sql）。
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JDBCToolTest {

    @Test
    @Order(1)
    void testSaveAndGetOneById() throws Exception {
        try (Connection connection = JDBCTool.getConnection()) {
            College college = new College("测试学院", "T001");
            int rows = JDBCTool.save(college, connection);
            assertEquals(1, rows, "save 应影响 1 行");
            assertNotNull(college.getId(), "保存后应把数据库自增主键回填到对象");

            College found = JDBCTool.getOneById(String.valueOf(college.getId()), College.class, connection);
            assertNotNull(found, "getOneById 应能查到刚保存的对象");
            assertEquals("测试学院", found.getName());
            assertEquals("T001", found.getCode());
            JDBCTool.delete(found, connection); // 清理测试数据
        }
    }

    @Test
    @Order(2)
    void testResultSetToList() throws Exception {
        try (Connection connection = JDBCTool.getConnection()) {
            Student student = new Student("测试学生", "测试专业", 20, LocalDate.of(2023, 9, 1), false, new BigDecimal("5000.00"));
            JDBCTool.save(student, connection);
            try {
                try (Statement st = connection.createStatement()) {
                    List<Student> list = JDBCTool.resultSetToList(st.executeQuery("SELECT * FROM student"), Student.class);
                    assertTrue(list.stream().anyMatch(s -> "测试学生".equals(s.getName())), "resultSetToList 应包含测试学生");
                    Student s = list.stream().filter(x -> "测试学生".equals(x.getName())).findFirst().orElseThrow();
                    assertEquals("测试专业", s.getMajor());
                    assertEquals(LocalDate.of(2023, 9, 1), s.getEnrollDate());
                    assertEquals(new BigDecimal("5000.00"), s.getTuition());
                }
            } finally {
                JDBCTool.delete(JDBCTool.getOneById(String.valueOf(student.getId()), Student.class, connection), connection);
            }
        }
    }

    @Test
    @Order(3)
    void testUpdate() throws Exception {
        try (Connection connection = JDBCTool.getConnection()) {
            Student student = new Student("待更新学生", "原专业", 19, LocalDate.of(2024, 9, 1), false, new BigDecimal("4000.00"));
            JDBCTool.save(student, connection);
            try {
                student.setMajor("新专业");
                student.setGraduated(true);
                int rows = JDBCTool.update(student, connection);
                assertEquals(1, rows, "update 应影响 1 行");

                Student updated = JDBCTool.getOneById(String.valueOf(student.getId()), Student.class, connection);
                assertEquals("新专业", updated.getMajor());
                assertEquals(Boolean.TRUE, updated.getGraduated());
            } finally {
                JDBCTool.delete(JDBCTool.getOneById(String.valueOf(student.getId()), Student.class, connection), connection);
            }
        }
    }

    @Test
    @Order(4)
    void testDelete() throws Exception {
        try (Connection connection = JDBCTool.getConnection()) {
            Student student = new Student("待删除学生", "删除专业", 18, LocalDate.of(2025, 9, 1), false, new BigDecimal("3000.00"));
            JDBCTool.save(student, connection);
            int rows = JDBCTool.delete(student, connection);
            assertEquals(1, rows, "delete 应影响 1 行");
            assertNull(JDBCTool.getOneById(String.valueOf(student.getId()), Student.class, connection),
                    "删除后按 id 应查不到该对象");
        }
    }
}

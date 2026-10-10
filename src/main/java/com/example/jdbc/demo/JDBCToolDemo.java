package com.example.jdbc.demo;

import com.example.jdbc.entity.College;
import com.example.jdbc.entity.Student;
import com.example.jdbc.util.JDBCTool;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class JDBCToolDemo {

    public static void main(String[] args) throws Exception {
        try (Connection connection = JDBCTool.getConnection()) {

            // 清空表数据
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("TRUNCATE TABLE student");
                statement.executeUpdate("TRUNCATE TABLE college");
            }

            printTitle("步骤1：学生表 新增 3 条记录（JDBCTool.save）");
            Student s1 = new Student("张三", "计算机科学与技术", 20, LocalDate.of(2023, 9, 1), false, new BigDecimal("5800.00"));
            Student s2 = new Student("李四", "软件工程", 21, LocalDate.of(2022, 9, 1), true, new BigDecimal("6200.50"));
            Student s3 = new Student("王五", "人工智能", 19, LocalDate.of(2024, 9, 1), false, new BigDecimal("6600.00"));
            for (Student s : List.of(s1, s2, s3)) {
                int rows = JDBCTool.save(s, connection);
                System.out.println("save 影响行数=" + rows + "，保存对象（主键已回填）：" + s);
            }

            printTitle("步骤2：学生表 查询全部（JDBCTool.resultSetToList）");
            List<Student> students = JDBCTool.resultSetToList(
                    connection.createStatement().executeQuery("SELECT * FROM student"), Student.class);
            students.forEach(System.out::println);

            printTitle("步骤3：学生表 按ID查询（JDBCTool.getOneById, id=" + s2.getId() + "）");
            Student found = JDBCTool.getOneById(String.valueOf(s2.getId()), Student.class, connection);
            System.out.println("查询结果：" + found);

            printTitle("步骤4：学生表 修改（JDBCTool.update）");
            found.setMajor("数据科学与大数据技术");
            found.setGraduated(true);
            found.setTuition(new BigDecimal("6888.88"));
            int rows = JDBCTool.update(found, connection);
            System.out.println("update 影响行数=" + rows + "，更新后对象：" + found);
            System.out.println("再次按ID查询验证：" + JDBCTool.getOneById(String.valueOf(found.getId()), Student.class, connection));

            printTitle("步骤5：学生表 删除（JDBCTool.delete, id=" + s3.getId() + "）");
            rows = JDBCTool.delete(s3, connection);
            System.out.println("delete 影响行数=" + rows + "，删除对象：" + s3);
            System.out.println("删除后剩余记录：");
            JDBCTool.resultSetToList(
                    connection.createStatement().executeQuery("SELECT * FROM student"), Student.class)
                    .forEach(System.out::println);

            printTitle("步骤6：学院表 完整增删改查（JDBCTool 五方法）");
            College c1 = new College("计算机学院", "CS01");
            College c2 = new College("软件学院", "SE02");
            College c3 = new College("人工智能学院", "AI03");
            for (College c : List.of(c1, c2, c3)) {
                System.out.println("save 影响行数=" + JDBCTool.save(c, connection) + "，保存对象（主键已回填）：" + c);
            }
            System.out.println("—— 查询全部 ——");
            JDBCTool.resultSetToList(
                    connection.createStatement().executeQuery("SELECT * FROM college"), College.class)
                    .forEach(System.out::println);

            System.out.println("—— 按ID查询（id=" + c2.getId() + "）——");
            College college = JDBCTool.getOneById(String.valueOf(c2.getId()), College.class, connection);
            System.out.println("查询结果：" + college);

            System.out.println("—— 修改（id=" + c2.getId() + "）——");
            college.setName("网络空间安全学院");
            college.setCode("CS-CYBER");
            rows = JDBCTool.update(college, connection);
            System.out.println("update 影响行数=" + rows + "，更新后：" + college);

            System.out.println("—— 删除（id=" + c3.getId() + "）——");
            rows = JDBCTool.delete(c3, connection);
            System.out.println("delete 影响行数=" + rows + "，删除对象：" + c3);

            System.out.println("—— 学院表最终数据 ——");
            JDBCTool.resultSetToList(
                    connection.createStatement().executeQuery("SELECT * FROM college"), College.class)
                    .forEach(System.out::println);

            printTitle("演示结束：两张表增删改查全部成功！");
        } catch (SQLException e) {
            System.err.println("数据库操作失败：" + e.getMessage());
            throw e;
        }
    }

    private static void printTitle(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}

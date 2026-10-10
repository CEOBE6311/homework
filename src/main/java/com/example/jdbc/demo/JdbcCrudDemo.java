package com.example.jdbc.demo;

import com.example.jdbc.util.JDBCTool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 纯 JDBC 增删改查示例（不使用 JDBCTool）：
 * 演示 JDBC 编程的固定套路 ——
 * 1) 加载驱动并获取 Connection；
 * 2) 用 Statement / PreparedStatement 执行 SQL；
 * 3) 用 ResultSet 遍历查询结果；
 * 4) 关闭资源。
 */
public class JdbcCrudDemo {

    public static void main(String[] args) throws Exception {
        try (Connection connection = JDBCTool.getConnection()) {

            System.out.println("========== 纯 JDBC 增删改查示例（student 表） ==========");

            // 1. 增：PreparedStatement 占位符防止 SQL 注入
            String insertSql = "INSERT INTO student(name, major, age, enroll_date, graduated, tuition) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setString(1, "赵六");
                ps.setString(2, "信息安全");
                ps.setInt(3, 22);
                ps.setDate(4, java.sql.Date.valueOf("2021-9-1"));
                ps.setBoolean(5, true);
                ps.setBigDecimal(6, new java.math.BigDecimal("5500.00"));
                int rows = ps.executeUpdate();
                System.out.println("[增] INSERT 影响行数=" + rows);
            }

            // 2. 查：Statement + ResultSet 遍历
            System.out.println("[查] SELECT * FROM student：");
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM student")) {
                while (rs.next()) {
                    System.out.printf("  id=%d, name=%s, major=%s, age=%d, enroll_date=%s, graduated=%s, tuition=%s%n",
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("major"),
                            rs.getInt("age"),
                            rs.getDate("enroll_date"),
                            rs.getBoolean("graduated"),
                            rs.getBigDecimal("tuition"));
                }
            }

            // 3. 改
            String updateSql = "UPDATE student SET major = ? WHERE name = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setString(1, "网络空间安全");
                ps.setString(2, "赵六");
                System.out.println("[改] UPDATE 影响行数=" + ps.executeUpdate());
            }

            // 4. 删
            String deleteSql = "DELETE FROM student WHERE name = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                ps.setString(1, "赵六");
                System.out.println("[删] DELETE 影响行数=" + ps.executeUpdate());
            }

            System.out.println("========== 纯 JDBC 增删改查演示结束 ==========");
        }
    }
}

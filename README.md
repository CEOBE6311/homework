# JDBCTool —— ORM 原理 + Java 反射 + JDBC 增删改查作业

## 一、作业目标与实现对照

| 作业要求 | 本项目的实现 |
| --- | --- |
| 理解 ORM 基本原理 | 自定义 `@Table` / `@Column` / `@Id` 注解描述"类→表、属性→字段"的映射，`JDBCTool` 自动完成"对象↔记录"的转换 |
| Java 反射在对象创建与属性赋值中的应用 | `JDBCTool.resultSetToList()` 用 `clazz.getDeclaredConstructor().newInstance()` 创建对象，`field.setAccessible(true)` + `field.set()` 给私有属性赋值；`save/update/delete` 用反射读出属性值并自动拼 SQL |
| 用 JDBC 进行数据库增删改查 | `JdbcCrudDemo` 演示纯 JDBC 的 Statement / PreparedStatement / ResultSet 用法；`JDBCTool` 底层全部基于 JDBC |
| JDBCTool 五个方法 | 见下文"核心方法" |
| 数据库（含 student、college 两张表） | `src/main/resources/schema.sql`，库名 datetest（即作业中的 DateTest，MySQL Windows 下不区分大小写） |
| 用 JDBCTool 对两张表增删改查 | `JDBCToolDemo` 完整演示两张表的 save / update / delete / getOneById / resultSetToList |
| Maven 构建 | `pom.xml`（mysql-connector-j 8.4.0 + JUnit 5，JDK 21） |
| Git 版本管理 | 本仓库全部提交历史 |
| 运行效果图 | 见下文"运行效果"及 `docs/运行效果` |

## 二、项目结构

```
untitled5
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/example/jdbc
    │   │   ├── annotation        # @Table / @Column / @Id 三个 ORM 映射注解
    │   │   ├── entity            # Student 学生实体、College 学院实体
    │   │   ├── util/JDBCTool.java# 核心工具类（5 个方法 + 辅助方法）
    │   │   └── demo              # JDBCToolDemo（两表增删改查演示）、JdbcCrudDemo（纯 JDBC 演示）
    │   └── resources
    │       ├── db.properties     # 数据库连接配置
    │       └── schema.sql        # 建库建表脚本
    ├── test/java/.../JDBCToolTest.java   # JUnit 5 单元测试
    └── docs/运行效果/           # 运行输出与数据库状态（用于截图）
```

## 三、核心方法（JDBCTool）

| 方法 | 作用 | 生成的 SQL |
| --- | --- | --- |
| `<T> List<T> resultSetToList(ResultSet rs, Class<T> clazz)` | 查询：结果集 → 对象列表 | `SELECT ...` |
| `<T> int save(T obj, Connection connection)` | 增（保存后把自增主键回填到对象） | `INSERT INTO 表(字段...) VALUES(?,...)` |
| `<T> int update(T obj, Connection connection)` | 改 | `UPDATE 表 SET 字段=? ... WHERE id=?` |
| `<T> int delete(T obj, Connection connection)` | 删 | `DELETE FROM 表 WHERE id=?` |
| `<T> T getOneById(String id, Class<T> clazz, Connection connection)` | 按主键查一个对象 | `SELECT * FROM 表 WHERE id=?` |

辅助方法：`getConnection()`（读 db.properties 建连接）、`close(...)`（统一关闭资源）、
`tableName/columnName/idField/columnFields`（反射读取注解得到映射元数据）、
`bind/bindValue`（按属性类型绑定占位符）、`readColumnValue`（结果集值转 Java 类型）、
`toSnakeCase`（驼峰转下划线，如 enrollDate → enroll_date）。

### 反射的应用点

1. **创建对象**：`clazz.getDeclaredConstructor().newInstance()` —— 不知道具体类型也能实例化；
2. **属性赋值**：`Field.setAccessible(true)` 突破 private，`field.set(obj, value)` 把数据库字段值写入对象；
3. **读取映射元数据**：`clazz.getAnnotation(Table.class)`、`field.getAnnotation(Column.class)` 得到表名/字段名；
4. **自动拼 SQL**：save/update/delete 通过反射遍历字段生成列名列表、SET 子句和 `?` 占位符；
5. **类型转换**：根据 `Field.getType()` 决定调用 `setInt/setString/setDate...` 以及 `getInt/getBigDecimal...`。

## 四、数据库设计（库名 datetest）

**student 学生表**：id（主键自增）、name、major、age、enroll_date（入学时间）、graduated（是否毕业）、tuition（学费）

**college 学院表**：id（主键自增）、name、code

## 五、运行方法

前置条件：本机 MySQL 8.x 已启动，root 密码与 `db.properties` 一致（本作业为 [removed]）。

1. 建库建表（先执行一次）：`mysql -u root -p < src/main/resources/schema.sql`
2. Maven 编译：`mvn clean compile`
3. 运行两表增删改查演示：
   `mvn compile exec:java -Dexec.mainClass="com.example.jdbc.demo.JDBCToolDemo"`
4. 运行纯 JDBC 演示：
   `mvn compile exec:java -Dexec.mainClass="com.example.jdbc.demo.JdbcCrudDemo"`
5. 单元测试：`mvn test`

> IntelliJ IDEA 中可直接右键运行 `JDBCToolDemo` / `JdbcCrudDemo` / `JDBCToolTest`。
> 本机未安装独立 Maven 时，可用 IDEA 自带的 Maven（H:\IntelliJ IDEA 2025.2.1\plugins\maven\lib\maven3）。

## 六、运行效果

**JDBCToolDemo（两表完整增删改查）实际运行输出：**

```
========== 步骤1：学生表 新增 3 条记录（JDBCTool.save） ==========
save 影响行数=1，保存对象（主键已回填）：Student{id=1, name='张三', major='计算机科学与技术', age=20, enrollDate=2023-09-01, graduated=false, tuition=5800.00}
save 影响行数=1，保存对象（主键已回填）：Student{id=2, name='李四', major='软件工程', age=21, enrollDate=2022-09-01, graduated=true, tuition=6200.50}
save 影响行数=1，保存对象（主键已回填）：Student{id=3, name='王五', major='人工智能', age=19, enrollDate=2024-09-01, graduated=false, tuition=6600.00}

========== 步骤2：学生表 查询全部（JDBCTool.resultSetToList） ==========
Student{id=1, name='张三', major='计算机科学与技术', age=20, enrollDate=2023-09-01, graduated=false, tuition=5800.00}
Student{id=2, name='李四', major='软件工程', age=21, enrollDate=2022-09-01, graduated=true, tuition=6200.50}
Student{id=3, name='王五', major='人工智能', age=19, enrollDate=2024-09-01, graduated=false, tuition=6600.00}

========== 步骤3：学生表 按ID查询（JDBCTool.getOneById, id=2） ==========
查询结果：Student{id=2, name='李四', major='软件工程', age=21, enrollDate=2022-09-01, graduated=true, tuition=6200.50}

========== 步骤4：学生表 修改（JDBCTool.update） ==========
update 影响行数=1，更新后对象：Student{id=2, name='李四', major='数据科学与大数据技术', age=21, enrollDate=2022-09-01, graduated=true, tuition=6888.88}
再次按ID查询验证：Student{id=2, name='李四', major='数据科学与大数据技术', age=21, enrollDate=2022-09-01, graduated=true, tuition=6888.88}

========== 步骤5：学生表 删除（JDBCTool.delete, id=3） ==========
delete 影响行数=1，删除对象：Student{id=3, name='王五', major='人工智能', age=19, enrollDate=2024-09-01, graduated=false, tuition=6600.00}
删除后剩余记录：
Student{id=1, name='张三', major='计算机科学与技术', age=20, enrollDate=2023-09-01, graduated=false, tuition=5800.00}
Student{id=2, name='李四', major='数据科学与大数据技术', age=21, enrollDate=2022-09-01, graduated=true, tuition=6888.88}

========== 步骤6：学院表 完整增删改查（JDBCTool 五方法） ==========
save 影响行数=1，保存对象（主键已回填）：College{id=1, name='计算机学院', code='CS01'}
save 影响行数=1，保存对象（主键已回填）：College{id=2, name='软件学院', code='SE02'}
save 影响行数=1，保存对象（主键已回填）：College{id=3, name='人工智能学院', code='AI03'}
—— 查询全部 ——
College{id=1, name='计算机学院', code='CS01'}
College{id=2, name='软件学院', code='SE02'}
College{id=3, name='人工智能学院', code='AI03'}
—— 按ID查询（id=2）——
查询结果：College{id=2, name='软件学院', code='SE02'}
—— 修改（id=2）——
update 影响行数=1，更新后：College{id=2, name='网络空间安全学院', code='CS-CYBER'}
—— 删除（id=3）——
delete 影响行数=1，删除对象：College{id=3, name='人工智能学院', code='AI03'}
—— 学院表最终数据 ——
College{id=1, name='计算机学院', code='CS01'}
College{id=2, name='网络空间安全学院', code='CS-CYBER'}

========== 演示结束：两张表增删改查全部成功！ ==========
```

**JdbcCrudDemo（纯 JDBC 增删改查）实际运行输出：**

```
========== 纯 JDBC 增删改查示例（student 表） ==========
[增] INSERT 影响行数=1
[查] SELECT * FROM student：
  id=1, name=张三, major=计算机科学与技术, age=20, enroll_date=2023-09-01, graduated=false, tuition=5800.00
  id=2, name=李四, major=数据科学与大数据技术, age=21, enroll_date=2022-09-01, graduated=true, tuition=6888.88
  id=7, name=赵六, major=信息安全, age=22, enroll_date=2021-09-01, graduated=true, tuition=5500.00
[改] UPDATE 影响行数=1
[删] DELETE 影响行数=1
========== 纯 JDBC 增删改查演示结束 ==========
```

**数据库最终状态（mysql 命令行查询）：**

```
===== 2. datetest 库中的表 =====
Tables_in_datetest
college
student

===== 3. student 学生表（最终数据） =====
id	name	major	age	enroll_date	graduated	tuition
1	张三	计算机科学与技术	20	2023-09-01	0	5800.00
2	李四	数据科学与大数据技术	21	2022-09-01	1	6888.88

===== 4. college 学院表（最终数据） =====
id	name	code
1	计算机学院	CS01
2	网络空间安全学院	CS-CYBER
```

**单元测试（mvn test）：`Tests run: 4, Failures: 0, Errors: 0, Skipped: 0` —— 全部通过。**

> 完整运行输出已保存在 `docs/运行效果/` 目录（JDBCToolDemo运行输出.txt、JdbcCrudDemo运行输出.txt、数据库状态.txt、mvn-test输出.txt），可直接截图作为作业运行效果图。

## 七、Git 版本管理

- 本地仓库：本项目目录（main 分支）
- 远程（本地裸/镜像仓库）：`D:\GIThub\homework`（URL：`D:\GIThub\homework`）
- 提交历史：`git log --oneline`

如需推送到 GitHub/Gitee（在线 URL）：

```bash
git remote add origin https://github.com/<你的用户名>/homework.git
git push -u origin main
```

## 八、注意事项

- `db.properties` 中的密码为本机开发密码，仅用于作业演示；正式项目请改用环境变量或加密配置。
- 演示程序启动时会清空两张表再插入，保证运行效果可重复。

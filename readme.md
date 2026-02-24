# xtr-db 框架

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-8+-blue.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![Spring Boot](https://img.shields.io/badge/spring--boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)

## 📖 项目简介

xtr-db 是一个基于 Spring Boot 和 Hutool 的轻量级数据库访问框架，采用单层架构设计理念，专注于简化数据库操作。该框架通过 IData 和 IDataset 抽象数据操作，通过 BaseDao 封装数据库访问细节，为上层业务提供统一的数据访问接口。

## 🚀 核心特性

- **单层架构设计**: 逆主流分层思想，将业务逻辑集中在 Controller 层
- **多数据源支持**: 支持 MySQL、PostgreSQL、SQL Server 等多种数据库
- **自动化事务管理**: 通过拦截器实现全局事务控制
- **灵活数据转换**: 支持驼峰命名与下划线命名互转
- **高度封装**: 简化复杂的数据库操作接口
- **弱对象存储**: 统一使用 IData 和 IDataset 进行数据传输

## 🛠️ 技术栈

### 后端技术
- **Java 8+**
- **Spring Boot 2.7.18**
- **Undertow** (Web容器)
- **Hutool 5.8.10** (工具库)
- **MyBatis** (可选ORM)
- **Lombok 1.18.30**
- **FastJSON 1.2.78**

### 数据库支持
- **MySQL 8.0.33**
- **PostgreSQL 42.7.3**
- **SQL Server** (通过配置)
- **Oracle** (通过配置)

## 📁 项目结构

```
xtr-db/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/xtr/framework/
│       │       ├── base/           # 基础组件
│       │       │   ├── controller/ # 基础控制器
│       │       │   ├── domain/     # 基础领域对象
│       │       │   └── exception/  # 全局异常处理
│       │       ├── common/         # 通用工具
│       │       │   ├── annotation/ # 注解定义
│       │       │   ├── constant/   # 常量定义
│       │       │   ├── enums/      # 枚举类型
│       │       │   ├── text/       # 文本处理
│       │       │   └── utils/      # 工具类
│       │       ├── config/         # 配置类
│       │       ├── hutool/         # 核心数据库模块
│       │       │   ├── BaseDao.java     # 数据访问对象
│       │       │   ├── BaseDb.java      # 数据库操作类
│       │       │   ├── IData.java       # 数据传输对象
│       │       │   ├── IDataset.java    # 数据集对象
│       │       │   ├── SQLParser.java   # SQL解析器
│       │       │   └── ChangeBean.java  # 数据转换工具
│       │       ├── manager/        # 管理器
│       │       ├── queue/          # 消息队列
│       │       ├── security/       # 安全配置
│       │       └── utils/          # 业务工具类
│       └── resources/
│           ├── db.setting          # 数据库配置文件
│           └── application.yml     # Spring Boot配置
├── pom.xml                         # Maven配置
└── README.md                       # 项目说明
```

## 🔧 核心组件

### 数据访问层

#### IData & IDataset
```java
// 数据传输对象
IData data = new IData();
data.set("name", "张三");
data.set("age", 25);

// 数据集对象
IDataset dataset = new IDataset();
dataset.add(data);
```

#### BaseDao
```java
// 获取DAO实例
BaseDao dao = BaseDao.getDao("mysql");

// 查询操作
IData result = dao.queryByFirst("SELECT * FROM user WHERE id = ?", userId);
IDataset list = dao.queryList("SELECT * FROM user");

// 插入操作
data.setTableName("user");
dao.insert(data);

// 更新操作
dao.update(data, new IData().set("id", userId));
```

### 控制器层

#### BaseController
```java
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {
    
    @GetMapping("/{id}")
    public AjaxResult getUser(@PathVariable Long id) {
        BaseDao dao = BaseDao.getDao("");
        IData user = dao.queryByFirst("SELECT * FROM user WHERE id = ?", id);
        return success(user);
    }
}
```

## ⚙️ 配置说明

### 数据库配置

#### db.setting 配置文件
```properties
# MySQL配置
[url]
jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai

[user]
your_username

[pass]
your_password

[driver]
com.mysql.cj.jdbc.Driver
```

## 🚀 快速开始

### 1. 环境准备
- Java 8+
- Maven 3.6+
- MySQL/PostgreSQL 数据库

### 2. 项目构建
```bash
# 克隆项目
git clone https://github.com/your-username/xtr-db.git

cd xtr-db

# 编译项目
mvn clean compile

# 打包项目
mvn package
```

### 3. 数据库配置
编辑 `src/main/resources/config/db.setting` 文件，配置你的数据库连接信息。

### 4. 运行项目
```bash
# 运行项目
mvn spring-boot:run

# 或者运行打包的jar文件
java -jar target/xtr-db-1.3.jar
```

## 💡 使用示例

### 基本查询操作
```java
@RestController
public class ExampleController extends BaseController {
    
    // 查询单条记录
    @GetMapping("/user/{id}")
    public AjaxResult getUser(@PathVariable Long id) {
        BaseDao dao = BaseDao.getDao("");
        IData user = dao.queryByFirst("SELECT * FROM user WHERE id = ?", id);
        return success(user);
    }
    
    // 查询列表
    @GetMapping("/users")
    public AjaxResult getUsers() {
        BaseDao dao = BaseDao.getDao("");
        IDataset users = dao.queryList("SELECT * FROM user");
        return success(users);
    }
    
    // 分页查询
    @GetMapping("/users/page")
    public AjaxResult getUsersPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        BaseDao dao = BaseDao.getDao("");
        Pagination pagination = new Pagination(page, size);
        IDataset users = dao.queryPage("SELECT * FROM user", new IData(), pagination);
        return success(users);
    }
}
```

### 数据操作
```java
// 插入数据
@PostMapping("/user")
public AjaxResult createUser(@RequestBody IData userData) {
    BaseDao dao = BaseDao.getDao("");
    userData.setTableName("user");
    int result = dao.insert(userData);
    return toAjax(result);
}

// 更新数据
@PutMapping("/user/{id}")
public AjaxResult updateUser(@PathVariable Long id, @RequestBody IData userData) {
    BaseDao dao = BaseDao.getDao("");
    userData.set("id", id);
    userData.setTableName("user");
    int result = dao.updateById(userData);
    return toAjax(result);
}

// 删除数据
@DeleteMapping("/user/{id}")
public AjaxResult deleteUser(@PathVariable Long id) {
    BaseDao dao = BaseDao.getDao("");
    int result = dao.delete("user", "id", id);
    return toAjax(result);
}
```

### 事务管理
```java
@PostMapping("/transfer")
public AjaxResult transferMoney(@RequestBody TransferRequest request) {
    BaseDao dao = BaseDao.getDao("");
    
    try {
        // 扣款
        dao.update(new IData().setTableName("account")
                  .set("balance", "balance - ?")
                  .set("id", request.getFromAccountId()),
                  new IData().set("amount", request.getAmount()));
        
        // 入账
        dao.update(new IData().setTableName("account")
                  .set("balance", "balance + ?")
                  .set("id", request.getToAccountId()),
                  new IData().set("amount", request.getAmount()));
        
        dao.commit();
        return success("转账成功");
    } catch (Exception e) {
        dao.rollback();
        return error("转账失败: " + e.getMessage());
    }
}
```

## 📚 API 文档

### 返回结果格式
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### 常用方法

| 方法 | 说明 | 返回类型 |
|------|------|----------|
| `success()` | 返回成功结果 | AjaxResult |
| `error()` | 返回错误结果 | AjaxResult |
| `toAjax(int rows)` | 根据影响行数返回结果 | AjaxResult |
| `getData()` | 获取请求数据 | IData |
| `getParam()` | 获取请求参数 | String |

## 🤝 贡献指南

我们欢迎任何形式的贡献！

### 贡献步骤
1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 开发规范
- 遵循 Java 编码规范
- 编写单元测试
- 更新相关文档
- 保持代码简洁

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 项目主页: [GitHub Repository](https://github.com/your-username/xtr-db)
- 问题反馈: [Issues](https://github.com/your-username/xtr-db/issues)
- 邮箱: your-email@example.com

---

**如果你觉得这个项目有用，请给它一个⭐️！**

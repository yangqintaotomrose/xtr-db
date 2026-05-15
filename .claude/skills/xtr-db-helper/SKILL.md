---
name: xtr-db-helper
description: 在编写、调试或扩展基于 xtr-db 框架（com.xtr:xtr-db）的代码时使用。适用于：编辑使用 BaseDao / IData / IDataset / AjaxResult / BaseController / SQLParser / Pagination / ChangeBean 的 Java 文件；新增 Controller 或 DAO 调用；处理 DaoInterceptor 事务问题；选择 BaseEntity / StringUtils 两个版本之一；写多数据源切换；调试 ThreadLocal 泄漏；理解 autoCommit 与拦截器 commit/rollback 的关系。本 skill 跟随项目，仅在 xtr-db 项目或其消费方项目中生效。
---

# xtr-db 框架开发助手

`xtr-db` 是一个 Spring Boot 2.7 **库**（不是 runnable 应用），目标 Java 8，通过 `com.xtr.framework.*` 包扫描为宿主应用提供 DAO / Controller 基础设施。**没有 service 层**：业务逻辑写在 Controller 中，直接调 `BaseDao`。

> ⚠️ 编辑代码前先确认：你在编辑的是「xtr-db 库本身」还是「使用了 xtr-db 的消费方项目」。库本身要保持 API 稳定；消费方代码可以自由调用。

---

## 一、DAO 调用速查（最常用）

```java
// 获取（线程内复用，请求级）：
BaseDao dao = BaseDao.getDao("");  // "" = 默认数据源；其它字符串 = 命名数据源

// 查询：
IData    one    = dao.queryByFirst("select * from t where id = :id", new IData().set("id", 1));
IDataset list   = dao.queryList   ("select * from t where status = :s", new IData().set("s", "A"));
IDataset paged  = dao.queryPage   ("select * from t where status = :s",
                                   new IData().set("s", "A"),
                                   new Pagination(1, 20));  // 1-based 页码！

// 写入：
IData row = new IData("{\"name\":\"x\"}");  // 或 new IData().set("name", "x")
row.setTableName("t");                       // ⚠️ insert/update 前必须设置表名
dao.insert(row);
dao.insert("t", row);                        // 也可以传表名
dao.insertBatch("t", dataset, 500);          // 分批，避免内存爆
long id = dao.insertExt(row);                // 返回自增主键

dao.update(row, new IData().set("id", 1));   // 显式 where
dao.updateById(row);                         // 用 row 里的 id 字段作为 where

// 元数据过滤插入/更新（自动剔除表中不存在的列）：
dao.insertByExistField("t", anyData);
dao.updateByIdExistField("t", anyDataWithId);  // 内部会自动 remove("id")

dao.delete("t", "id", 1);
dao.execSql("update t set status = ? where id = ?", "X", 1);  // 原生 SQL，? 占位
dao.executeBatch(sql1, sql2, sql3);
```

**命名规范**（新增方法时请保持一致）：
- 单条 → `queryByFirst`；列表 → `queryList`；分页 → `queryPage`
- 单插 → `insert`；批量 → `inserts` / `insertBatch`
- 全字段 → `update` / `updateById`；按表字段过滤 → `*ByExistField`
- 删 → `delete`
- 原生 SQL → `execSql`（新代码用）/ `executeUpdate`（兼容老项目，等价）

---

## 二、参数化 SQL 与 SQLParser

**绝不要用字符串拼接用户输入构造 SQL**。两种安全写法：

1. **命名占位符** `:key`（推荐，配合 IData）：
   ```java
   IData cond = new IData().set("uid", userId).set("st", "A");
   dao.queryList("select * from t where user_id = :uid and status = :st", cond);
   ```
   `SQLParser` 会扫描 `:name`，跳过单引号字符串内的伪占位符。
   **陷阱**：cond 里没有对应 key 时，`SQLParser.addSQL` 会直接 return，SQL 片段被静默丢弃 —— 缺参时显式校验。

2. **`?` 占位**（用 `execSql`）：
   ```java
   dao.execSql("update t set name = ? where id = ?", name, id);
   ```

---

## 三、事务模型（重要！）

事务由 `DaoInterceptor` 在请求结束时统一处理，**不要乱加 `@Transactional`**：

```
请求进入 → Controller 调 BaseDao.getDao(ds) → 实例存入 ThreadLocal<HashMap>
       → 业务逻辑（多次 DAO 调用复用同一连接）
       → Controller 返回 / 抛异常
DaoInterceptor.afterCompletion:
    无异常且未 setAttribute("_exception", …) → 遍历 ThreadLocal 中所有 BaseDao.commit()
    有异常 → 全部 rollback()
```

**已知不一致**：`BaseDb` 强制 `setAutoCommit(false)`（L147），但 `db.setting` 写着 `autoCommit = true`。以连接层为准：**实际是不自动提交，依赖拦截器**。

**非 HTTP 场景**（定时任务 / 消费 MessageQueue / 工具脚本）：拦截器不会触发，**必须手动**：
```java
BaseDao dao = new BaseDao("");   // 注意：脚本里用 new，不用 getDao（避免污染 ThreadLocal）
try {
    // ... 业务
    dao.commit();
} catch (Exception e) {
    dao.rollback();
    throw e;
}
```

**ThreadLocal 不清理的陷阱**：`afterCompletion` 调完 commit/rollback 后并不 `remove()` ThreadLocal。Servlet 容器线程复用时下一次请求的 `BaseDao.getDao()` 会拿到同一个对象（连接已重置，OK），但 HashMap 越积越大，**不要把请求级状态字段加到 BaseDao 上**。

---

## 四、Controller 写法

新 Controller 继承 `BaseController`（`com.xtr.framework.base.controller.*` 或 `BaseWebController`），返回 `AjaxResult`：

```java
@RestController
@RequestMapping("/foo")
public class FooController extends BaseWebController {

    @PostMapping("/save")
    public AjaxResult save() {
        IData param = getRequestParam();       // 自动解析 JSON body / form
        BaseDao dao = BaseDao.getDao("");
        param.setTableName("foo");
        dao.insertByExistField("foo", param);  // 自动剔除非表字段
        return AjaxResult.success();
    }
}
```

- 不要捕获 DAO 抛出的 `RuntimeException`，让 `GlobalExceptionHandler` 转成 `AjaxResult.error(...)`。
- 想强制回滚而不抛异常 → `request.setAttribute("_exception", someValue)`。

---

## 五、IData / IDataset / 周边类型

| 类 | 性质 | 典型构造 |
|----|------|----------|
| `IData` | `extends Entity`（Map<String,Object> + tableName） | `new IData()` / `new IData(jsonStr)` / `new IData(jsonObject)` / `new IData(entity)` |
| `IDataset` | `extends PageResult<IData>` | `new IDataset()` / `new IDataset(List<Entity>)` / `new IDataset(PageResult<Entity>)` |
| `Pagination` | **1-based** 页码 | `new Pagination(currentPage, size)`；内部转 Hutool 的 0-based `Page` |
| `SQLParser` | 命名占位符 + 字符串字面量识别 | 见上 |
| `ChangeBean` | camelCase ↔ snake_case；Bean ↔ IData | 桥接前端 JSON 与数据库列 |

---

## 六、重复类陷阱 ⚠️

**先看 import 路径再改！这些类同名但不等价：**

| 名称 | 位置 A | 位置 B | 选哪个 |
|------|--------|--------|--------|
| `BaseEntity` | `com.xtr.framework.base.domain` | `com.xtr.framework.hutool` | **hutool 版本**：多了 `@JsonIgnore` / `@JsonInclude`，序列化更干净 |
| `StringUtils` | `com.xtr.framework.common.utils` | `com.xtr.framework.hutool` | 取决于调用方现有 import，**不要混用** |
| `StrFormatter` | `com.xtr.framework.common.text` | `com.xtr.framework.hutool` | 同上 |

跨包修改时务必先确认调用方 import 的是哪个版本。

---

## 七、不要做的事

- ❌ 不要给 DAO 方法加 `throws SQLException` —— 框架契约是 `RuntimeException`，破坏会让所有调用方编译失败。
- ❌ 不要在 Controller 用 `try-catch` 吞掉 DAO 异常 —— 拦截器要靠异常触发 rollback。
- ❌ 不要在 BaseDao 上加字段保存请求级状态 —— ThreadLocal 不清理，会跨请求污染。
- ❌ 不要用 `@Transactional` —— 本项目不走 Spring 事务，拦截器是唯一事务边界。
- ❌ 不要拼接 SQL 字符串拼接用户输入 —— 用 `:key` 或 `?` 占位。
- ❌ 不要忘记 `setTableName` —— `insert`/`update` 不带表名会抛运行时异常。
- ❌ 不要引入 Java 9+ 语法（var、record、switch 表达式新形态、List.of 等谨慎）—— 编译目标 Java 8。
- ❌ 不要提交真实的 `db.setting` —— 仓库里的 `127.0.0.1:43306/ykc` + `root/123456` 是占位值，改了要还原。
---

## 八、调试线索

| 现象 | 可能原因 |
|------|----------|
| 改了数据但请求成功后没落库 | 拦截器没注册（检查 `WebMvcConfig`）/ 非 HTTP 场景没手动 commit |
| 多个请求拿到同一个 BaseDao | 正常：同线程同数据源会复用 ThreadLocal 里的实例 |
| `获取表元数据失败` | 表不存在 / 当前数据源连不到 / `MetaUtil` 大小写问题（列名缓存按 lowercase） |
| `insertByExistField执行失败: 过滤后无有效字段` | IData 里的 key 全都不在表列名中，多半是 camelCase 没转 snake_case，需要先过 `ChangeBean` |
| `:xxx` 占位符没生效 | cond 里没设该 key，被 SQLParser 静默跳过 —— 显式补 set 或换 `?` 占位 |
| 测试代码连不上库 | 改 `src/main/resources/config/db.setting`；改完务必还原占位值再提交 |

---

## 九、运行 / 验证

- **没有 JUnit**：`mvn test` 是空操作。
- 验证逻辑写在类的 `main()` 方法里（最完整的是 `src/main/java/TestDao.java`），从 IDE 里 Run。
- 改 `src/main/resources/config/db.setting` 指向真实库后再跑 `TestDao`，跑完**记得还原 db.setting 再提交**。
- 这是个库，不要尝试 `mvn spring-boot:run`（readme 写错了，没有 `spring-boot-maven-plugin` 也没 `@SpringBootApplication`）。

---

## 十、回答用户时的语言

- 始终用**中文**与用户交流，输出格式前带一个 emoji（项目全局偏好）。
- 代码、SQL、命令、报错日志按原样保留英文。

xtr-db框架 继承自hutool框架，重写了全局db事物
主流思想是简单建议将所有业务写在controller层，统一使用弱对象存储，Idata,和Idataset
1.x单层架构 逆主流java框架,没有那多多的分层强调的是业务。
基于springboot 项目只有一个controller 层，数据持久化使用hutooldb
这是一个基于Spring Boot和Hutool数据库操作框架的Java项目，主要特点如下：
1. 项目结构
   项目名称: xtr-db
   版本: 1.2
   框架: Spring Boot 2.7.18, 使用 undertow 作为web容器
   数据库: 主要使用 PostgreSQL，支持多数据源
   开发语言: Java 8
2. 核心功能模块
   数据访问层 (hutool包)
   IData: 数据传输对象，封装了hutool的Entity，支持JSON转换
   IDataset: 数据集对象，用于处理数据列表和分页结果
   BaseDao: 数据访问对象，封装了数据库操作，支持多数据源
   BaseDb: 数据库操作类，继承自hutool的Db，实现事务管理
   SQLParser: SQL解析器，支持参数化SQL查询
   控制器层
   BaseController: 通用Web控制器基类，处理日期绑定、请求响应等
   BaseWebController: Web控制器扩展，处理请求参数、文件上传等
   实体和工具类
   BaseEntity: 实体基类，包含创建者、更新时间等通用字段
   AjaxResult: 统一返回结果封装
   ChangeBean: 数据转换工具，支持驼峰命名和下划线命名转换
   IDataHepler: IData操作辅助类，包含分组、树形结构构建等功能
3. 特色功能
   事务管理
   通过DaoInterceptor实现全局事务管理
   自动在请求完成后提交或回滚事务
   使用ThreadLocal管理连接
   多数据源支持
   通过BaseDao.getDao()工厂方法实现线程安全的多数据源访问
   支持PostgreSQL等多种数据库
   数据格式转换
   支持驼峰命名和下划线命名之间的转换
   JSON和IData之间的转换
   支持复杂数据类型处理
4. 配置文件
   db.setting: 数据库连接配置，使用PostgreSQL
5. 项目特点
   高度封装的数据库操作接口
   自动化的事务管理
   灵活的参数处理和数据转换
   支持分页查询和批量操作
   提供了丰富的数据处理工具
6. 项目用途
   这个项目似乎是一个数据库访问中间件或数据访问框架，主要为上层业务提供统一的数据访问接口，简化数据库操作，特别适用于需要处理多种数据库、复杂数据转换和事务管理的场景。
   该项目没有典型的Spring Boot启动类，可能是一个库或框架，被其他项目引用。
   这个项目的设计思路非常清晰，通过IData和IDataset抽象了数据操作，通过BaseDao封装了数据库访问细节，是一个非常实用的数据访问框架。

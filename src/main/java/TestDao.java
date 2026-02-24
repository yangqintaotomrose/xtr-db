import com.xtr.framework.hutool.BaseDao;
import com.xtr.framework.hutool.IData;
import com.xtr.framework.hutool.IDataset;

import java.util.List;

/**
 * @Classname TestDao
 * @Description
 * @Date 2022/11/25 17:02
 * @Created by xtr-framework
 */
public class TestDao {
    public static void main(String[] args) throws Exception {
        System.out.println("=== xtr-db 数据库操作测试 ===");
        // 建表语句
// CREATE TABLE `test_table` (
//   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
//   `username` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账户账号',
//   `email` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '账户名称',
//
//   `create_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
//   `create_time` datetime DEFAULT NULL,
//   `update_by` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
//   `update_time` datetime DEFAULT NULL,
//   `power_balance` decimal(12,2) DEFAULT '0.00',
//                 PRIMARY KEY (`id`) USING BTREE
// ) ENGINE=InnoDB AUTO_INCREMENT=12284 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='测试表';

        // 创建数据库连接
        BaseDao dao = new BaseDao(""); // 使用默认数据源

        try {
            // 1. 查询操作示例
            testQueryOperations(dao);

            // 2. 插入操作示例
            testInsertOperation(dao);

            // 3. 更新操作示例
            testUpdateOperation(dao);

            // 4. 删除操作示例
            testDeleteOperation(dao);

            // 5. 事务操作示例
            testTransactionOperation(dao);

            // 6. 分页查询示例
            testPaginationQuery(dao);

            System.out.println("=== 所有测试完成 ===");

        } catch (Exception e) {
            System.err.println("操作出现异常: " + e.getMessage());
            e.printStackTrace();
            // 发生异常时回滚事务
            dao.rollback();
        } finally {
            // 提交事务
            dao.commit();
            System.out.println("数据库连接已关闭");
        }
    }

    /**
     * 测试查询操作
     */
    private static void testQueryOperations(BaseDao dao) throws Exception {
        System.out.println("\n--- 测试查询操作 ---");

        // 查询单条记录
        IData firstRecord = dao.queryByFirst("SELECT * FROM test_table LIMIT 1");
        if (firstRecord != null) {
            System.out.println("第一条记录: " + firstRecord);
        } else {
            System.out.println("未找到记录");
        }

        // 查询多条记录
        IDataset records = dao.queryList("SELECT * FROM test_table LIMIT 5");
        System.out.println("查询到 " + records.size() + " 条记录");
        for (int i = 0; i < records.size(); i++) {
            System.out.println("记录 " + (i + 1) + ": " + records.getData(i));
        }
    }

    /**
     * 测试插入操作
     */
    private static void testInsertOperation(BaseDao dao) throws Exception {
        System.out.println("\n--- 测试插入操作 ---");

        // 创建测试数据
        IData testData = new IData();
        testData.set("username", "test_user_" + System.currentTimeMillis());
        testData.set("email", "test@example.com");
        testData.set("create_time", dao.getSysTimeLocal());
        testData.setTableName("test_table"); // 设置表名

        // 执行插入
        int result = dao.insert(testData);
        System.out.println("插入操作影响行数: " + result);

        if (result > 0) {
            System.out.println("插入成功!");
        }
    }

    /**
     * 测试更新操作
     */
    private static void testUpdateOperation(BaseDao dao) throws Exception {
        System.out.println("\n--- 测试更新操作 ---");

        // 先查询一条记录
        IData record = dao.queryByFirst("SELECT * FROM test_table LIMIT 1");
        if (record != null) {
            System.out.println("更新前记录: " + record);

            // 修改数据
            record.set("email", "updated_email_" + System.currentTimeMillis() + "@example.com");
            record.set("update_time", dao.getSysTimeLocal());
            record.setTableName("test_table");

            // 执行更新
            int result = dao.updateById(record);
            System.out.println("更新操作影响行数: " + result);

            if (result > 0) {
                System.out.println("更新成功!");
                // 查询更新后的记录
                IData updatedRecord = dao.queryByFirst("SELECT * FROM test_table WHERE id = ?", record.get("id"));
                System.out.println("更新后记录: " + updatedRecord);
            }
        } else {
            System.out.println("没有可更新的记录");
        }
    }

    /**
     * 测试删除操作
     */
    private static void testDeleteOperation(BaseDao dao) throws Exception {
        System.out.println("\n--- 测试删除操作 ---");

        // 插入一条测试数据用于删除
        IData testData = new IData();
        String testUsername = "delete_test_" + System.currentTimeMillis();
        testData.set("username", testUsername);
        testData.set("email", "delete_test@example.com");
        testData.set("create_time", dao.getSysTimeLocal());
        testData.setTableName("test_table");

        dao.insert(testData);

        // 查询刚插入的记录
        IData recordToDelete = dao.queryByFirst("SELECT * FROM test_table WHERE username = ?", testUsername);
        if (recordToDelete != null) {
            System.out.println("待删除记录: " + recordToDelete);

            // 执行删除
            int result = dao.delete("test_table", "id", recordToDelete.get("id"));
            System.out.println("删除操作影响行数: " + result);

            if (result > 0) {
                System.out.println("删除成功!");
            }
        }
    }

    /**
     * 测试事务操作
     */
    private static void testTransactionOperation(BaseDao dao) throws Exception {
        System.out.println("\n--- 测试事务操作 ---");

        try {
            // 插入第一条记录
            IData record1 = new IData();
            record1.set("username", "transaction_test_1_" + System.currentTimeMillis());
            record1.set("email", "trans1@example.com");
            record1.set("create_time", dao.getSysTimeLocal());
            record1.setTableName("test_table");
            dao.insert(record1);

            System.out.println("第一条记录插入成功");

            // 故意制造一个错误来测试事务回滚
            // 这里模拟一个业务逻辑错误
            if (true) { // 模拟条件不满足
                throw new RuntimeException("模拟业务异常，触发事务回滚");
            }

            // 插入第二条记录（不会执行到这里）
            IData record2 = new IData();
            record2.set("username", "transaction_test_2_" + System.currentTimeMillis());
            record2.set("email", "trans2@example.com");
            record2.set("create_time", dao.getSysTimeLocal());
            record2.setTableName("test_table");
            dao.insert(record2);

            System.out.println("第二条记录插入成功");

            // 如果没有异常，提交事务
            dao.commit();
            System.out.println("事务提交成功");

        } catch (Exception e) {
            // 发生异常时回滚事务
            dao.rollback();
            System.out.println("事务已回滚: " + e.getMessage());
        }
    }

    /**
     * 测试分页查询
     */
    private static void testPaginationQuery(BaseDao dao) throws Exception {
        System.out.println("\n--- 测试分页查询 ---");

        // 模拟分页参数
        int currentPage = 1;
        int pageSize = 3;

        // 创建分页对象
        com.xtr.framework.hutool.Pagination pagination = new com.xtr.framework.hutool.Pagination(currentPage, pageSize);

        // 执行分页查询
        IDataset pageResult = dao.queryPage("SELECT * FROM test_table ORDER BY id", new IData(), pagination);

        System.out.println("第 " + currentPage + " 页，每页 " + pageSize + " 条");
        System.out.println("总记录数: " + pageResult.getTotal());
        System.out.println("总页数: " + pageResult.getTotalPage());
        System.out.println("当前页记录数: " + pageResult.size());

        for (int i = 0; i < pageResult.size(); i++) {
            System.out.println("记录 " + (i + 1) + ": " + pageResult.getData(i));
        }
    }
}

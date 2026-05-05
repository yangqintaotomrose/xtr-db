package com.xtr.framework.hutool;

/**
 * @Classname BaseDao
 * @Description
 * @Date 2022/6/25 08:54
 * @Created by xtr-framework
 */

import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.PageResult;
import cn.hutool.db.meta.MetaUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 封装hutooldb ,简化数据持久化实现细节
 * 对外统一使用IData 和 IDataset 传输数据
 * 内部捕获受检异常，统一抛出运行时异常
 */
public class BaseDao {

    //存放dao对象的线程变量
    private static ThreadLocal<HashMap<String,BaseDao>> threadLocal = new ThreadLocal();

    private BaseDb db = null;//封装db

    //每个BaseDao实例缓存自己数据源下的表列名（小写），避免每次都查元数据
    private final ConcurrentHashMap<String, Set<String>> tableColumnsCache = new ConcurrentHashMap<>();

    /**
     * 创建和关闭 都需要手工处理
     */
    public BaseDao(String datasource)
    {
        db = BaseDb.use(datasource);
    }

    public BaseDb getDb() {
        return db;
    }

    /**
     *
     * 存入本地线程变量，框架同意关闭
     *
     */
    public static BaseDao getDao(String datasource)
    {
        System.out.println("当前线程："+Thread.currentThread());
        HashMap<String,BaseDao> daos = threadLocal.get();

        if(daos == null){
            daos = new HashMap<String,BaseDao>();
            threadLocal.set(daos);
        }
        BaseDao dao = daos.get(datasource);
     //   System.out.println("当前daoHashCode:"+dao.hashCode());
        if(dao == null)
        {
            daos.put(datasource,new BaseDao(datasource));
        }
        return daos.get(datasource);
    }

    //查第一条
    public IData queryByFirst(String sql)
    {
        try {
            List<Entity> list = db.query(sql);
            return list.size() == 0 ? null : new IData(list.get(0));
        } catch (Exception e) {
            throw new RuntimeException("queryByFirst执行失败: " + sql, e);
        }
    }

    public IData queryByFirst(String sql, Object... params)
    {
        try {
            List<Entity> list = db.query(sql, params);
            return list.size() == 0 ? null : new IData(list.get(0));
        } catch (Exception e) {
            throw new RuntimeException("queryByFirst执行失败: " + sql, e);
        }
    }

    public IData queryByFirst(String sql, IData params)
    {
        try {
            List<Entity> list = db.query(sql, params);
            return list.size() == 0 ? null : new IData(list.get(0));
        } catch (Exception e) {
            throw new RuntimeException("queryByFirst执行失败: " + sql, e);
        }
    }

    //查全部
    public IDataset queryList(String sql, Object... params)
    {
        try {
            List<Entity> list = db.query(sql, params);
            return new IDataset(list);
        } catch (Exception e) {
            throw new RuntimeException("queryList执行失败: " + sql, e);
        }
    }

    public IDataset queryList(String sql, IData params)
    {
        try {
            List<Entity> list = db.query(sql, params);
            return new IDataset(list);
        } catch (Exception e) {
            throw new RuntimeException("queryList执行失败: " + sql, e);
        }
    }

    public IDataset queryList(SQLParser parser, IData params)
    {
        try {
            List<Entity> list = db.query(parser.getSQL(), params);
            return new IDataset(list);
        } catch (Exception e) {
            throw new RuntimeException("queryList执行失败: " + parser.getSQL(), e);
        }
    }

    public IDataset queryList(String sql)
    {
        try {
            List<Entity> list = db.query(sql);
            return new IDataset(list);
        } catch (Exception e) {
            throw new RuntimeException("queryList执行失败: " + sql, e);
        }
    }

    //查数据库时间
    public String getSysTime()
    {
        try {
            return db.queryString("select now() as now from dual");
        } catch (Exception e) {
            throw new RuntimeException("getSysTime执行失败", e);
        }
    }

    //查本地服务器时间getSysTime
    public String getSysTimeLocal() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(dateTimeFormatter);
    }

    //查本地服务器日期
    public String getSysDateLocal() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    //分页查询
    public IDataset queryPage(String sql, IData cond, Pagination page)
    {
        try {
            PageResult<Entity> list = db.page(sql, new Page(page.getCurrPage() - 1, page.getSize()), cond);
            return new IDataset(list);
        } catch (Exception e) {
            throw new RuntimeException("queryPage执行失败: " + sql, e);
        }
    }

    public IDataset queryPage(SQLParser sql, IData cond, Pagination page)
    {
        try {
            PageResult<Entity> list = db.page(sql.getSQL(), new Page(page.getCurrPage() - 1, page.getSize()), cond);
            return new IDataset(list);
        } catch (Exception e) {
            throw new RuntimeException("queryPage执行失败: " + sql.getSQL(), e);
        }
    }

    //插入返回执行记录数据
    public int insert(IData data)
    {
        try {
            return db.insert(data);
        } catch (Exception e) {
            throw new RuntimeException("insert执行失败: " + data.getTableName(), e);
        }
    }

    //传入表名称插入
    public int insert(String tableName, IData data)
    {
        data.setTableName(tableName);
        try {
            return db.insert(data);
        } catch (Exception e) {
            throw new RuntimeException("insert执行失败: " + tableName, e);
        }
    }

    public int update(IData data, IData where)
    {
        try {
            return db.update(data, where);
        } catch (Exception e) {
            throw new RuntimeException("update执行失败: " + data.getTableName(), e);
        }
    }

    public int delete(String tableName, String field, Object value)
    {
        try {
            return db.del(tableName, field, value);
        } catch (Exception e) {
            throw new RuntimeException("delete执行失败: " + tableName, e);
        }
    }
    public int updateById(IData data)
    {
        try {
            //兼容pd的强类型，pd 在类型转化上没有mysql灵活
            return db.update(data, new IData().set("id", data.getObj("id")));
        } catch (Exception e) {
            throw new RuntimeException("updateById执行失败: " + data.getTableName(), e);
        }
    }
    //默认根据ID更新
    public int updateById(String tableName,IData data)
    {
        try {
            data.setTableName(tableName);
            //兼容pd的强类型，pd 在类型转化上没有mysql灵活
            return db.update(data, new IData().set("id", data.getObj("id")));
        } catch (Exception e) {
            throw new RuntimeException("updateById执行失败: " + data.getTableName(), e);
        }
    }

    public int[] executeBatch(String... sqls)
    {
        try {
            return db.executeBatch(sqls);
        } catch (Exception e) {
            throw new RuntimeException("executeBatch执行失败", e);
        }
    }

    //批量插入返回执行记录数据
    public int[] inserts(IDataset list)
    {
        try {
            return db.insert(list);
        } catch (Exception e) {
            throw new RuntimeException("inserts执行失败", e);
        }
    }

    //批量插入：指定表名，返回总影响行数
    public int insertBatch(String tableName, IDataset list)
    {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < list.size(); i++) {
            list.getData(i).setTableName(tableName);
        }
        try {
            int[] rows = db.insert(list);
            int total = 0;
            for (int r : rows) {
                total += r;
            }
            return total;
        } catch (Exception e) {
            throw new RuntimeException("insertBatch执行失败: " + tableName, e);
        }
    }

    //批量插入：分批提交，避免一次性插入数据量过大
    public int insertBatch(String tableName, IDataset list, int batchSize)
    {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        if (batchSize <= 0) {
            batchSize = 500;
        }
        int total = 0;
        IDataset chunk = new IDataset();
        try {
            for (int i = 0; i < list.size(); i++) {
                IData row = list.getData(i);
                row.setTableName(tableName);
                chunk.add(row);
                if (chunk.size() >= batchSize) {
                    for (int r : db.insert(chunk)) {
                        total += r;
                    }
                    chunk = new IDataset();
                }
            }
            if (!chunk.isEmpty()) {
                for (int r : db.insert(chunk)) {
                    total += r;
                }
            }
            return total;
        } catch (Exception e) {
            throw new RuntimeException("insertBatch执行失败: " + tableName, e);
        }
    }

    /**
     * 获取表的列名集合（小写形式，用于不区分大小写匹配）
     */
    private Set<String> getTableColumns(String tableName)
    {
        return tableColumnsCache.computeIfAbsent(tableName, t -> {
            try {
                List<String> cols = Arrays.asList(MetaUtil.getColumnNames(db.getDs(), t));
                if (cols == null || cols.isEmpty()) {
                    throw new RuntimeException("未获取到表的列元数据: " + t);
                }
                Set<String> set = new HashSet<>(cols.size() * 2);
                for (String c : cols) {
                    set.add(c.toLowerCase());
                }
                return set;
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                throw new RuntimeException("获取表元数据失败: " + t, e);
            }
        });
    }

    /**
     * 按表元数据过滤IData，剔除不存在的列
     */
    private IData filterByExistField(String tableName, IData data)
    {
        if (data == null) {
            return null;
        }
        Set<String> cols = getTableColumns(tableName);
        IData filtered = new IData();
        filtered.setTableName(tableName);
        for (String key : data.keySet()) {
            if (key != null && cols.contains(key.toLowerCase())) {
                filtered.set(key, data.get(key));
            }
        }
        return filtered;
    }

    /**
     * 按表元数据过滤后插入：自动剔除IData中不属于表字段的属性
     */
    public int insertByExistField(String tableName, IData data)
    {
        if (data == null) {
            return 0;
        }
        IData filtered = filterByExistField(tableName, data);
        if (filtered.isEmpty()) {
            throw new RuntimeException("insertByExistField执行失败: 过滤后无有效字段, table=" + tableName);
        }
        try {
            return db.insert(filtered);
        } catch (Exception e) {
            throw new RuntimeException("insertByExistField执行失败: " + tableName, e);
        }
    }

    /**
     * 按表元数据过滤后按id更新：自动剔除IData中不属于表字段的属性
     */
    public int updateByIdExistField(String tableName, IData data)
    {
        if (data == null) {
            return 0;
        }
        Object id = data.getObj("id");
        if (id == null) {
            throw new RuntimeException("updateByIdExistField执行失败: 缺少id字段, table=" + tableName);
        }
        IData filtered = filterByExistField(tableName, data);
        //更新内容不应包含id本身
        filtered.remove("id");
        if (filtered.isEmpty()) {
            throw new RuntimeException("updateByIdExistField执行失败: 过滤后无可更新字段, table=" + tableName);
        }
        try {
            return db.update(filtered, new IData().set("id", id));
        } catch (Exception e) {
            throw new RuntimeException("updateByIdExistField执行失败: " + tableName, e);
        }
    }

    //插入返回主键ID
    public long insertExt(IData data)
    {
        try {
            return db.insertForGeneratedKey(data);
        } catch (Exception e) {
            throw new RuntimeException("insertExt执行失败: " + data.getTableName(), e);
        }
    }

    //执行SQL
    public int execSql(String sql, Object... param)
    {
        try {
            return db.execute(sql, param);
        } catch (Exception e) {
            throw new RuntimeException("execSql执行失败: " + sql, e);
        }
    }

    //无错就，提交事务
    public void commit()
    {
        try {
            db.commit();
        } catch (Exception e) {
            throw new RuntimeException("commit执行失败", e);
        }
    }

    //有错就，回滚事务
    public void rollback()
    {
        try {
            db.rollback();
        } catch (Exception e) {
            throw new RuntimeException("rollback执行失败", e);
        }
    }

    //结束
    public void end()
    {
        try {
            db.end();
        } catch (Exception e) {
            throw new RuntimeException("end执行失败", e);
        }
    }

    public static ThreadLocal<HashMap<String,BaseDao>> getThreadLocal(){
        return threadLocal;
    }
}

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
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

/**
 * 封装hutooldb ,简化数据持久化实现细节
 * 对外统一使用IData 和 IDataset 传输数据
 */
public class BaseDao {

    //存放dao对象的线程变量
    private static ThreadLocal<HashMap<String,BaseDao>> threadLocal = new ThreadLocal();

    private BaseDb db = null;//封装db

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
    @SneakyThrows
    public IData queryByFirst(String sql)
    {
        List<Entity> list = db.query(sql);
        return list.size()==0?null:new IData(list.get(0));
    }

    @SneakyThrows
    public IData queryByFirst(String sql,Object... params)
    {
        List<Entity> list = db.query(sql,params);
        return list.size()==0?null:new IData(list.get(0));
    }
    @SneakyThrows
    public IData queryByFirst(String sql,IData params)
    {
        List<Entity> list = db.query(sql,params);
        return list.size()==0?null:new IData(list.get(0));
    }

    //查全部
    @SneakyThrows
    public IDataset queryList(String sql, Object... params)
    {
        List<Entity> list = db.query(sql,params);
        return new IDataset(list);
    }

    @SneakyThrows
    public IDataset queryList(String sql,IData params)
    {
        List<Entity> list = db.query(sql,params);
        return new IDataset(list);
    }
    @SneakyThrows
    public IDataset queryList(SQLParser parser, IData params)
    {
        List<Entity> list = db.query(parser.getSQL(),params);
        return new IDataset(list);
    }
    @SneakyThrows
    public IDataset queryList(String sql)
    {
        List<Entity> list = db.query(sql);
        return new IDataset(list);
    }
    //查数据库时间
    public String getSysTime() throws Exception
    {
        return db.queryString("select now() as now from dual");
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
    public IDataset queryPage(String sql, IData cond, Pagination page) throws Exception
    {
        PageResult<Entity> list = db.page(sql, new Page(page.getCurrPage()-1, page.getSize()),cond);
        return new IDataset(list);
    }

    @SneakyThrows
    public IDataset queryPage(SQLParser sql, IData cond, Pagination page)
    {
        PageResult<Entity> list = db.page(sql.getSQL(), new Page(page.getCurrPage()-1, page.getSize()),cond);
        return new IDataset(list);
    }
    //插入返回执行记录数据
    @SneakyThrows
    public int insert(IData data) throws Exception
    {
        return db.insert(data);
    }
    //传入表名称插入
    @SneakyThrows
    public int insert(String tableName,IData data) throws Exception
    {
        data.setTableName(tableName);
        return db.insert(data);
    }
    @SneakyThrows
    public int update(IData data,IData where)
    {
        return db.update(data,where);
    }

    @SneakyThrows
    public int delete(String tableName, String field, Object value)
    {
        return db.del(tableName,field,value);
    }

    //默认根据ID更新
    @SneakyThrows
    public int updateById(IData data)
    {
        //兼容pd的强类型，pd 在类型转化上没有mysql灵活
        return db.update(data,new IData().set("id",data.getObj("id")));
    }
    @SneakyThrows
    public int[] executeBatch(String... sqls) throws Exception
    {
        return db.executeBatch(sqls);
    }
    //批量插入返回执行记录数据
    @SneakyThrows
    public int[] inserts(IDataset list) throws Exception
    {
        return db.insert(list);
    }
    //插入返回主键ID
    @SneakyThrows
    public long insertExt(IData data)
    {
        return db.insertForGeneratedKey(data);
    }
    //执行SQL
    @SneakyThrows
    public int execSql( String sql,Object... param)
    {
        return db.execute(sql, param);
    }

    //无错就，提交事务
    public void commit() throws Exception
    {
        db.commit();
    }
    //有错就，回滚事务
    public void rollback() throws Exception
    {
        db.rollback();
    }
    //结束
    public void end() throws Exception
    {
        db.end();
    }

    public static ThreadLocal<HashMap<String,BaseDao>> getThreadLocal(){
        return threadLocal;
    }
}

import com.xtr.framework.hutool.BaseDao;
import com.xtr.framework.hutool.IDataset;

/**
 * @Classname TestDao
 * @Description
 * @Date 2022/11/25 17:02
 * @Created by yangqintao
 */
public class TestDao {
    public static void main(String[] args) throws Exception {
        //测试多数据源连接 默认mysql 5.7 udap库为主库
        BaseDao dao2 = new BaseDao("");
       // IData aaa2 = dao2.queryByFirst("select * from h_biz_service_method");
       // IData hasUser = dao2.queryByFirst("select * from user_4a where user_id = ?","12");
       //  IData hasUser  = new IData();
       //  System.out.println(hasUser);
       //  hasUser.setTableName("user_4a");
       //  hasUser.set("id",2);
       //  hasUser.set("update_time", dao2.getSysTimeLocal());
       //  dao2.updateById(hasUser);
       //  //System.out.println(dao2.getSysTimeLocal());

        dao2.getSysTime();
        IDataset list = dao2.queryList("select * from ims_sys_org");
        System.out.println(list);
        dao2.commit();
//        BaseDao dao = new BaseDao("");
//        //IDataset list = dao.queryList("SELECT * FROM [192.168.80.19].mySHOPHQStock.dbo.shop");
//
//        IDataset list = dao.queryList("SELECT * FROM [192.168.80.19].mySHOPHQStock.dbo.shopconns");
////        IData list = dao.queryByFirst("EXEC [192.9.203.3].mySHOPSHStock.dbo.up_GetDaySaleGross '2025-03-20','1003','','2025-03-19'");
////        list.set("shopid",list.getString("shopid").trim());
////        System.out.println(list);
////        String sql = JsonToSql.generateCreateTableStatement("huijia.shop_ip", list);
////        dao2.execSql(sql);
////        String sql = JsonToSql.generateCreateTableStatement("huijia.day_sale", list);
////        dao2.execSql(sql);
//
//       // System.out.println(list.getNames());
//     //   System.out.println(list.getJSONObject());
////        dao2.execSql("truncate table huijia.shop");
////        String sql = JsonToSql.generateCreateTableStatement("huijia.shop", list);
////        dao2.execSql(sql);
////        list.setTableName("huijia.shop");
////        dao2.insert(list);
////        System.out.println("sql = " + sql);
//        //根据数据结构直接建表
//        for (int i = 0; i <list.size() ; i++) {
//            IData data = list.getData(i);
//            data.setTableName("huijia.shop_ip");
//        }
//        dao2.inserts(list);
//        dao2.commit();

//        list.setTableName("huijia.day_sale");
//        dao2.insert(list);
//        dao2.commit();

        //goldendb数据库，goldendb驱动兼容mysql8可以直连
//        BaseDao dao1 = new BaseDao("jf_mysql8");
//        Object[] queryParam = {"20240611"};
//        String sql = "select t.* from mgmt20.sjbz_index_table t where 1=1\n" +
//                "and (select count(*) from mgmt20.sjbz_index_table_run_log l where t.table_id = l.table_id and   l.stat_date = ? ) = 0\n" +
//                "";
//        System.out.println(sql);
//        IDataset tableList = dao1.queryList(sql,queryParam);
//        System.out.println(tableList);


        //test12

        //gbase
//        BaseDao dao3 = new BaseDao("gbase_query");
//        IData data = new IData();
//        data.setTableName("adhoc.test12");
//        data.set("a1", "1111");
//       long id =  dao3.insertExt(data);
//        System.out.println(id);
        //IData ccc = dao3.queryByFirst("select * from mgmt20.sm_user");
        //System.out.println(ccc);




    }
}

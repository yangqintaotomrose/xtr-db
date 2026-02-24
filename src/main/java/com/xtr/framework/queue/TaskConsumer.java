package com.xtr.framework.queue;

import cn.hutool.core.date.DateUtil;
import com.xtr.framework.hutool.BaseDao;
import com.xtr.framework.hutool.IData;
import org.springframework.stereotype.Service;

@Service
public class TaskConsumer extends Thread {
    private final MessageQueue queue;

    public TaskConsumer(MessageQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Task task;
            synchronized (queue.getLock()) {
                while ((task = queue.takeTask()) == null) {
                    try {
                        queue.getLock().wait(); // 线程等待
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            try {
                String info = processTask(task); // 处理任务
                System.out.println(getName() + " 消费了任务:" + info);
            } catch (RuntimeException e) {
                e.printStackTrace();
                //Thread.currentThread().interrupt();
                return;
            }
        }
    }


    private String processTask(Task task)  {
//        Thread.sleep(1000); // 假设处理时间为1000毫秒
        BaseDao dao = new BaseDao("mysql7");
        BaseDao sqlserver = new BaseDao("");
        String currentDate = DateUtil.yesterday().toString("yyyy-MM-dd");//DateUtil.formatDate(DateUtil.date());
        try {

            switch (task.getType())
            {
                case "sale":
                    IData shop = task.getTaskInfo();
                    IData shopInfo = dao.queryByFirst("select * from xxx.shop where id='"+shop.getString("shopid")+"'");
                    IData sale = sqlserver.queryByFirst("EXEC ["+shop.getString("ip")+"].xxx.dbo.up_GetDaySaleGross '"+currentDate+"','"+shop.getString("shopid")+"','','"+currentDate+"'");
                    if(sale == null)
                    {
                        break;
                    }
                    sale.set("stat_date", currentDate);
                    sale.set("shop_name", shopInfo==null?"":shopInfo.getString("name"));
                    sale.set("create_time", DateUtil.formatDateTime(DateUtil.date()));
                    sale.setTableName("xxx.sale_record");
                    //查询历史表有没有当天的记录 有就更新没有就插入
                    IData saleHis = dao.queryByFirst("select * from xxx.sale_record where stat_date='"+currentDate+"' and shopid='"+shop.getString("shopid")+"' and avgsalevalue="+sale.getBigDecimal("avgsalevalue"));
                    if(saleHis == null)
                    {
                        dao.insert(sale);
                    }else{
                        dao.update(sale,new IData().set("stat_date",sale.getString("stat_date")).set("shopid",sale.getString("shopid")).set("avgsalevalue",sale.getBigDecimal("avgsalevalue")));
                    }

                    break;
            }

            dao.commit();
            sqlserver.commit();
        } catch (Exception e) {
            try {
                dao.rollback();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            try {
                sqlserver.rollback();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

        return "";
    }
}


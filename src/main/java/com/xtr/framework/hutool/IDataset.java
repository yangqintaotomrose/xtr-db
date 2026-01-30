package com.xtr.framework.hutool;
import cn.hutool.db.Entity;
import cn.hutool.db.PageResult;
import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @Classname IDataset
 * @Description
 * @Date 2022/6/24 00:09
 * @Created by yangqintao
 */
public class IDataset extends PageResult {
    public IDataset(){
    }

    /**
     * 普通查询转IDataset
     * @param list
     */
    public IDataset(List<Entity> list){
        for (int i = 0; i < list.size(); i++) {
            super.add(new IData(list.get(i)));
        }
    }

    /**
     * 分页查询结果转IDataset
     * @param list
     */
    public IDataset(PageResult<Entity> list){
        super.setTotal(list.getTotal());
        super.setPage(list.getPage());
        super.setPageSize(list.getPageSize());
        super.setTotalPage(list.getTotalPage());
        for (int i = 0; i < list.size(); i++) {
            super.add(new IData(list.get(i)));
        }
    }
    public String[] getNames()
    {
        return size() > 0 ? ((IData)get(0)).getNames() : null;
    }
    /**
     * 获取IData
     * @param i
     * @return
     */
    public IData getData(int i){
        return (IData) super.get(i);
    }



    public IDataset(String jsonString){
        List<IData> list = JSONArray.parseArray(jsonString,IData.class);
        super.addAll(list);
    }
    /**
     * 转换为List<IData>
     * @return List<IData>
     */
    public List<IData> toList() {
        return (List<IData>) this;
    }


}

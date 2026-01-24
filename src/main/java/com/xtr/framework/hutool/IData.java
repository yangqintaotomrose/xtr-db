package com.xtr.framework.hutool;

import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.Map;

/**
 * @Classname IData
 * @Description
 * @Date 2022/6/23 23:01
 * @Created by yangqintao
 */
public class IData extends Entity {

    public IData(){

    }

    public IData(Entity entity){
        super.putAll(entity);
    }

    /**
     * 支持复杂json类型
     * @param jsonString
     */
    public IData(String jsonString){
        Map mapObj = JSONObject.parseObject(jsonString, Map.class);
        super.putAll(mapObj);
    }

    /**
     * JSONObject 转换为IData
     * @param jsonObject
     */
    public IData(JSONObject jsonObject){
        Map mapObj =jsonObject.getInnerMap();
        super.putAll(jsonObject);
    }

    public static IData  fromEntity(Entity entity){
        return new IData(entity);
    }

    public String getString(String var1){
        return this.getStr(var1);
    }

    public String getString(String var1, String var2){
        return null == this.getStr(var1)?var2:this.getStr(var1);
    }


    public int getInt(String var1, int var2){
        return null == this.getInt(var1)?var2:this.getInt(var1);
    }


    public double getDouble(String var1, double var2){
        return null == this.getDouble(var1)?var2:this.getDouble(var1);
    }

    public IData getData(String var1){
        if(this.get(var1) instanceof IData)
        {
            return (IData)this.get(var1);
        }
        return null;
    }

    public IDataset getDataset(String var1){
        if(this.get(var1) instanceof IDataset)
        {
            return (IDataset)this.get(var1);
        }
        return null;
    }

    public IData set(String field, Object value){
        super.set(field, value);
        return this;
    }

    public String[] getNames()
    {
        return getNames(true);
    }

    /**
     *
     * @param sort
     * @return
     */
    public String[] getNames(boolean sort)
    {
        String[] names = (String[])keySet().toArray(new String[0]);
        if (sort) Arrays.sort(names);
        return names;
    }
    public JSONObject getJSONObject()
    {
        JSONObject jsonObject = new JSONObject();
        String[] names = (String[])keySet().toArray(new String[0]);
        for (String name:names) {
            jsonObject.put(name, get(name));
        }
        return jsonObject;
    }

    /**
     * 添加非空属性
     * @param m
     */
    public void putAllNotNull(Map<? extends String, ?> m) {
        m.forEach((key, value) -> {
            if(value != null)
            {
                this.put(key, value);
            }

        });
    }

    public IData getNotNullData() {
        IData newIdata =  new IData();
        this.forEach((key, value) -> {
            if(value == null || StringUtils.isNotEmpty(value.toString()))
            {
                newIdata.put(key, value);
            }

        });
        return newIdata;
    }


    public static void main(String[] args) {
        JSONObject a = new JSONObject();
        a.put("a", "1");
        a.put("b", "2");
        JSONObject b = new JSONObject();
        b.put("c", "3");
        b.put("d", "4");
        JSONArray array = new JSONArray();
        array.add(a);
        array.add(b);
        JSONObject c = new JSONObject();
        c.put("c", "1");
        c.put("a", a.clone());
        c.put("arr", array);
        System.out.println(c.toJSONString());
        IData iData = new IData(c.toJSONString());
        System.out.println("iData = " + iData.toString());
    }

}

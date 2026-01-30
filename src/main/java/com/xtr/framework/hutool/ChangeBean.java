package com.xtr.framework.hutool;

import com.xtr.framework.common.utils.DateUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Classname ChangeBean
 * @Description
 * @Date 2022/11/23 15:56
 * @Created by yangqintao
 */
public class ChangeBean {
    public static IData getIDataByGroup(String group_name, IData source) {
        IData idata = new IData();

        String[] names = source.getNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].indexOf(group_name) == 0) {
                idata.put(
                        names[i].substring(group_name.length(),
                                names[i].length()),
                        source.get(names[i]));
            }
        }
        return idata;
    }

    public static IData change(String up, IData source) {
        IData dest = new IData();

        String[] names = source.getNames();
        for (int i = 0; i < names.length; i++) {
            dest.put(
                    ("LOW".equalsIgnoreCase(up)) ? names[i].toLowerCase() : names[i]
                            .toUpperCase(), source.get(names[i]));
        }
        return dest;
    }

    public static IDataset change(String up, IDataset source) {
        IDataset dest = new IDataset();
        for (int ii = 0; ii < source.size(); ii++) {
            IData source_idata = source.getData(ii);
            IData dest_idata = new IData();

            String[] names = source_idata.getNames();
            for (int i = 0; i < names.length; i++) {
                dest_idata.put(("LOW".equalsIgnoreCase(up)) ? names[i].toLowerCase()
                        : names[i].toUpperCase(), source_idata.get(names[i]));
            }
            dest.add(dest_idata);
        }
        return dest;
    }

    public static IData getIdataByName(String name,String value, IDataset source) {
        if(null == value)
        {
            return new IData();
        }
        for(int i=0;i<source.size();i++)
        {
            IData idata = source.getData(i);
            String value2 = idata.getString(name);
            if(value.equals(value2))
            {
                return idata;
            }
        }
        //此数返回将来可能有bug后期需要调整
        return new IData();
    }
    //数据库样式装换成mybatis样式
    public static IData db_vo(IData db){
        return ChangeBean.parseIDataCamelize(JSON.toJSONString(db));
    }
    //驼峰式转换为非驼峰
    public static IData vo_db(IData vo){
        return ChangeBean.parseIDataDecamelize(JSON.toJSONString(vo));
    }
    //数据库样式装换成mybatis样式
    public static IDataset dbs_vos(IDataset db){
        IDataset a = new IDataset();
        for (int i = 0; i < db.size(); i++) {
            a.add(ChangeBean.parseIDataCamelize(JSON.toJSONString(db.getData(i))));
        }
        return a;
    }
    //驼峰式转换为非驼峰
    public static IDataset vos_dbs(IDataset vo){
        IDataset a = new IDataset();
        for (int i = 0; i < vo.size(); i++) {
            a.add(ChangeBean.parseIDataDecamelize(JSON.toJSONString(vo.getData(i))));
        }
        return a;
    }



    //非驼峰转换
    public static IData parseIData(String str){
        IData re = new IData();
        JSONObject a = JSONObject.parseObject(str);
        return parseIDataFromJSONObject(a);
    }
    //驼峰转换
    public static IData parseIDataCamelize(String str){
        IData re = new IData();
        JSONObject a = JSONObject.parseObject(str);
        return parseIDataFromJSONObject(a,true);
    }
    //驼峰式的vo对象转换成可以插入数据库的Idata对象
    public static IData parseObject(Object obj){
        IData re = new IData();
        JSONObject a = (JSONObject)JSONObject.toJSON(obj);
        return parseIDataFromJSONObject(a,false);
    }
    //驼峰式的vo对象转换成可以插入数据库的Idata对象
    //为了解决模糊查询只能存,String类型，Long 和 Interge无法查询
    public static IData parseObjectQuery(Object obj){
        IData re = new IData();
        JSONObject a = (JSONObject)JSONObject.toJSON(obj);
        //处理数据类型转换，Long,Integer强制转换成String
        return parseIDataFromAllStringJSONObject(a,false);
    }
    //Idata数据库对象转vo驼峰类，idata对象只有一层
    public static <T> T parseObject(IData idata,Class<T> clazz){
        IData re = new IData();
        Set<String> a_set = idata.keySet();
        for(String _a:a_set) {
            //key转换为新的驼峰
            re.set(camelize(_a),idata.get(_a));
        }
        return JSONObject.parseObject(JSONObject.toJSONString(re), clazz);
    }
    //Idataset 转换为list 每个list中的元素驼峰化
    public static <T> List<T> parseList(IDataset list, Class<T> clazz)
    {
        List<T> newList = new ArrayList<T>();
        for (int i = 0; i <list.size() ; i++) {
            IData temp = list.getData(i);
            newList.add(ChangeBean.parseObject(temp, clazz));
        }
        return newList;
    }
    public static <T> T parseObjectQuery(IData idata,Class<T> clazz){
        IData re = new IData();
        Set<String> a_set = idata.keySet();
        for(String _a:a_set) {
            //key转换为新的驼峰
            //re.set(camelize(_a),idata.get(_a));
            //判断方法的类型
            re.set(camelize(_a),getClassFrildValue(clazz,camelize(_a),idata.get(_a)));

        }
        return JSONObject.parseObject(JSONObject.toJSONString(re), clazz);
    }

    public static Object  getClassFrildValue(Class clazz,String key,Object obj){

        HashMap<String,Object> map = new HashMap<>();
        List<Field> fieldList = new ArrayList<>() ;
        Class tempClass = clazz;
        while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        for (Field field1 : fieldList) {
            //System.out.println(field1.getName());
            String type = field1.getAnnotatedType().getType().getTypeName();
            if(key.equals(field1.getName()))
            {
                if("long".equals(type)||"java.lang.Long".equals(type))
                {
                    return new Long(obj instanceof String ? ((String) obj) : null);
                }
                if("int".equals(type)||"java.lang.Integer".equals(type))
                {
                    return new Integer(obj instanceof String ? ((String) obj) : null);
                }
            }

        }
        return obj;
    }


    //反驼峰表达式(如果存在驼峰用这个方法插入数据库)
    public static IData parseIDataDecamelize(String str){
        IData re = new IData();
        JSONObject a = JSONObject.parseObject(str);
        return parseIDataFromJSONObject(a,false);
    }

    private static IData parseIDataFromJSONObject(JSONObject a){
        IData re = new IData();
        Set<String> a_set = a.keySet();
        for(String _a:a_set)
        {
            Object a_o = a.get(_a);
//			System.out.println(_a +"   key:   "+a_o);
//			System.out.println(a_o.getClass());
            if (a_o instanceof String) {
                String aO = (String) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof Double) {
                Double aO = (Double) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof Integer) {
                Integer aO = (Integer) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof Float) {
                Float aO = (Float) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof BigDecimal) {
                BigDecimal aO = (BigDecimal) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof Boolean) {
                Boolean aO = (Boolean) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof Long) {
                Long aO = (Long) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof Date) {
                Date aO = (Date) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof JSONObject) {
                JSONObject aO = (JSONObject) a_o;
                re.set(_a,parseIDataFromJSONObject(aO));

            }
            if (a_o instanceof JSONArray) {
                JSONArray aO = (JSONArray) a_o;
                re.set(_a,parseIDatasetFromJSONArray(aO));
            }

        }
        return re;
    }

    private static IDataset parseIDatasetFromJSONArray(JSONArray a){
        IDataset re = new IDataset();
        for(int _a =0;_a<a.size();_a++)
        {
            Object a_o = a.get(_a);
//			System.out.println(_a +"   key:   "+a_o);
//			System.out.println(a_o.getClass());
            if (a_o instanceof String) {
                String aO = (String) a_o;
                re.add(aO);
            }
            if (a_o instanceof Double) {
                Double aO = (Double) a_o;
                re.add(aO);
            }
            if (a_o instanceof Integer) {
                Integer aO = (Integer) a_o;
                re.add(aO);
            }
            if (a_o instanceof Float) {
                Float aO = (Float) a_o;
                re.add(aO);
            }
            if (a_o instanceof BigDecimal) {
                BigDecimal aO = (BigDecimal) a_o;
                re.add(aO);
            }
            if (a_o instanceof Boolean) {
                Boolean aO = (Boolean) a_o;
                re.add(aO);
            }
            if (a_o instanceof Long) {
                Long aO = (Long) a_o;
                re.add(aO);
            }
            if (a_o instanceof Date) {
                Date aO = (Date) a_o;
                re.add(aO);
            }
            if (a_o instanceof JSONObject) {
                JSONObject aO = (JSONObject) a_o;
                re.add(parseIDataFromJSONObject(aO));
            }
            if (a_o instanceof JSONArray) {
                JSONArray aO = (JSONArray) a_o;
                re.add(parseIDatasetFromJSONArray(aO));
            }

        }
        return re;
    }

    //驼峰表达式
    private static IData parseIDataFromJSONObject(JSONObject a, boolean isTf){
        IData re = new IData();
        Set<String> a_set = a.keySet();
        for(String _a:a_set)
        {
            Object a_o = a.get(_a);
            _a = isTf?camelize(_a):decamelize(_a);
//			System.out.println(_a +"   key:   "+a_o);
//			System.out.println(a_o.getClass());
            if (a_o instanceof String) {
                String aO = (String) a_o;
                re.set(_a,aO);
            }else if (a_o instanceof Double) {
                Double aO = (Double) a_o;
                re.set(_a,aO);
            }else if (a_o instanceof Integer) {
                Integer aO = (Integer) a_o;
                re.set(_a,aO);
            }else if (a_o instanceof Float) {
                Float aO = (Float) a_o;
                re.set(_a,aO);
            }else if (a_o instanceof BigDecimal) {
                BigDecimal aO = (BigDecimal) a_o;
                re.set(_a,aO);
            }else if (a_o instanceof Boolean) {
                Boolean aO = (Boolean) a_o;
                re.set(_a,aO);
            }else if (a_o instanceof Long) {
                Long aO = (Long) a_o;
                re.set(_a,aO);
            }else if (a_o instanceof Timestamp) {
                Timestamp aO = (Timestamp) a_o;
                //日期类型全部转换成String
                re.set(_a, DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",aO));
            }else if (a_o instanceof Date) {
                Date aO = (Date) a_o;
                //日期类型全部转换成String
                re.set(_a, DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",aO));
            }else if (a_o instanceof JSONObject) {
                JSONObject aO = (JSONObject) a_o;
                re.set(_a,parseIDataFromJSONObject(aO,isTf));

            }else if (a_o instanceof JSONArray) {
                JSONArray aO = (JSONArray) a_o;
                re.set(_a,parseIDatasetFromJSONArray(aO,isTf));
            }

        }
        return re;
    }

    //驼峰表达式
    private static IData parseIDataFromAllStringJSONObject(JSONObject a, boolean isTf){
        IData re = new IData();
        Set<String> a_set = a.keySet();
        for(String _a:a_set)
        {
            Object a_o = a.get(_a);
            _a = isTf?camelize(_a):decamelize(_a);
//			System.out.println(_a +"   key:   "+a_o);
//			System.out.println(a_o.getClass());
            if (a_o instanceof String) {
                String aO = (String) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof Double) {
                Double aO = (Double) a_o;
                re.set(_a,aO);
            }
            if (a_o instanceof Integer) {
                Integer aO = (Integer) a_o;
                re.set(_a,aO+"");
            }
            if (a_o instanceof Float) {
                Float aO = (Float) a_o;
                re.set(_a,aO+"");
            }
            if (a_o instanceof BigDecimal) {
                BigDecimal aO = (BigDecimal) a_o;
                re.set(_a,aO+"");
            }
            if (a_o instanceof Boolean) {
                Boolean aO = (Boolean) a_o;
                re.set(_a,aO+"");
            }
            if (a_o instanceof Long) {
                Long aO = (Long) a_o;
                re.set(_a,aO+"");
            }
            if (a_o instanceof Date) {
                Date aO = (Date) a_o;
                //日期类型全部转换成String
                re.set(_a,DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",aO));
            }
            if (a_o instanceof JSONObject) {
                JSONObject aO = (JSONObject) a_o;
                re.set(_a,parseIDataFromAllStringJSONObject(aO,isTf));

            }
            //就不考虑数组情况了
            if (a_o instanceof JSONArray) {
                JSONArray aO = (JSONArray) a_o;
                re.set(_a,parseIDatasetFromJSONArray(aO,isTf));
            }

        }
        return re;
    }
    //对象数组驼峰表达式
    private static IDataset parseIDatasetFromJSONArray(JSONArray a, boolean isTf){
        IDataset re = new IDataset();
        for(int _a =0;_a<a.size();_a++)
        {
            Object a_o = a.get(_a);
//			System.out.println(_a +"   key:   "+a_o);
//			System.out.println(a_o.getClass());
            if (a_o instanceof String) {
                String aO = (String) a_o;
                re.add(aO);
            }
            if (a_o instanceof Double) {
                Double aO = (Double) a_o;
                re.add(aO);
            }
            if (a_o instanceof Integer) {
                Integer aO = (Integer) a_o;
                re.add(aO);
            }
            if (a_o instanceof Float) {
                Float aO = (Float) a_o;
                re.add(aO);
            }
            if (a_o instanceof BigDecimal) {
                BigDecimal aO = (BigDecimal) a_o;
                re.add(aO);
            }
            if (a_o instanceof Boolean) {
                Boolean aO = (Boolean) a_o;
                re.add(aO);
            }
            if (a_o instanceof Long) {
                Long aO = (Long) a_o;
                re.add(aO);
            }
            if (a_o instanceof Date) {
                Date aO = (Date) a_o;
                re.add(aO);
            }
            if (a_o instanceof JSONObject) {
                JSONObject aO = (JSONObject) a_o;
                re.add(parseIDataFromJSONObject(aO,isTf));
            }
            if (a_o instanceof JSONArray) {
                JSONArray aO = (JSONArray) a_o;
                re.add(parseIDatasetFromJSONArray(aO,isTf));
            }

        }
        return re;
    }

    /**
     * 数据表字段名转换为驼峰式名字的实体类属性名
     * @param tabAttr   数据表字段名
     * @return  转换后的驼峰式命名
     */
    public static String camelize(String tabAttr){
        if(isBlank(tabAttr))
            return tabAttr;
        Pattern pattern = Pattern.compile("(.*)_(\\w)(.*)");
        Matcher matcher = pattern.matcher(tabAttr);
        if(matcher.find()){
            return camelize(matcher.group(1) + matcher.group(2).toUpperCase() + matcher.group(3));
        }else{
            return tabAttr;
        }
    }

    /**
     * 驼峰式的实体类属性名转换为数据表字段名
     * @param camelCaseStr  驼峰式的实体类属性名
     * @return  转换后的以"_"分隔的数据表字段名
     */
    public static String decamelize(String camelCaseStr){
        return isBlank(camelCaseStr) ? camelCaseStr : camelCaseStr.replaceAll("[A-Z]", "_$0").toLowerCase();
    }
    /**
     * 字符串是否为空
     * @param cs   待检查的字符串
     * @return  空：true; 非空：false
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }



    //2015.03.18新增动态分组
    public static IDataset gen_group(IDataset records,String idata_key) {
        //分组
        HashMap group = new LinkedHashMap();
        for(int i=0;i<records.size();i++){
            group.put(records.getData(i).getString(idata_key), records.getData(i).getString(idata_key));
        }
        //建立组模型
        IDataset group_list = new IDataset();
        for(Object key:group.keySet())
        {
            IData g = new IData();
            g.put("name", key);
            g.put("group_list", new IDataset());
            group_list.add(g);
        }
        for(int i=0;i<records.size();i++)
        {
            IData idata = records.getData(i);
            IDataset g = getGroup(group_list,idata,idata_key);
            if(!g.contains(idata))
            {
                g.add(idata);
            }

        }
        return group_list;
    }

    private static IDataset  getGroup(IDataset grouplist,IData idata,String idata_key)
    {
        for(int i=0;i<grouplist.size();i++)
        {
            IData idata_group = grouplist.getData(i);
            if(idata_group.getString("name").equals(idata.getString(idata_key)))
            {//找到分组名称
                return idata_group.getDataset("group_list");
            }
        }
        return null;
    }

    public static void main(String[] args) {
        IData a = new IData();
        a.set("user_name", "tomrose");

        IData iData = ChangeBean.parseIDataCamelize(JSON.toJSONString(a));
        System.out.println(iData.getString("userName"));

    }
}

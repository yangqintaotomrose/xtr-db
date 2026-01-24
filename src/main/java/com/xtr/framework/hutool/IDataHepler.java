package com.xtr.framework.hutool;




import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Classname IDataHepler
 * @Description
 * @Date 2022/7/20 15:26
 * @Created by yangqintao
 */
public class IDataHepler {
    public static IDataset gen_group(IDataset records,String idata_key) {
        //分组
        IData group = new IData();
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

    public static IDataset getByProperty(IDataset records,String idata_key,String idata_data) {
        IDataset group_list = new IDataset();

        for(int i=0;i<records.size();i++){
            IData data = records.getData(i);
            String key  = data.getString(idata_key);
            if(idata_data.equals(key)){
                group_list.add(data);
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

    /**
     * 判断idataset是否包含指定key的值
     * @parambooleanString 输入idata对象
     * @parambooleanString 键
     * @parambooleanString 值
     * @return idataset是否包含
     */
    public static boolean containsKey2Value(IDataset idataset,String key,String value){
        for(int i=0;i<idataset.size();i++)
        {
            if(value.equals(idataset.getData(i).getString(key)))
            {
                return true;
            }
        }
        return false;
    }
    /**
     * 返回包含相关key和值的idata集合
     * @parambooleanString 输入list对象
     * @parambooleanString 键
     * @parambooleanString 值
     * @return 包含所有指定key 的值对象
     */
    public static IDataset getSubDataset(IDataset datas, String key,String value) {
        IDataset dataset = new IDataset();
        for (Iterator iter = datas.iterator(); iter.hasNext();) {
            IData data = (IData) iter.next();
            if (data.containsKey(key) && data.getString(key).equals(value)) {
                dataset.add(data);
            }
        }
        return dataset;
    }
    /**
     * 从IDataset里面找key对应的内容，如果和value相同，就返回目前这个IData里面 column对应的那列值
     *
     * @param datas
     * @param key
     * @param value
     * @param column
     * @return
     */
    public static String getTheDataValue(IDataset datas, String key,
                                         String value, String column) {

        IData data = getTheData(datas, key, value);
        return data == null ? "" : data.getString(column);
    }
    /**
     * 从IDataset里面找key对应的内容，如果和value相同，就返回目前这个IData
     *
     * @param datas
     * @param key
     * @param value
     * @return
     * @author chenjw
     */
    public static IData getTheData(IDataset datas, String key, String value) {
        for (Iterator iter = datas.iterator(); iter.hasNext();) {
            IData data = (IData) iter.next();
            if (data.containsKey(key) && data.getString(key).equals(value)) {
                return data;
            }
        }
        return null;
    }

    //构建Tree
    /**
     * 构建前端所需要树结构
     *
     */
    public static IDataset buildIDataTree(IDataset depts) {
        IDataset returnList = new IDataset();
        List<String> tempList = new ArrayList<String>();
        for (int i = 0; i < depts.size(); i++) {
            tempList.add(depts.getData(i).getString("id"));
        }
        for (Iterator<IData> iterator = depts.iterator(); iterator.hasNext();)
        {
            IData dept =  iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getString("parent_id")) && "0".equals(dept.getString("parent_id")) )
            {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }
    /**
     * 递归列表
     */
    private static void recursionFn(IDataset list, IData t)
    {
        // 得到子节点列表
        IDataset childList = getChildList(list, t);
        t.set("children",childList);
        for (int i = 0; i < childList.size(); i++) {

            IData tChild = childList.getData(i);
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private static IDataset getChildList(IDataset list, IData t)
    {
        IDataset tlist = new IDataset();

        Iterator<IData> it = list.iterator();
        while (it.hasNext())
        {
            IData n = (IData) it.next();
            if (StringUtils.isNotNull(n.getString("id")) && n.getString("parent_id","").equals(t.getString("id")))
            {
                tlist.add(n);
            }
        }
        return tlist;
    }
    /**
     * 判断是否有子节点
     */
    private static boolean hasChild(IDataset list, IData t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    public static IDataset clone(IDataset depts) {
        IDataset returnList = new IDataset();
        for (int i = 0; i < depts.size(); i++) {
            returnList.add(depts.getData(i));
        }
        return returnList;
    }

}

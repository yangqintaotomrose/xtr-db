package com.xtr.framework.hutool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

public class JsonToSql {
    public static void main(String[] args) {
        String jsonString = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"Alice\",\n" +
                "  \"age\": 30,\n" +
                "  \"email\": \"alice@example.com\",\n" +
                "  \"address\": {\n" +
                "    \"street\": \"123 Main St\",\n" +
                "    \"city\": \"Wonderland\",\n" +
                "    \"zip\": \"12345\"\n" +
                "  },\n" +
                "  \"phone_numbers\": [\"123-456-7890\", \"987-654-3210\"]\n" +
                "}";
        IData idata = new IData();
        idata.set("id", 1);
        idata.set("departmentName", "aaa");
        idata.set("departmentNum", "aaa");
        idata.set("displayIndex", "aaa");
        idata.set("empNumber", "aaa");
        idata.set("extensions", "aaa");
        idata.set("firstName", "aaa");
        idata.set("isBywork", "aaa");
        idata.set("lastName", "aaa");
        idata.set("mail", "aaa");
        idata.set("mobile", "aaa");
        idata.set("orgCode", "aaa");
        idata.set("orgName", "aaa");
        idata.set("pid", "aaa");
        idata.set("passportID", "aaa");
        idata.set("password", "aaa");
        idata.set("sex", "aaa");
        idata.set("telephone", "aaa");
        idata.set("title", "aaa");
        idata.set("titleName", "aaa");
        idata.set("userId", "aaa");
        idata.set("userStatus", "aaa");
        idata.set("userType", "aaa");

        IData db = ChangeBean.vo_db(idata);
        db.set("add_time", "aaa");
        db.set("update_time", "aaa");
        db.set("sync", 1);

        //创建"departmentName",
        //    "departmentNum",
        //    "displayIndex",
        //    "empNumber",
        //    "extensions",
        //    "firstName",
        //    "isBywork",
        //    "lastName",
        //    "mail",
        //    "mobile",
        //    "orgCode",
        //    "orgName",
        //    "pid",
        //    "passportID",
        //    "password",
        //    "sex",
        //    "telephone",
        //    "title",
        //    "titleName",
        //    "userId",
        //    "userStatus",
        //    "userType"
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String tableName = "user_4a";
//        String sql = getCreateTableSql(tableName, jsonObject);
        String sql = getCreateTableSql(tableName, db);
        System.out.println(sql);
    }
    public static String getCreateTableSql(String tableName, JSONObject jsonObject) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE ").append(tableName).append(" (\n");
        boolean hasId = false;
        Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String columnName = entry.getKey();
            Object value = entry.getValue();

            String dataType = determineDataType(value);
            if (columnName.equals("id")) {//单独处理ID
                hasId = true;
                sqlBuilder.append("    ").append(columnName).append(" ").append("int(11) NOT NULL AUTO_INCREMENT");
            }else{
                sqlBuilder.append("    ").append(columnName).append(" ").append(dataType);
            }


            if (iterator.hasNext()) {
                sqlBuilder.append(",\n");
            }else{
                //最后一次
                if(hasId){
                    sqlBuilder.append(",\n");
                    sqlBuilder.append("    PRIMARY KEY (`id`)");
                }
            }
        }

        sqlBuilder.append("\n);");
        return sqlBuilder.toString();
    }
    public static String getCreateTableSql(String tableName, IData iadta) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE ").append(tableName).append(" (\n");
        boolean hasId = false;
        Iterator<Map.Entry<String, Object>> iterator = iadta.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String columnName = entry.getKey();
            Object value = entry.getValue();

            String dataType = determineDataType(value);
            if (columnName.equals("id")) {//单独处理ID
                hasId = true;
                sqlBuilder.append("    ").append(columnName).append(" ").append("int(11) NOT NULL AUTO_INCREMENT");
            }else{
                sqlBuilder.append("    ").append(columnName).append(" ").append(dataType);
            }

            if (iterator.hasNext()) {
                sqlBuilder.append(",\n");
            }else{
                //最后一次
                if(hasId){
                    sqlBuilder.append(",\n");
                    sqlBuilder.append("    PRIMARY KEY (`id`)");
                }
            }
        }

        sqlBuilder.append("\n);");
        return sqlBuilder.toString();
    }

    private static String determineDataType(Object value) {
        if (value instanceof Integer) {
            return "INT";
        } else if (value instanceof Long) {
            return "BIGINT";
        } else if (value instanceof Double || value instanceof Float) {
            return "DECIMAL";
        }  else if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            return "DECIMAL(19,"+bigDecimal.scale()+")";
        }else if (value instanceof Boolean) {
            return "BOOLEAN";
        } else if (value instanceof String) {
            return "VARCHAR(100)";
        } else if (value instanceof JSONObject) {
            // Handle nested JSON objects
            return "TEXT"; // or create a separate table
        } else if (value instanceof JSONArray) {
            // Handle JSON arrays
            return "TEXT"; // or create a separate table
        }else if (null == value) {
            // Handle JSON arrays
            return "VARCHAR(100)"; // or create a separate table
        } else {
            return "TEXT"; // Default to TEXT for unknown types
        }
    }
}

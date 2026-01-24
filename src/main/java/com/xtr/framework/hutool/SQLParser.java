package com.xtr.framework.hutool;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 以一种更加优雅的方式解析sql条件查询
 */
public class SQLParser
{
    private StringBuffer sqlstr = new StringBuffer();
    private IData param;
    private String group;

    public SQLParser(IData param)
    {
        this.param = param;
    }

    public SQLParser(IData param, String group)
    {
        this.param = param;
        this.group = group;
    }

    private List getTokens(String sql)
    {
        Pattern patParam = Pattern.compile("(:[\\w]*)");
        Pattern patQuote = Pattern.compile("('[^']*')");

        List quoteRanges = new ArrayList();
        Matcher matcher = patQuote.matcher(sql);
        while (matcher.find()) {
            DoubleRange r = new DoubleRange();
            r.start = matcher.start();
            r.text = matcher.group();
            r.length = r.text.length();
            quoteRanges.add(r);
        }
        matcher = patParam.matcher(sql);
        List keys = new ArrayList();
        while (matcher.find()) {
            String key = matcher.group().substring(1);
            if (!quoteRanges.isEmpty()) {
                boolean skip = false;
                int pos = matcher.start();
                Iterator it = quoteRanges.iterator();
                while (it.hasNext()) {
                    DoubleRange r = (DoubleRange)it.next();
                    if ((pos >= r.start) && (pos < r.start + r.length)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
            }
            keys.add(key);
        }
        return keys;
    }

    public void addSQL(String sql)
    {
        if ((this.group != null) && (!"".equals(this.group))) sql = sql.replaceAll(":" + this.group, ":");
        List names = getTokens(sql);
        if (names.isEmpty()) {
            this.sqlstr.append(sql);
        } else {
            for (int i = 0; i < names.size(); i++) {
                String name = (String)names.get(i);
                Object obj = this.param.get(name);
                String value = obj == null?null:obj.toString();
                if ((value == null) || ("".equals(value))) return;
            }
            this.sqlstr.append(sql);
        }
    }

    public String getSQL()
    {
        return this.sqlstr.toString();
    }

    public IData getParam()
    {
        return this.param;
    }

    public void addParser(SQLParser parser)
    {
        this.param.putAll(parser.getParam());
        this.sqlstr.append(parser.getSQL());
    }
}

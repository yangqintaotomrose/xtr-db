package com.xtr.framework.base.controller;


import cn.hutool.core.date.DateUtil;
import com.xtr.framework.base.domain.AjaxResult;
import com.xtr.framework.base.domain.AjaxResult.Type;
import com.xtr.framework.common.utils.DateUtils;
import com.xtr.framework.common.utils.ServletUtils;
import com.xtr.framework.common.utils.StringUtils;
import com.xtr.framework.hutool.IData;
import com.xtr.framework.hutool.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * web层通用数据处理
 *
 * @author hougt
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }


    /**
     * 获取request
     */
    public HttpServletRequest getRequest()
    {
        return ServletUtils.getRequest();
    }

    public String getStatMonth()
    {
        if(ServletUtils.getRequest().getHeader("STAT_MONTH") == null)
        {
            return DateUtil.offsetMonth(DateUtil.date(),-1).toString("yyyyMM");
        }
        return ServletUtils.getRequest().getHeader("STAT_MONTH");
    }

    /**
     * 获取response
     */
    public HttpServletResponse getResponse()
    {
        return ServletUtils.getResponse();
    }

    /**
     * 获取session
     */
    public HttpSession getSession()
    {
        return getRequest().getSession();
    }


    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? success() : error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    /**
     * 返回成功数据
     */
    public static AjaxResult success(Object data)
    {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    /**
     * 返回错误码消息
     */
    public AjaxResult error(Type type, String message)
    {
        return new AjaxResult(type, message);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }

    public Pagination getSinglePage(IData idata) {

        // 每页显示记录数
        int size = idata.getInt("pageSize", 20);
        int currPage =  idata.getInt("pageNum", 1);
        Pagination pagination = new Pagination();

        pagination.setCurrPage(currPage);
        pagination.setSize(size);
        return pagination;
    }


}


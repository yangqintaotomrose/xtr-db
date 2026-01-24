package com.xtr.framework.hutool;

import com.xtr.framework.base.domain.AjaxResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @Classname BaseHutoolController
 * @Description
 * @Date 2025/1/6 17:27
 * @Created by yangqintao
 */
public class BaseWebController {
    public IData getLoginUser(HttpServletRequest request)
    {
        //自己实现
//        String token = request.getHeader("mail_token");
//        //解密
//        String user_id = DESTool.decryptDES(token).split("_")[0];
        return BaseDao.getDao("").queryByFirst("select * from mgmt20.sm_user where user_id=?","10000");
    }

    public IData getRequestParam() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        if (null != request.getAttribute("request_param")) {
            return (IData) request.getAttribute("request_param");
        }
        IData param = new IData();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            String[] values = request.getParameterValues(key);
            String value = joinArray(values, ",");
            param.put(key.trim(), value.trim());
        }
        request.setAttribute("request_param", param);

        if(!(request.getRequestURI().indexOf("upload")>=0))
        {
            System.out.println(param);
        }

        IData header = getRequestHeader();
        System.out.println("-----------------------------");
        //System.out.println(header);
        if(!"-1".equals(header.getString("_user_id","-1")))
        {
            System.out.println("通过后台跳转过来的链接");
            param.set("_user_id",header.getString("_user_id","-1"));
        }
        //param.set("_user_id",header.getString("_user_id","-1"));
        return param;
    }

    /**
     * 获取页面请求的参数,第一次请求时创建，以后直接取request中的对象
     */
    public IData getIDataFromStream() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        if (null != request.getAttribute("request_param")) {
            return (IData) request.getAttribute("request_param");
        }
        IData param = new IData();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String key = (String) paramNames.nextElement();
            String[] values = request.getParameterValues(key);
            String value = values[0];
            param.put(key.trim(), value.trim());
        }

        request.setAttribute("request_param", param);

        if(!(request.getRequestURI().indexOf("upload")>=0))
        {
            System.out.println(param);
        }
        String json = readAsChars(request);
        if(json == null ||  json.length() == 0)
        {
             return param;
        }
        param.putAll(new IData(json));//追加数据到param里面
        return param;
    }

    /**
     * 获取页面请求的参数,第一次请求时创建，以后直接取request中的对象
     */
    public IDataset getIDatasetFromStream() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        if (null != request.getAttribute("request_param")) {
            return (IDataset) request.getAttribute("request_param");
        }
        String json = readAsChars(request);
        return new IDataset(json);
    }


    public String getStringFromStream() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return readAsChars(request);
    }

    public IData getRequestHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        IData param = new IData();
        Enumeration<String> paramNames = request.getHeaderNames();
        while (paramNames.hasMoreElements()) {
            String key = paramNames.nextElement().trim();
            String value = request.getHeader(key).trim();
            param.put(key, value);
        }
        return param;
    }

    public static String joinArray(String[] str, String val) {
        if (str == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            sb.append(str[i] + (i < str.length - 1 ? val : ""));
        }
        return sb.toString();
    }

    /**
     *
     * @param
     * @return 数据库查询分页对象
     * @desc 将前端传过来的参数转化成分页对象并返回，参数中必须要还有limit，offset 的键
     * @throws Exception
     */
    public Pagination getSinglePage(IData idata) {

        // 每页显示记录数
        int size = idata.getInt("pageSize", 20);
        int currPage =  idata.getInt("pageNum", 1);
        Pagination pagination = new Pagination();

        pagination.setCurrPage(currPage);
        pagination.setSize(size);
        return pagination;
    }

    public Pagination getExportPage() {

        // 每页显示记录数
        int size = 60000;
        int currPage =  0;
        Pagination pagination = new Pagination();
        pagination.setCurrPage(currPage);
        return pagination;
    }

    public static String readAsChars(HttpServletRequest request)
    {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try
        {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null)
            {
                sb.append(str);
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != br)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(sb);
        return sb.toString();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }


    /**
     * 返回成功
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(Object data)
    {
        return AjaxResult.success(data);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }


    public static IData fileToIdata(MultipartFile file) throws Exception {
        //加载excel
        XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();//带公式计算
        XSSFSheet sheet = wb.getSheetAt(0);
        int totalRows = sheet.getPhysicalNumberOfRows(); // 获取总行数
        System.out.println(totalRows);
        IDataset list = new IDataset();
        int totalColumns = 0;
        IData rowData = new IData();
        for (int rowIndex = 0; rowIndex <= totalRows; rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);

            if (row != null) {
                int numCells = row.getPhysicalNumberOfCells();
                if (numCells > totalColumns) {
                    totalColumns = numCells;
                }
                for (int i = 0; i <numCells; i++) {
                    XSSFCell cell =  row.getCell(i);
                    CellType cellType = cell.getCellType();
                    String tit = "r"+(rowIndex+1)+"_c" + (i+1);
                    switch (cellType) { // 假设cellType是预先定义的CellType枚举值
                        case STRING:
                            rowData.set(tit,cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            rowData.set(tit,cell.getNumericCellValue());
                            break;
                        case BOOLEAN:
                            rowData.set(tit,true);
                            break;
                        case FORMULA:
                            Cell newCell= evaluator.evaluateInCell(cell);
                            CellType newCellType = newCell.getCellType();
                            switch (newCellType) { // 假设cellType是预先定义的CellType枚举值
                                case STRING:
                                    rowData.set(tit,newCell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    rowData.set(tit,newCell.getNumericCellValue());
                                    break;
                                case BOOLEAN:
                                    rowData.set(tit,true);
                                    break;
                                case BLANK:
                                    rowData.set(tit,"");
                                    break;
                                default:
                                    throw new IllegalArgumentException("Unsupported cell type111: " + newCell);
                            }
                            //rowData.set("a"+i,cell.getCellFormula().toString());
                            break;
                        case BLANK:
                            rowData.set(tit,"");
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported cell type: " + cellType);
                    }

                }
                // list.add(rowData);
            }
        }
        System.out.println(rowData);
        return rowData;
    }

}

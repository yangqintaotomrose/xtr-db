package com.xtr.framework.hutool;

/**
 * @Classname Pagination
 * @Description
 * @Date 2022/6/28 11:01
 * @Created by yangqintao
 */
public class Pagination {
    public static final int MAX_PAGE_SIZE = 500;
    public static final int MAX_RECODE_SIZE = 100000000;
    private int start;
    private int size;
    private int count;
    private int currPage;
    private int pageCount;

    public Pagination() {
    }

    public Pagination(int currentPase,int size) {
        this.currPage = currentPase;
        this.size = size;

    }

    public int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }


    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return this.start;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrPage() {
        return this.currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public static Pagination getSinglePage(IData idata) {

        // 每页显示记录数
        int size = idata.getInt("pageSize", 30);
        int currPage =  idata.getInt("pageNum", 1);
        if(currPage ==0)
        {
            currPage = 1;
        }
        Pagination pagination = new Pagination();

        pagination.setCurrPage(currPage);
        pagination.setSize(size);
        return pagination;
    }

}

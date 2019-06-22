package com.caiyi.lottery.tradesystem.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *分页通用数据封装对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Page<T> {
    private Integer pageSize; // 每页大小
    private Integer pageNumber; // 页码
    private Integer totalPages; // 总共页数
    private Long totalRecords; // 总共条数
    private Boolean hasNextPage;//是否有下一页
    private T Datas;

    public Page() {
    }

    public Page(Integer pageSize, Integer pageNumber, Integer totalPages, Long totalRecords) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.totalRecords = totalRecords;
    }

    public Page(Integer pageSize, Integer pageNumber, Integer totalPages, Long totalRecords, T datas) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.totalRecords = totalRecords;
        Datas = datas;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Boolean getHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(Boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public T getDatas() {
        return Datas;
    }

    public void setDatas(T datas) {
        Datas = datas;
    }
}
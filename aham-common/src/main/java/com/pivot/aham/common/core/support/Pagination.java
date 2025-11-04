//package com.pivot.aham.common.core.support;
//
//import com.google.common.collect.Lists;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//
//import java.io.Serializable;
//import java.util.Collections;
//import java.util.List;
//
///**
// * 与业务层交互的自定义分页实体
// *
// * @author addison
// * @since 2018年11月16日
// */
//@SuppressWarnings("serial")
//@ApiModel(value = "分页对象说明")
//public class Pagination<T> implements Serializable {
//    public Pagination() {
//        this.offset = NO_ROW_OFFSET;
//        this.limit = NO_ROW_LIMIT;
//    }
//
//    public Pagination(int current, int size) {
//        this(current, size, true);
//    }
//
//    public Pagination(int current, int size, boolean searchCount) {
//        this(current, size, searchCount, true);
//    }
//
//    public Pagination(int current, int size, boolean searchCount, boolean openSort) {
//        this.offset = offsetCurrent(current, size);
//        this.limit = size;
//        if (current > 1) {
//            this.current = current;
//        }
//        this.size = size;
//        this.searchCount = searchCount;
//        this.openSort = openSort;
//    }
//
//    public Pagination(int current, int size, String orderByField) {
//        this(current, size);
//        this.setOrderByField(orderByField);
//    }
//
//    public Pagination(int current, int size, String orderByField, boolean isAsc) {
//        this(current, size, orderByField);
//        this.setAsc(isAsc);
//    }
//
//    public static final int NO_ROW_OFFSET = 0;
//    public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;
//    private final int offset;
//    private final int limit;
//    /**
//     * 总数
//     */
//    @ApiModelProperty(value = "记录总数",required = true)
//    private long total;
//
//    /**
//     * 每页显示条数，默认 10
//     */
//    @ApiModelProperty(value = "每页显示条数，默认 10",required = true)
//    private int size = 10;
//
//    /**
//     * 当前页
//     */
//    @ApiModelProperty(value = "当前页，默认 1",required = true)
//    private int current = 1;
//
//    /**
//     * 查询总记录数（默认 true）
//     */
//    @ApiModelProperty(value = "查询总记录数（默认 true）",required = true)
//    private boolean searchCount = true;
//
//    /**
//     * 开启排序（默认 true） 只在代码逻辑判断 并不截取sql分析
//     *
//     * @see com.baomidou.mybatisplus.mapper.SqlHelper#fillWrapper
//     **/
//    @ApiModelProperty(value = "开启排序（默认 true）",required = true)
//    private boolean openSort = true;
//
//    /**
//     * 优化 Count Sql 设置 false 执行 select count(1) from (listSql)
//     */
//    @ApiModelProperty(value = "优化 Count Sql 设置 false 执行 select count(1) from (listSql)",required = true)
//    private boolean optimizeCountSql = true;
//
//    /**
//     * <p>
//     * SQL 排序 ASC 集合
//     * </p>
//     */
//    @ApiModelProperty(value = "SQL 排序 ASC 集合",required = true)
//    private List<String> ascs;
//    /**
//     * <p>
//     * SQL 排序 DESC 集合
//     * </p>
//     */
//    @ApiModelProperty(value = "SQL 排序 DESC 集合",required = true)
//    private List<String> descs;
//
//    /**
//     * 是否为升序 ASC（ 默认： true ）
//     *
//     * @see #ascs
//     * @see #descs
//     */
//    @ApiModelProperty(value = "是否为升序 ASC（ 默认： true ）",required = true)
//    private boolean isAsc = true;
//
//    /**
//     * <p>
//     * SQL 排序 ORDER BY 字段，例如： id DESC（根据id倒序查询）
//     * </p>
//     * <p>
//     * DESC 表示按倒序排序(即：从大到小排序)<br>
//     * ASC 表示按正序排序(即：从小到大排序)
//     *
//     * @see #ascs
//     * @see #descs
//     * </p>
//     */
//    @ApiModelProperty(value = "SQL 排序 ORDER BY 字段，例如： id DESC（根据id倒序查询）",required = true)
//    private String orderByField;
//    /**
//     * 查询数据列表
//     */
//    @ApiModelProperty(value = "数据列表",required = true)
//    private List<T> records = Lists.newArrayList();
//
//
//    public Long getTotal() {
//        return total;
//    }
//
//    public void setTotal(long total) {
//        this.total = total;
//    }
//
//    public Integer getSize() {
//        return size;
//    }
//
//    public void setSize(int size) {
//        this.size = size;
//    }
//
//    public long getPages() {
//        if (this.size == 0) {
//            return 0L;
//        }
//        long pages = (this.total - 1) / this.size;
//        pages++;
//        return pages;
//    }
//
//    public Integer getCurrent() {
//        return current;
//    }
//
//    public void setCurrent(int current) {
//        this.current = current;
//    }
//
//    public boolean isSearchCount() {
//        return searchCount;
//    }
//
//    public void setSearchCount(boolean searchCount) {
//        this.searchCount = searchCount;
//    }
//
//    public boolean isOpenSort() {
//        return openSort;
//    }
//
//    public void setOpenSort(boolean openSort) {
//        this.openSort = openSort;
//    }
//
//    public boolean isOptimizeCountSql() {
//        return optimizeCountSql;
//    }
//
//    public void setOptimizeCountSql(boolean optimizeCountSql) {
//        this.optimizeCountSql = optimizeCountSql;
//    }
//
//    public List<String> getAscs() {
//        return ascs;
//    }
//
//    public void setAscs(List<String> ascs) {
//        this.ascs = ascs;
//    }
//
//    public List<String> getDescs() {
//        return descs;
//    }
//
//    public void setDescs(List<String> descs) {
//        this.descs = descs;
//    }
//
//    public boolean isAsc() {
//        return isAsc;
//    }
//
//    public void setAsc(boolean isAsc) {
//        this.isAsc = isAsc;
//    }
//
//    public String getOrderByField() {
//        return orderByField;
//    }
//
//    public void setOrderByField(String orderByField) {
//        this.orderByField = orderByField;
//    }
//
//    public int getOffset() {
//        return offset;
//    }
//
//    public int getLimit() {
//        return limit;
//    }
//
//    public List<T> getRecords() {
//        return records;
//    }
//
//    public Pagination<T> setRecords(List<T> records) {
//        this.records = records;
//        return this;
//    }
//
//    /**
//     * <p>
//     * 计算当前分页偏移量
//     * </p>
//     *
//     * @param current 当前页
//     * @param size    每页显示数量
//     * @return
//     */
//    private int offsetCurrent(int current, int size) {
//        if (current > 0) {
//            return (current - 1) * size;
//        }
//        return 0;
//    }
//
//    /**
//     * <p>
//     * Pagination 分页偏移量
//     * </p>
//     */
//    private int offsetCurrent(Pagination<T> page) {
//        if (null == page) {
//            return 0;
//        }
//        return offsetCurrent(page.getCurrent(), page.getSize());
//    }
//
//}

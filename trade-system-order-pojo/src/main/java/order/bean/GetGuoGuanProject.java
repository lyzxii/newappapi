package order.bean;

import org.apache.commons.lang3.StringUtils;

/**
 * 过关统计查询参数对象
 */
public class GetGuoGuanProject {

    private int pageNo = 1;
    private int pageSize = 25;
    private String gid;
    private String pid; // 开始期号
    private String endpid; // 结束期号
    private String ggtype; // 过关方案类型（my-我的过关 jcfs-已结束的成功方案 jcus-未结束的成功方案 jcff-已结束的流产方案 jcuf-未结束的流产方案）

    private String nickID; // 登录的用户编号
    private long cacheTime; // 缓存开始时间
    private String cacheData; // 缓存数据

    private String sort = "bonus"; // 排序字段（默认）
    private String sortType = "descending"; // 排序方式（默认）

    public String getCacheData() {
        return cacheData;
    }

    public void setCacheData(String cacheData) {
        this.cacheTime = System.currentTimeMillis();
        this.cacheData = cacheData;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public String getNickID() {
        return nickID;
    }

    public void setNickID(String nickID) {
        this.nickID = nickID;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        if (pageNo <= 0) {
            this.pageNo = 1;
        } else {
            this.pageNo = pageNo;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize <= 0) {
            this.pageSize = 25;
        } else {
            this.pageSize = pageSize;
        }
    }

    public String getEndpid() {
        return endpid;
    }

    public void setEndpid(String endpid) {
        this.endpid = endpid;
    }

    public String getGgtype() {
        return ggtype;
    }

    public void setGgtype(String ggtype) {
        this.ggtype = ggtype;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    // 生成过关统计真实数据的缓存键值
    public String toSubKey(String pid) {
        if(!StringUtils.isEmpty(pid)){
            return gid + "_" + pid + "_" + ggtype;
        }
        return "";
    }

    // 缓存时间一个月
    public boolean expired() {
        if (System.currentTimeMillis() > cacheTime + 30 * 24 * 60 * 60 * 1000) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "彩种id=" + gid + ",期号pid=" + pid + ",期号endpid=" + endpid
                + ",过关类型ggtype=" + ggtype + ",页码pageNo=" + pageNo + ",排序sort="
                + sort + ",排序方式sortType=" + sortType;
    }
}

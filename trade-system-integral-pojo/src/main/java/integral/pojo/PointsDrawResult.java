package integral.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PointsDrawResult {
    private String status;
    private Integer point;
    private Integer cj_cnt;
    private Integer result;
    private String desc;
    private String cnickid;
    private Integer require_point;

    private Integer cnt;
    private Integer per_cnt;

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public Integer getPer_cnt() {
        return per_cnt;
    }

    public void setPer_cnt(Integer per_cnt) {
        this.per_cnt = per_cnt;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public Integer getRequire_point() {
        return require_point;
    }

    public void setRequire_point(Integer require_point) {
        this.require_point = require_point;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getCj_cnt() {
        return cj_cnt;
    }

    public void setCj_cnt(Integer cj_cnt) {
        this.cj_cnt = cj_cnt;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package integral.bean;

import com.caiyi.lottery.tradesystem.BaseBean;

public class PointsMallBean extends BaseBean{
    private String jf;
    private int flag;//中奖标识
    private String ex_goods_id;
    private String ex_goods_name;
    private int require_point;
    private String ex_goods_desc;
    private int ex_goods_cnt;
    private String type;
    private int userExCnt;
    private int goodsExCnt;
    private String checkStatus="";
    private String checkResult;


    public String getJf() {
        return jf;
    }

    public void setJf(String jf) {
        this.jf = jf;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getEx_goods_id() {
        return ex_goods_id;
    }

    public void setEx_goods_id(String ex_goods_id) {
        this.ex_goods_id = ex_goods_id;
    }

    public String getEx_goods_name() {
        return ex_goods_name;
    }

    public void setEx_goods_name(String ex_goods_name) {
        this.ex_goods_name = ex_goods_name;
    }

    public int getRequire_point() {
        return require_point;
    }

    public void setRequire_point(int require_point) {
        this.require_point = require_point;
    }

    public String getEx_goods_desc() {
        return ex_goods_desc;
    }

    public void setEx_goods_desc(String ex_goods_desc) {
        this.ex_goods_desc = ex_goods_desc;
    }

    public int getEx_goods_cnt() {
        return ex_goods_cnt;
    }

    public void setEx_goods_cnt(int ex_goods_cnt) {
        this.ex_goods_cnt = ex_goods_cnt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserExCnt() {
        return userExCnt;
    }

    public void setUserExCnt(int userExCnt) {
        this.userExCnt = userExCnt;
    }

    public int getGoodsExCnt() {
        return goodsExCnt;
    }

    public void setGoodsExCnt(int goodsExCnt) {
        this.goodsExCnt = goodsExCnt;
    }


    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }
}

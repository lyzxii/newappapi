package integral.pojo;

import java.util.ArrayList;
import java.util.List;


public class ExchangeStatus {
    private String goods_status;
    private String goods_desc;
    private List<PointsMallGood> good_detail=new ArrayList<>();

    public String getGoods_status() {
        return goods_status;
    }

    public void setGoods_status(String goods_status) {
        this.goods_status = goods_status;
    }

    public String getGoods_desc() {
        return goods_desc;
    }

    public void setGoods_desc(String goods_desc) {
        this.goods_desc = goods_desc;
    }

    public List<PointsMallGood> getGood_detail() {
        return good_detail;
    }

    public void setGood_detail(List<PointsMallGood> good_detail) {
        this.good_detail = good_detail;
    }
}

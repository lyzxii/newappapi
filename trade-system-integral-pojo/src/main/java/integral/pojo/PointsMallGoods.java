package integral.pojo;

import java.util.ArrayList;
import java.util.List;


public class PointsMallGoods {

   private String jf;

   private List<PointsMallGood> goodsList=new ArrayList<>();


    public String getJf() {
        return jf;
    }

    public void setJf(String jf) {
        this.jf = jf;
    }

    public List<PointsMallGood> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<PointsMallGood> goodsList) {
        this.goodsList = goodsList;
    }
}

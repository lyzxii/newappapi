package integral.pojo;

/**
 * @Author: wang tao
 * @Date: created in 17:33 2017/12/8
 * @Description:
 */
public class ExchangeGood{
    private String jf;
    private int userExCnt;
    private int goodsExCnt;

    public ExchangeGood() {

    }

    public ExchangeGood(String jf, int userExCnt, int goodsExCnt) {
        this.jf = jf;
        this.userExCnt = userExCnt;
        this.goodsExCnt = goodsExCnt;
    }

    public String getJf() {
        return jf;
    }

    public void setJf(String jf) {
        this.jf = jf;
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
}

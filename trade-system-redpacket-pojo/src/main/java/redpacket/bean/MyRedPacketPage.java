package redpacket.bean;

import com.caiyi.lottery.tradesystem.bean.Page;

public class MyRedPacketPage extends Page{

    private Integer canUseNum;//可使用数量
    private Integer watingNum;//待派发数量

    public MyRedPacketPage() {
    }

    public MyRedPacketPage(Integer pageSize, Integer pageNumber, Integer totalPages,
                           Long totalRecords, Object datas, int canUseNum, int watingNum) {
        super(pageSize, pageNumber, totalPages, totalRecords, datas);
        this.canUseNum = canUseNum;
        this.watingNum = watingNum;
    }

    public int getCanUseNum() {
        return canUseNum;
    }

    public void setCanUseNum(int canUseNum) {
        this.canUseNum = canUseNum;
    }

    public int getWatingNum() {
        return watingNum;
    }

    public void setWatingNum(int watingNum) {
        this.watingNum = watingNum;
    }
}

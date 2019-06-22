package order.bean;

import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

public class RankingBean {
    private long time;
    private JXmlWrapper xml;
    public boolean needUpdate(){
        return (System.currentTimeMillis() - time) / 1000 > 5 * 60;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public JXmlWrapper getXml() {
        return xml;
    }

    public void setXml(JXmlWrapper xml) {
        this.xml = xml;
    }
}

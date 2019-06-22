package bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

public class PushBean {


    private String openKey = "";//开关
    //个推返回tag值
    private String tag = "";
    //小米



    public String getOpenKey() {
        return openKey;
    }

    public void setOpenKey(String openKey) {
        this.openKey = openKey;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}

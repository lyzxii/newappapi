package dto;

import com.alibaba.fastjson.JSONObject;
import com.gexin.fastjson.JSON;
import lombok.Data;

import java.util.List;

@Data
public class ZhRecordResults<T> {
    private List<T> array;
    private String tr;
    private String tp;
    private String ps;
    private String pn;

    public String toJsonString() {
        JSONObject json = new JSONObject();
        String str = "";
        if (null != array)
            str = JSON.toJSONString(array);
        json.put("tr", this.tr);
        json.put("tp", this.tp);
        json.put("ps", this.ps);
        json.put("pn", this.pn);
        json.put("data", str);
        return json.toJSONString();
    }
}

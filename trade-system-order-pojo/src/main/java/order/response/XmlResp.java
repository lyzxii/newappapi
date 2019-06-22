package order.response;

import com.caiyi.lottery.tradesystem.base.BaseResp;
import order.dto.XmlDTO;

import static com.caiyi.lottery.tradesystem.util.xml.XmlUtil.XML_HEAD;

/**
 * Created by tiankun on 2017/12/28.
 */
public class XmlResp extends BaseResp<XmlDTO> {

    //转为标准xml文件格式
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(XML_HEAD);
        sb.append("<Resp code=\"" + this.getCode() + "\" desc=\"" + this.getDesc() + "\">");
        sb.append(this.getData().getBusiXml());
        sb.append("</Resp>");
        return sb.toString();
    }

}

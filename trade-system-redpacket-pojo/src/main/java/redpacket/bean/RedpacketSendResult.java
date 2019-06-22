package redpacket.bean;

import lombok.Data;

@Data
public class RedpacketSendResult {
    private int busiErrCode;
    private String busiErrDesc;
    private String cupacketid;
}

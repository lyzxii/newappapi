package redpacket.bean;

import lombok.Data;

@Data
public class UserRedpacket {
    private String redpacketId; //cupacketid
    private String redpacketName;//crpname
    private int tid;//itid
    private String imoney;//imoney
    private String balance;//irmoney
    private String deaddate;//cdeaddate
    private String scale;//scale
    private String gameid="";//cgameid
    private String availableBalance="";//
    private String availableAgent="";//cagent
    private String availableSource="";//isource
    private String vipuse;//vipuse
    private String rpid;//红包id
}

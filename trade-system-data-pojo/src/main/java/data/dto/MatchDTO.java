package data.dto;

import lombok.Data;

/**
 * @author GJ
 * @create 2018-01-18 11:42
 **/
@Data
public class MatchDTO {

    private String rid;
    private String sid;//篮球mid
    private String lid;
    private String ln;
    private String time;
    private String htime;
    private String hn;
    private String gn;
    private String homeRank;
    private String guestRank;
    private String hid;
    private String gid;
    private String hsc;
    private String asc;
    private String halfsc;
    private String type;
    private String jn;
    private String roundItemId;
    private String qc;
    private String sort;
    private String tvlive;
    private String isfriendly;
    private String isfiveleague;
    private String swapTeam;// 是否交换两支队伍
    private String viewer; // 在线查看人数

    //完赛
    private String cg;////篮球sg
    private String odds;
    private String rq;
    private String iaudit;

    //篮球
    private String st;
    private String down;
    private String dx;
    private String rf;



}

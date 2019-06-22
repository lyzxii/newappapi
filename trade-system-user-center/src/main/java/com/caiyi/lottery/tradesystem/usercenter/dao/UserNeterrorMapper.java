package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import pojo.CalculateNeterrorPojo;

/**
 * Created by tiankun on 2017/12/7.
 * 统计网络错误信息
 */
@Mapper
public interface UserNeterrorMapper {

    @Insert("INSERT INTO TB_USER_NETERROR (ID, CNICKID, CERRORTIME," +
            "CAPPNAME, CAPPPLATFORM,CAPPVERSION,CSOURCE,LOCALDNS,PINGRESULT,DNSRESOLUTION,LOCALIP,RESPONCONTENT," +
            "CIPADDRESS,CCITY,CORIGINALDOMAIN,CSWITCHDOMAIN,CERRORURL,CERRORDESC,CURLTYPE,IPDETAIL," +
            "NETTYPE,OPERATORTYPE,OSVERSION,UPLOADDOMAIN) " +
            "VALUES(seq_user_neterror.NEXTVAL,#{cnickid},to_date(#{cerrortime},'yyyy-MM-dd hh24:mi:ss'),#{cappname},#{cappplatform},#{cappversion},#{csource},#{localdns},#{pingresult},#{dnsresolution},#{localip},#{responcontent}" +
            ",#{cipaddress},#{ccity},#{coriginaldomain},#{cswitchdomain},#{cerrorurl},#{cerrordesc},#{curltype},#{ipdetail},#{nettype},#{operatortype},#{osversion},#{uploaddomain})")
    int insertUserNeterror(CalculateNeterrorPojo calculateNeterrorPojo);
}

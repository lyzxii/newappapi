package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import pojo.UserpingNeterrorPojo;

/**
 * Created by tiankun on 2017/12/6.
 * 用户检测网络统计错误信息
 */
@Mapper
public interface UserpingNeterrorMapper {

    @Insert("insert into tb_userping_neterror(u_id,errordesc,nettype,operatortype,osversion,dnsresolution,username,errortime,appname,appplatform,appversion,localip,localdns,pingresult)" +
            "VALUES (seq_userping_neterror.NEXTVAL,#{errordesc},#{nettype},#{operatortype},#{osversion},#{dnsresolution},#{username},to_date(#{errortime},'yyyy-MM-dd hh24:mi:ss')," +
            "#{appname},#{appplatform},#{appversion},#{localip},#{localdns},#{pingresult})")
    int insertUserImei(UserpingNeterrorPojo userpingNeterrorPojo);
    

}

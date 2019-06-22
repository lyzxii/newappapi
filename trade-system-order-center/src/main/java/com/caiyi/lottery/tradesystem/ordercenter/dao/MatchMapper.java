package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.dto.ZucaiMatchDTO;
import order.pojo.MatchPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * tb_match
 *
 * @author GJ
 * @create 2018-01-15 9:33
 **/
@Mapper
public interface MatchMapper {
    //select cperiodid expect,imatchid mid,cmname hn,csname gn,cbegintime bt,cendtime et,cbet3 b3,cbet1 b1,cbet0 b0,cmscore ms, csscore ss, cresult rs,close,cmatchname mname,cspf spf,cbqc bqc, ccbf cbf, cjqs jqs,csxp sxp,ccolor cl  from tb_match where itype = ? and cperiodid = ? and isale=0 order by imatchid
    @Select("select imatchid mid,cmname hn,csname gn,cbegintime bt,cmscore ms, csscore ss, cresult rs  from tb_match where itype = #{type} and cperiodid = #{periodid} and isale=0 order by imatchid")
    List<ZucaiMatchDTO> queryMatchList(@Param("type") Integer type, @Param("periodid") String periodid);
}

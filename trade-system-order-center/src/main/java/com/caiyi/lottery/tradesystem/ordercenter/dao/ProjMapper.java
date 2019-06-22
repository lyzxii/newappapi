package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * Created by tiankun on 2017/12/21.
 * tb_proj_xx表
 */
@Mapper
public interface ProjMapper {

    QueryProjPojo queryPinfo(@Param("gid") String gid, @Param("hid") String hid);

    QueryProjAppPojo queryProjectinfo(@Param("gid") String gid, @Param("hid") String hid);

    @Select("select 1 type, cprojid projid,itmoney money,itax tax, ibonus bonus, ccodes codes,ijiesuan,istate,iaward,ireturn ireturn," +
            "decode(iaward,2," +
            "decode(ibonus,0,decode(istate,3,7,4,7,9)," +
            "decode(istate,3,8,4,8,10))," +
            "decode(istate,3,5,4,5,6)) st " +
            "from tb_proj_${gid} where cnickid = #{uid} and cperiodid= #{tid}")
    List<ProjZhPojo> queryProjDetail(@Param("gid") String gid, @Param("uid") String uid, @Param("tid") String tid);


    @Select("select 1 type,cperiodid pid,decode(icast,3,1,0) st,ibonus bonus from tb_proj_${gid} where cnickid = #{uid} and cperiodid>=#{tid}")
    List<ZhProjPojo> queryCastDetail(@Param("gid") String gid, @Param("uid") String uid, @Param("tid") String tid);

    @Select("select cnickid,cgameid,cperiodid,ccodes,imulity,itmoney,iopen,cendtime,imoneyrange,itype,cguoguan,cmatchs,iminrange,isource,extendtype from tb_proj_${gid}  where cprojid = #{cprojid}")
    List<ProjPojo> queryProjInfo(@Param("gid")String gid,@Param("cprojid")String cprojid);

    @Update("update tb_proj_${gid} set itype = 2 where cprojid = #{hid}")
    int updateProjItype(@Param("gid")String gid,@Param("hid")String hid);

    /**
     * 查询投注人数
     * @param gid 彩种id
     * @param pid 期次
     * @return
     */
    @Select("select count(1) from tb_proj_${gid} where cperiodid = #{pid}")
    Integer queryBetNum(@Param("gid") String gid, @Param("pid") String pid);
}

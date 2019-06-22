package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.dto.ZhuihaoDTO;
import order.pojo.ProjZhPojo;
import order.pojo.ZhProjPojo;
import order.pojo.ZhuihaoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ZhuiHaoMapper {

    @Select("select t.cnickid,t.ihide,t.ireason,t2.isreturn " +
            "from tb_zhuihao_${gid} t,tb_zh_detail_${gid} t2 " +
            "where t.czhid=#{pid} and t.czhid=t2.czhid")
    List<ZhuihaoDTO> selectZhuiHaoDetail(@Param("gid") String gid, @Param("pid") String pid);

    @Update("update tb_zhuihao_${gid} set ihide=1,chidedate=sysdate where czhid=#{pid}")
    int updateZhDetail(@Param("gid") String gid, @Param("pid") String pid);

    @Select("select 2 type, czhid projid,icmoney money,itax tax,iamoney bonus,ccodes codes,ijiesuan,istate,iaward,isreturn ireturn,decode(iaward,2,decode(iamoney,0,decode(istate,3,7,4,7,9)，decode(istate,3,8,4,8,10)),decode(istate,3,5,4,5,6)) st " +
            "from tb_zh_detail_${gid} where cnickid = #{uid} and cperiodid=#{tid}")
    List<ProjZhPojo> queryZhDetail(@Param("gid") String gid, @Param("uid") String uid, @Param("tid") String tid);

    @Select("select 2 type, cperiodid pid,decode(istate,2,1,0) st, iamoney bonus from tb_zh_detail_${gid} where cnickid = #{uid} and cperiodid>=#{tid}")
    List<ZhProjPojo> queryZhList(@Param("gid") String gid, @Param("uid") String uid, @Param("tid") String tid);
}

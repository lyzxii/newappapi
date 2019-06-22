package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.QueryProjAppPojo;
import order.pojo.ShareGodProjPojo;
import order.pojo.ShareListPojo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by tiankun on 2017/12/22.
 */

@Mapper
public interface ShareListMapper {
    @Select("select t1.ibonus as rmoney, sum(t2.ireward) as owins from tb_sharelist t1, tb_followlist t2 "
            + "where t1.cnickid = #{nickid} and t1.cprojid = #{hid} and t1.cprojid = t2.cshareprojid group by t1.ibonus")
    QueryProjAppPojo queryShareUserBous(@Param("nickid") String nickid, @Param("hid") String hid);

    /**
     * 获取打赏金额
     * @param nickid
     * @param hid
     * @return
     */
    @Select("select ibonus as rmoney,ireward as owins from tb_followlist t where CNICKID = #{nickid} and CPROJID = #{hid}")
    QueryProjAppPojo queryFollowUserBouns(@Param("nickid") String nickid, @Param("hid") String hid);

    /**
     * 获取跟单用户名
     * @param hid
     * @return
     */
    @Select("select CNICKID from tb_sharelist where CPROJID = (select CSHAREPROJID from tb_followlist where CPROJID = #{hid})")
    String queryNickid(@Param("hid") String hid);

    /**
     * 获取打赏比率
     * @param hid
     * @return
     */
    @Select("select iwrate from tb_followlist where cprojid = #{hid}")
    String queryFollowListWrate(@Param("hid") String hid);

    /**
     * 是跟投的单子
     *
     * @param hid
     * @return
     */
    @Select("select clastdate from tb_sharelist where cprojid = (select cshareprojid from tb_followlist where cprojid = #{hid})")
    Date queryShareListAndFollowListByHid(@Param("hid") String hid);

    /**
     * 是分享的单子
     *
     * @param hid
     * @return
     */
    @Select("select clastdate from tb_sharelist where cprojid = #{hid}")
    Date queryShareListByHid(@Param("hid") String hid);

    /**
     * 查询彩种状态
     *
     * @param hid
     * @return
     */
    @Select("select cnickid nickid,cgameid gameid,cperiodid period,ccodes codes,imulity mulity,itmoney tmoney,cmatchs matches,ifollowmoney followmoney, extendtype," +
            "iopen open,cendtime endtime,cadddate adddate,ibonus bonus,ifinish finish,ifollownums follows,iwrate wrate,iusernums usernum,clastdate lastdate " +
            "from tb_sharelist where cprojid = #{hid}")
    ShareListPojo queryShareProjStatus(@Param("hid") String hid);

    /**
     * 查询其他彩种状态
     *
     * @param nickid
     * @param hid
     * @return
     */
    @Select("select s.cnickid nickid,s.cendtime endtime,s.itmoney tmoney,s.cgameid gameid,s.cperiodid period,s.ccodes codes,s.imulity mulity,s.iwrate wrate,s.cmatchs matches,s.cguoguan guoguan,s.iusernums usernum,s.cprojid projid,s.extendtype extendtype "
            + "from tb_sharelist s where to_char(sysdate+5/(24*60),'yyyy/mm/dd hh24:mi:ss') < to_char(s.cendtime,'yyyy/mm/dd hh24:mi:ss' ) "
            + "and s.cvisiable = '1' and s.cnickid = #{nickid} and s.cprojid <> #{hid}")
    List<ShareListPojo> queryOtherItemAll(@Param("nickid") String nickid, @Param("hid") String hid);

    /**
     * 查询其他彩种状态
     *
     * @param nickid
     * @param hid
     * @return
     */
    @Select("select s.cnickid nickid,s.cendtime endtime,s.itmoney tmoney,s.cgameid gameid,s.cperiodid period,s.ccodes codes,s.imulity mulity,s.iwrate wrate,s.cmatchs matches,s.cguoguan guoguan,s.iusernums usernum,s.cprojid projid,s.extendtype extendtype "
            + "from tb_sharelist s where to_char(sysdate+5/(24*60),'yyyy/mm/dd hh24:mi:ss') < to_char(s.cendtime,'yyyy/mm/dd hh24:mi:ss') "
            + "and s.cvisiable = '1' and  (select nvl(sum(itmoney),0) tmoney from tb_followlist where cshareprojid = s.CPROJID) < 10*s.ITMONEY and s.cnickid = #{nickid} and s.cprojid <> #{hid}")
    List<ShareListPojo> queryOtherItem(@Param("nickid") String nickid, @Param("hid") String hid);

    /**
     * 根据天数查询发单人数
     *
     * @param dayNum
     * @return
     */
    @Select("select count(1) from (select distinct cnickid from tb_sharelist where cadddate >=  sysdate - #{dayNum})")
    int shareUserNumByDay(@Param("dayNum") String dayNum);

    /**
     * 查询大神进行中的分享神单数
     *
     * @param nickid
     * @return
     */
    @Select("select count(1) from tb_sharelist s where to_char(sysdate+5/(24*60),'yyyy/mm/dd hh24:mi:ss') < to_char(s.cendtime,'yyyy/mm/dd hh24:mi:ss') "
            + "and s.cvisiable = '1' and s.cnickid = #{nickid} and  (select nvl(sum(itmoney),0) tmoney from tb_followlist where cshareprojid = s.CPROJID) < 10*s.ITMONEY")
    int queryGodGoingProjNum(@Param("nickid") String nickid);

    /**
     * 根据优先级查询
     *   yhmoney  codes
     * @return
     */
    /*@Select("select cnickid nickid,cendtime endtime,itmoney tmoney,imulity mulity,extendtype,ccodes codes,cgameid gameid,cperiodid period,"
            + "iwrate wrate,cmatchs matches,cguoguan guoguan,iusernums usernum,cprojid projid "
            + "from tb_sharelist where to_char(sysdate+5/(24*60),'yyyy/mm/dd hh24:mi:ss') < to_char(cendtime,'yyyy/mm/dd hh24:mi:ss') and cpriority<>0 and cvisiable = '1' order by to_number(cpriority) desc,itmoney desc")*/
    @Select("select s.cnickid nickid,s.cendtime endtime,s.itmoney tmoney,s.imulity mulity,s.extendtype extendtype,yhmoney,s.cgameid gameid,s.cperiodid period,s.ccodes codes,"
            + "s.iwrate wrate,s.cmatchs matches,s.cguoguan guoguan,s.iusernums usernum,s.cprojid projid "
            + "from tb_sharelist s where to_char(sysdate+5/(24*60),'yyyy/mm/dd hh24:mi:ss') < to_char(s.cendtime,'yyyy/mm/dd hh24:mi:ss') "
            + "and s.cpriority<>0 and s.cvisiable = '1' and  (select nvl(sum(itmoney),0) tmoney from tb_followlist where cshareprojid = s.CPROJID) < 10*s.ITMONEY "
            + "order by to_number(s.cpriority) desc,s.itmoney desc")
    List<ShareListPojo> queryGodShareByPriority();

    //根据投注金额从大到小，起投金额大于5000排序(大神单前5条数据)
    @Select("select s.cnickid nickid,s.cendtime endtime,s.itmoney tmoney,s.imulity mulity,s.iwrate wrate,s.cmatchs matches,s.cguoguan guoguan,s.iusernums usernum,s.cprojid projid,s.extendtype extendtype "
            + "from tb_sharelist s where to_char(sysdate + 5 / (24 * 60), 'yyyy/mm/dd hh24:mi:ss') < to_char(s.cendtime, 'yyyy/mm/dd hh24:mi:ss') and s.cvisiable = '1' and s.yhmoney <= 5000 "
            + "and  (select nvl(sum(itmoney),0) tmoney from tb_followlist where cshareprojid = s.CPROJID) < 10*s.ITMONEY "
            + "order by s.itmoney desc, s.iusernums desc, s.yhmoney,s.iwrate,s.cendtime,s.cnickid")
    List<ShareListPojo> queryGodFirst5();

    //查询该用户金额最大,起投金额小于5000的用户
    @Select( "select * from (select s.cnickid nickid,s.cendtime endtime,s.itmoney tmoney,s.imulity mulity,s.iwrate wrate,s.cmatchs matches,s.cguoguan guoguan,s.iusernums usernum,yhmoney,s.cprojid projid,s.extendtype extendtype,s.cgameid gameid,s.cperiodid period,s.ccodes codes"
            + " from tb_sharelist s"
            + " where  to_char(sysdate+5/(24*60),'yyyy/mm/dd hh24:mi:ss') < to_char(s.cendtime,'yyyy/mm/dd hh24:mi:ss') and s.cvisiable = '1' and s.yhmoney <= 5000 and s.cnickid = #{nickid} "
            + " and  (select nvl(sum(itmoney),0) tmoney from tb_followlist where cshareprojid = s.CPROJID) < 10*s.ITMONEY "
            + "order by s.itmoney desc,s.iusernums desc,s.yhmoney,s.iwrate,s.cendtime,s.cnickid) where rownum<=1 ")
    List<ShareListPojo> queryMaxItem(@Param("nickid") String nickid);

    @Select("select max(itmoney)/min(itmoney) as moneyper from "
            + "(select * from tb_sharelist t where t.cnickid = #{nickid} order by t.cadddate desc)"
            + "where rownum < 11")
    List<String> queryBuyRate(@Param("nickid") String nickid);

    /**
     * 查询分享神单金额最大的用户
     * @param num
     * @return
     */
    @Select("select * from ("
            + "select cnickid nickid , max(itmoney) tmoney from "
            + "(select * from tb_sharelist where  to_char(sysdate+5/(24*60),'yyyy/mm/dd hh24:mi:ss') < to_char(cendtime,'yyyy/mm/dd hh24:mi:ss') and cvisiable = '1' "
            + "order by itmoney desc,iusernums desc,(itmoney/imulity),iwrate,cendtime,cnickid) "
            + "group by cnickid order by max(itmoney) desc) "
            + "where rownum <= #{num}")
    List<ShareListPojo> queryShareMaxMoney(@Param("num") int num);

    /**
     * 查询大神最大金额的一笔单子
     * @param nickid
     * @return
     */
    @Select("select * from (select cnickid nickid,cendtime endtime,itmoney tmoney,imulity mulity,extendtype,ccodes codes,cgameid gameid,cperiodid period,"
            + "iwrate wrate,cmatchs matches,cguoguan guoguan,iusernums usernum,cprojid projid "
            + "from tb_sharelist where to_char(sysdate+5/(24*60),'yyyy/mm/dd hh24:mi:ss') < to_char(cendtime,'yyyy/mm/dd hh24:mi:ss') and cnickid = #{nickid} and cvisiable = '1' order by tmoney desc) where rownum <=1")
    List<ShareListPojo> queryGodBiggestItem(@Param("nickid") String nickid);

    /**
     * 根据投注金额查询
     * @param num
     * @return
     */
    @Select("select * from (select cnickid nickid,cendtime endtime,itmoney tmoney,imulity mulity,extendtype,ccodes codes,cgameid gameid,cperiodid period,"
            + "iwrate wrate,cmatchs matches,cguoguan guoguan,iusernums usernum,cprojid projid "
            + "from tb_sharelist where to_char(sysdate+5/(24*60),'yyyy/mm/dd hh24:mi:ss') < to_char(cendtime,'yyyy/mm/dd hh24:mi:ss') and cvisiable = '1' order by tmoney desc,iusernums desc,yhmoney,iwrate,cendtime,cnickid) where rownum<= #{num}")
    List<ShareListPojo> queryGodShareByMoney(@Param("num") int num);

    /**
     * 时间限制15日以后，免除历史数据影响
     * @param nickid
     * @return
     */
    @Select("SELECT nvl(sum(b.IREWARD),0) returnmoney FROM tb_sharelist a ,tb_followlist b where a.cprojid = b.cshareprojid  and a.ifinish = 1 and a.cnickid = #{nickid} and a.cadddate > to_date('2016-10-15','yyyy-mm-dd')")
    String queryProjRewardall(@Param("nickid") String nickid);

    /**
     * 神单数据
     * @param newValue
     * @param qtype
     * @return
     */
    List<ShareListPojo> queryShareGodProj(@Param("newValue") String newValue,@Param("qtype") String qtype);

    /**
     * 神单数据
     * @param newValue
     * @param qtype
     * @return
     */
    List<ShareListPojo> queryShareGodProj_0(@Param("newValue") String newValue,@Param("qtype") String qtype);

    /**
     * 查询大神榜单(发单金额大到小)
     * @return
     */
    List<ShareListPojo> query_god_proj_1();

    /**
     * 查询大神榜单(发单金额小到大)
     * @return
     */
    List<ShareListPojo> query_god_proj_2();

    /**
     * 查询大神榜单(起投金额大到小)
     * @return
     */
    List<ShareListPojo> query_god_proj_3();

    /**
     * 查询大神榜单(起投金额小到大)
     * @return
     */
    List<ShareListPojo> query_god_proj_4();

    /**
     * 查询大神榜单(人气大到小)
     * @return
     */
    List<ShareListPojo> query_god_proj_5();

    /**
     * 查询当日分享方案个数
     */
    @Select("SELECT count(1) FROM tb_sharelist t where t.cnickid = #{cnickid} and t.cadddate >  to_date(#{curdate},'yyyy-mm-dd')")
    public int queryShareRecordNums(@Param("cnickid") String cnickid,@Param("curdate") String curdate);

    /**
     * 查询三天内用户发单所包含的场次ID
     */
    @Select("select t.cprojid projid,t.cgameid gameid,t.cperiodid period,t.cmatchs matches,t.extendtype extendtype,t.cadddate adddate from tb_sharelist t where t.cnickid = #{cnickid}  and t.cadddate >= sysdate - 3 and ifinish=0")
    public List<ShareListPojo> getSameItemid(@Param("cnickid") String cnickid);

    /**
     * 查询分享神单表  查看之前有没有分享过同样的方案
     */
    @Select("select ccodes codes from tb_sharelist where cnickid=#{cnickid} and cgameid=#{cgameid} and cperiodid=#{cperiodid}")
    public List<ShareListPojo> queryComShareList(@Param("cnickid") String cnickid,@Param("cgameid") String cgameid,@Param("cperiodid") String cperiodid);

    /**
     * 查询分享神单表  查看之前有没有分享过同样的方案
     */
    @Insert("insert into tb_sharelist(cprojid, cnickid, cgameid, cperiodid, ccodes, imulity, itmoney, iopen,cendtime, imoneyrange, extendtype,iwrate,imintmoney,yhmoney,cmatchs,cguoguan,clastdate) values (#{hid}, #{nickid}, #{gid}, #{periodid}, #{codes}, #{mulity}, #{tmoney}, #{iopen}, to_date(#{endtime}, 'yyyy-mm-dd hh24:mi:ss'), #{moneyrange},#{extendtype},#{iwrate},#{imintmoney},#{yhmoney},#{cmatchs},#{cguoguan},to_date(#{btime}, 'yyyy-mm-dd hh24:mi:ss'))")
    public int insertShareList(@Param("hid") String hid,@Param("nickid") String nickid,@Param("gid") String gid,@Param("periodid") String periodid,@Param("codes") String codes,@Param("mulity") String mulity,@Param("tmoney") String tmoney,@Param("iopen") String iopen,@Param("endtime") String endtime,@Param("moneyrange") String moneyrange,@Param("extendtype") String extendtype,@Param("iwrate") String iwrate,@Param("imintmoney") String imintmoney,@Param("yhmoney") String yhmoney,@Param("cmatchs") String cmatchs,@Param("cguoguan") String cguoguan,@Param("btime") String btime);

    /**
     * 查询方案的购买总金额
     */
    @Select("select nvl(sum(itmoney),0) tmoney from tb_followlist where cshareprojid = #{newValue}")
    public String queryFollowTMoney(@Param("newValue") String newValue);

    /**
     * 查询跟买数据
     * @param newValue
     * @return
     */
    @Select("select iusernums usernum,ibonus bonus from tb_sharelist where cprojid = #{newValue}")
    ShareListPojo queryFollowData(@Param("newValue") String newValue);
}

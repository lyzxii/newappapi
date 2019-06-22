package com.caiyi.lottery.tradesystem.usercenter.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pojo.Adsense;

@Mapper
public interface AdsenseMapper {
	
	@Select("select count(*) from tb_adsense where (cidfa = #{aid} or ccip = #{ip}) and  cadddate > sysdate-(12*60/24/60)  and inotify = 1 ")
	public int getNumByAidOrIp(@Param("aid") String aid, @Param("ip") String ip);

	@Select("select t.cadddate as adddate,t.ccallback as callback,t.cchannel as channel from tb_adsense t  where t.cidfa = #{aid} and t.isource = #{source} and t.cadddate > sysdate-(12*60/24/60)  and t.inotify = 0  and  rownum <= 1 order by t.cadddate desc ")
	public List<Adsense> getByAidAndSource(@Param("aid")String aid, @Param("source")int source);

	@Update("update tb_adsense set inotify = 1,inotifytype = 1, catime = sysdate,caip = #{ipAddr},cimei = #{imei},cnotifydesc = #{notifydesc} where cidfa = #{aid}  and cadddate = to_date(#{adddate},'yyyy-MM-dd hh24:mi:ss') and inotify = 0")
	public int updateByAid(@Param("ipAddr") String ipAddr,@Param("imei")  String imei,@Param("notifydesc")  String notifydesc,@Param("aid")  String aid,@Param("adddate")  String adddate);

	@Update("update tb_adsense_client_channel set cdownload = cdownload + 1 where iclientsource = #{source} and ichannelsource = #{channel}")
	public int updateAbsenseClientChannelBySourceAndChannel(@Param("source")int source,@Param("channel")int channel);

	@Select("select t.cadddate as adddate ,t.cchannel as channel,t.ccallback as callback from tb_adsense t  where t.ccip = #{ip} and t.isource = #{source} and t.cadddate > sysdate-(12*60/24/60)  and t.inotify = 0  and  rownum <= 1 order by t.cadddate desc ")
	public List<Adsense> getByIpAndSource(@Param("ip") String ip,@Param("source")  int source);

	@Update("update tb_adsense set inotify = 1,inotifytype = 2, catime = sysdate,caip = #{ipAddr},cimei = #{imei},cnotifydesc = #{notifydesc} where ccip = #{ipAddr2}  and cadddate = to_date(#{adddate},'yyyy-MM-dd hh24:mi:ss') and inotify = 0")
	public int updateByIp(@Param("ipAddr") String ipAddr,@Param("imei")  String imei,@Param("notifydesc")  String notifydesc,@Param("ipAddr2")  String ipAddr2,@Param("adddate")  String adddate);

	@Select("select CADDDATE as adddate,ISOURCE source from tb_adsense where (cidfa = #{aid} or ccip = #{ip}) and inotify = 0  and  cnotifydesc is null ")
	public List<Adsense> getByAidAndIp(@Param("aid")String aid,@Param("ip")String ip);

	@Insert("insert into tb_adsense(cidfa,isource,catime,caip,cimei,ccomeflag) values(#{aid},#{source},sysdate,#{ipAddr},#{imei},#{comeflag})")
	public int insert(@Param("aid")String aid,@Param("source") int source,@Param("ipAddr") String ipAddr,@Param("imei") String imei,@Param("comeflag") String comeflag);

	@Update("update tb_adsense set cnotifydesc = #{notifydesc} where (cidfa = #{aid} or ccip = #{ipAddr})  and cadddate = to_date(#{cadddate},'yyyy-MM-dd hh24:mi:ss') and inotify = 0 and  cnotifydesc is null ")
	public int updateNotifyDescByIdOrIp(@Param("notifydesc")String notifydesc,@Param("aid") String aid,@Param("ipAddr") String ipAddr,@Param("cadddate") String cadddate);
	
}

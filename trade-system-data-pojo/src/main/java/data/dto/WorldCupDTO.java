package data.dto;

import lombok.Data;
/**
 * @author LL
 * 世界杯DTO
 *
 */
@Data
public class WorldCupDTO {
	
	private String itemId ="" ; 
	private String matchId ="";
	private String hid ="";
	private String gid ="";
	private String matchStage = "0"; //比赛阶段： 0小组赛，1淘汰赛
	
	private String name="";	//周二015 期次
	private String mname="";	//联赛名
	private String hn="";		//主队名
	private String gn="";		//客队名
	private String et="";		//截止投注时间
	private String hm="";		//主队排名
	private String gm="";		//客队排名
	private String spf="";		//胜平负
	private String spfscale="";//胜平负投注比例
	private String matchNum  = ""; //小组轮次
	private String groupName =""; //分组名
	
	
}

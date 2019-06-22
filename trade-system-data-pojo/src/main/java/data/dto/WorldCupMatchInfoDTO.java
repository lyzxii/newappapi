package data.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
public class WorldCupMatchInfoDTO {
	
	private String matchId ="";
	private String itemId ="";
	private String sort = "";
	private String hid ="";
	private String gid ="";
	private String qc = ""; 
	
	private String historyMatch ="";//历史交锋 spf
	private String hHistory =""; //主队历史战绩
	private String gHistory  =""; 
	
	private String integral1 =""; //小组积分 ：按积分高低排列 ，各项为：排名，teamid，球队名称，比赛场次，spf，进失，积分
	private String integral2 =""; 
	
	private List<MatchInformationDto> intelligences = new ArrayList<>();//比赛情报
	
}

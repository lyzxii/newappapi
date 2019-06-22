package data.dto;

/**
 * 世界杯情报
 * @author ls
 * @2018年4月2日
 */
public class MatchInformationDto {

	private String matchId;
	private String iteamId;
	private String label;
	private String labelType;
	private String title;
	private String content;
	private String createTime;
	private Integer sort ;
		
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getMatchId() {
		return matchId;
	}
	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}
	public String getIteamId() {
		return iteamId;
	}
	public void setIteamId(String iteamId) {
		this.iteamId = iteamId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabelType() {
		return labelType;
	}
	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "MatchInformationDto [matchId=" + matchId + ", iteamId=" + iteamId + ", label=" + label + ", labelType="
				+ labelType + ", title=" + title + ", content=" + content + ", createTime=" + createTime + ", sort="
				+ sort + "]";
	}
	public MatchInformationDto(String matchId, String iteamId, String label, String labelType) {
		super();
		this.matchId = matchId;
		this.iteamId = iteamId;
		this.label = label;
		this.labelType = labelType;
	}
	public MatchInformationDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}

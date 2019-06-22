package pojo;

/**
 * 
 * @author A-0205
 * 对应tb_agent
 */
public class AgentPojo {
	
	private Integer agentLevel;   //代理商等级      ------  根据tb_agent表层级得出的结果查询
	private Integer agentFlag;    //是否是代理商(VIP)标识 ----  根据tb_agent表层级得出的结果查询
	public Integer getAgentLevel() {
		return agentLevel;
	}
	public void setAgentLevel(Integer agentLevel) {
		this.agentLevel = agentLevel;
	}
	public Integer getAgentFlag() {
		return agentFlag;
	}
	public void setAgentFlag(Integer agentFlag) {
		this.agentFlag = agentFlag;
	}
	
	
}

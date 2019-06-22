package pojo;
/**
 * 
 * @author ls
 *
 */
public class Adsense {
	
	private String adddate;//点击时间
	
	private String callback;//回调地址
	
	private Integer channel ; //渠道标识
	
	private String source;//客户端source
	
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public String getAdddate() {
		return adddate;
	}

	public void setAdddate(String adddate) {
		this.adddate = adddate;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}
	
	

}

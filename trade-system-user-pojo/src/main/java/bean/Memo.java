package bean;

public class Memo {
	
	
	
	public Memo(String memo) {
		super();
		this.memo = memo;
	}
	public Memo(String gid, String hid, String memo) {
		super();
		this.gid = gid;
		this.hid = hid;
		this.memo = memo;
	}
	
	private String gid;
	private String hid;
	private String memo;
	
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
	

}

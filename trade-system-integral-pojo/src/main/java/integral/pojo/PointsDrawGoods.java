package integral.pojo;

public class PointsDrawGoods {
    private String id;//抽奖红包id
	private String desc;
	private int totalcnt;//每千次允许发放数
    
	public PointsDrawGoods(String id, String desc, int totalcnt) {
		this.id=id;
		this.desc = desc;
		this.totalcnt = totalcnt;
	}
   
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getTotalcnt() {
		return totalcnt;
	}

	public void setTotalcnt(int totalcnt) {
		this.totalcnt = totalcnt;
	}

}
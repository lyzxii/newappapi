package trade.bean.jczq;

public class JcMatchBean {

	private String expect;
	private int mid;
	private String mname;//联赛名称
	private String hn;//主队
	private String gn;//客队
	private String bt;
	private String et;
	private String fet;
	private String itemid;
    private String lmname ="";//联赛
    private String cl ="";//颜色
    private String isale;
	
	public String getCl() {
		return cl;
	}
	public void setCl(String cl) {
		this.cl = cl;
	}
	public String getLmname() {
		return lmname;
	}
	public void setLmname(String lmname) {
		this.lmname = lmname;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public String getFet() {
		return fet;
	}
	public void setFet(String fet) {
		this.fet = fet;
	}
	private int close;	
	private String b3;
	private String b1;
	private String b0;
	private String spf;
	private String rqspf;
//	private String bqc;
//	private String cbf;
//	private String jqs;
//	private String sxp;
	private String spv;
	
	public String getExpect() {
		return expect;
	}
	public void setExpect(String expect) {
		this.expect = expect;
	}
	public int getMid() {
		return mid;
	}
	public void setMid(int mid) {
		this.mid = mid;
	}
	public String getMname() {
		return mname;
	}
	public void setMname(String mname) {
		this.mname = mname;
	}
	public String getHn() {
		return hn;
	}
	public void setHn(String hn) {
		this.hn = hn;
	}
	public String getGn() {
		return gn;
	}
	public void setGn(String gn) {
		this.gn = gn;
	}
	public String getBt() {
		return bt;
	}
	public void setBt(String bt) {
		this.bt = bt;
	}
	public String getEt() {
		return et;
	}
	public void setEt(String et) {
		this.et = et;
	}
	public int getClose() {
		return close;
	}
	public void setClose(int close) {
		this.close = close;
	}
	public String getB3() {
		return b3;
	}
	public void setB3(String b3) {
		this.b3 = b3;
	}
	public String getB1() {
		return b1;
	}
	public void setB1(String b1) {
		this.b1 = b1;
	}
	public String getB0() {
		return b0;
	}
	public void setB0(String b0) {
		this.b0 = b0;
	}
	public String getSpv() {
		return spv;
	}
	public void setSpv(String spv) {
		this.spv = spv;
	}
	public String getIsale() {
		return isale;
	}
	public void setIsale(String isale) {
		this.isale = isale;
	}
	public String getSpf() {
		return spf;
	}
	public void setSpf(String spf) {
		this.spf = spf;
	}
	public String getRqspf() {
		return rqspf;
	}
	public void setRqspf(String rqspf) {
		this.rqspf = rqspf;
	}
	//	public String getBqc() {
//		return bqc;
//	}
//	public void setBqc(String bqc) {
//		this.bqc = bqc;
//	}
//	public String getCbf() {
//		return cbf;
//	}
//	public void setCbf(String cbf) {
//		this.cbf = cbf;
//	}
//	public String getJqs() {
//		return jqs;
//	}
//	public void setJqs(String jqs) {
//		this.jqs = jqs;
//	}
//	public String getSxp() {
//		return sxp;
//	}
//	public void setSxp(String sxp) {
//		this.sxp = sxp;
//	}	
	/**
	* JAVA判断字符串数组中是否包含某字符串元素
	*
	* @param substring 某字符串
	* @param source 源字符串数组
	* @return 包含则返回true，否则返回false
	*/
	public static boolean isIn(String substring, String[] source) {
		if (source == null || source.length == 0) {
			return false;
		}
		for (int i = 0; i < source.length; i++) {
			String aSource = source[i];
			if (aSource.equals(substring)) {
				return true;
			}
		}
		return false;
	}
}

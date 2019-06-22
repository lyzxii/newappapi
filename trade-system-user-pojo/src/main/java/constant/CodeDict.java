package constant;

/**
 * 判断加密串是什么加密方式
 */
public enum CodeDict {

    /**
     * 没有加密
     */
    NOENCRYPT                                 ("0","NO"),
    /**
     * md5加密
     */
    MD5ENCRYPT                                ("1","MD5"),
    /**
     * aes加密
     */
    AESENCRYPT                                ("2","AES")
	;
    
	private String code;
	
	private String desc;
	
	private CodeDict(String code, String desc){
		this.code = code;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}

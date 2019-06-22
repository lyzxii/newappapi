package trade.bean.jczq;

import com.util.comparable.ComparableBean;

public class XBean extends ComparableBean<XBean> {
    double value;
		int index;
		String codestr;
		String codes;
		public String getCodes() {
			return codes;
		}
		public void setCodes(String codes) {
			this.codes = codes;
		}
		public String getCodestr() {
			return codestr;
		}
		public void setCodestr(String codestr) {
			this.codestr = codestr;
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
	}
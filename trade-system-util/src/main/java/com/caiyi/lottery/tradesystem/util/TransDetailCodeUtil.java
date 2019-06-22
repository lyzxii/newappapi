package com.caiyi.lottery.tradesystem.util;

import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 
 * @author wangtao
 * @date 2017年8月16日
 * @desc 
 */
@Slf4j
public class TransDetailCodeUtil {

	//出票明细获取组合明细
	public static JXmlWrapper getCodeDetail(JXmlWrapper xmlWrapper, String guoguan, String tcode, int i) {
		JXmlWrapper xml=xmlWrapper;
		  try {
			if(StringUtil.isEmpty("guoguan")||StringUtil.isEmpty("tcode")){
				    xmlWrapper.addValue("row["+ i +"].@expand", 0);
			    	return xmlWrapper;
			    }
			    if(IsRequireGetDetail(guoguan,tcode)){
			    	 xml.addValue("row["+ i +"].@expand", 1);
			    	 List<JXmlWrapper> detailNodes=xml.getXmlNodeList("row["+ i +"].detail");
			    	 for(JXmlWrapper detailNode:detailNodes){
			    		 String d_code=detailNode.getStringValue("@d_code");
			    		 if(StringUtil.isEmpty(d_code)){
			    			 d_code=detailNode.getStringValue("@_code");
			    			 if(StringUtil.isEmpty(d_code)){
			    				 xmlWrapper.addValue("row["+ i +"].@expand", 0);
			    				 return xmlWrapper;
			    			 }
			    		 }
			    		 String transCode = TransCodeUtil.transCode(d_code);
			    		 if(detailNode.getStringValue("@d_code")!=null){
			    			 detailNode.setValue("@d_code", transCode);
			    		 }else{
			    			 detailNode.addValue("@d_code", transCode);
			    		 }
			             String d_guoguan = TransCodeUtil.getGuoGuan(d_code);
			             detailNode.addValue("@d_gg", d_guoguan);
			    	 }
			    }else{
			    	 xml.addValue("row["+ i +"].@expand", 0);
			    }
			    return xml;
		} catch (Exception e) {
			xmlWrapper.addValue("row["+ i +"].@expand", 0);
			log.error("getCodeDetail guoguan:"+guoguan+" tcode:"+tcode+" i:"+i,e);
		}
	   return xmlWrapper;
	}
   
	//检查是否需要获取组合明细
	private static boolean IsRequireGetDetail(String guoguan, String tcode){
		int size=tcode.split(",").length;
		String cc=size+"串1";
		int gsize=guoguan.split(",").length;
		//该张票场次数为X，X>=3且过关方式不单独为X串1时
		//该张票过关方式为两种或以上（不包括单关）
		if((size>=3&&!guoguan.equals(cc))||(gsize>=2&&!guoguan.equals("单关")&&!guoguan.equals("1*1"))){
			return true;
		}
		return false;
	}
	
}

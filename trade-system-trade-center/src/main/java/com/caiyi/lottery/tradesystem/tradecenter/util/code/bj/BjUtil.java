package com.caiyi.lottery.tradesystem.tradecenter.util.code.bj;

public class BjUtil {
	
	public static boolean check(String playtype,String guoguan){
		int gg = getType(guoguan);
		if(gg == -1){
			return false;
		}
		if(playtype.equalsIgnoreCase("SPF")){
			if(gg > 15){
				return false;
			}
		}else if(playtype.equalsIgnoreCase("CBF")){
			if(gg > 3){
				return false;
			}
		}else if(playtype.equalsIgnoreCase("BQC") || playtype.equalsIgnoreCase("SXP") || playtype.equalsIgnoreCase("JQS")){
			if(gg > 6){
				return false;
			}
		}else{
			return false;
		}
		return true;
	}
	
	public static int getType(String fc){
		if(fc.startsWith("单场")||fc.startsWith("单关")||fc.startsWith("1*1")){
			return 1;
		}else if(fc.startsWith("2*1")){
			return 2;
		}else if(fc.startsWith("2*3")){
			return 2;
		}else if(fc.startsWith("3*1")||fc.startsWith("3*4")||fc.startsWith("3*7")){
			return 3;
		}else if(fc.startsWith("4*1")||fc.startsWith("4*5")||fc.startsWith("4*11")||fc.startsWith("4*15")){
			return 4;
		}else if(fc.startsWith("5*1")||fc.startsWith("5*6")||fc.startsWith("5*16")||fc.startsWith("5*26")||fc.startsWith("5*31")){
			return 5;                        
		}else if(fc.startsWith("6*1")||fc.startsWith("6*7")||fc.startsWith("6*22")||fc.startsWith("6*42")||fc.startsWith("6*57")||fc.startsWith("6*63")){
			return 6;                         
		}else if(fc.startsWith("7*1")){
			return 7;                       
		}else if(fc.startsWith("8*1")){
			return 8;                       
		}else if(fc.startsWith("9*1")){
			return 9;                       
		}else if(fc.startsWith("10*1")){
			return 10;                       
		}else if(fc.startsWith("11*1")){
			return 11;                       
		}else if(fc.startsWith("12*1")){
			return 12;                       
		}else if(fc.startsWith("13*1")){
			return 13;                       
		}else if(fc.startsWith("14*1")){
			return 14;                       
		}else if(fc.startsWith("15*1")){
			return 15;                       
		}else{
			return -1;
		}
	}
}

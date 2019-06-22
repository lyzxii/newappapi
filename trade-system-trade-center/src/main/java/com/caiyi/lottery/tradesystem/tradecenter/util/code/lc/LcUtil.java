package com.caiyi.lottery.tradesystem.tradecenter.util.code.lc;

public class LcUtil {
    
    public static boolean check(String playtype,String guoguan){
        int gg = getType(guoguan);
        if(gg == -1){
            return false;
        }
        if(playtype.equalsIgnoreCase("SF") || playtype.equalsIgnoreCase("RFSF") || playtype.equalsIgnoreCase("DXF")){
            if(gg > 8){
                return false;
            }
        }else if(playtype.equalsIgnoreCase("SFC")){
            if(gg > 4){
                return false;
            }
        }else if(playtype.equalsIgnoreCase("HH")){
            if(gg > 8){
                return false;
            }
        }else{
            return false;
        }
        return true;
    }
    
    public static int getType(String fc){
        if(fc.startsWith("1*1")){
            return 1;
        }else if(fc.startsWith("2*1")){
            return 2;
        }else if(fc.startsWith("3*1")||fc.startsWith("3*3")||fc.startsWith("3*4")){
            return 3;
        }else if(fc.startsWith("4*1")||fc.startsWith("4*4")||fc.startsWith("4*5")||fc.startsWith("4*6")||fc.startsWith("4*11")){
            return 4;
        }else if(fc.startsWith("5*1")||fc.startsWith("5*5")||fc.startsWith("5*6")||fc.startsWith("5*10")||fc.startsWith("5*16")||fc.startsWith("5*20")||fc.startsWith("5*26")){
            return 5;                        
        }else if(fc.startsWith("6*1")||fc.startsWith("6*6")||fc.startsWith("6*7")||fc.startsWith("6*15")||fc.startsWith("6*20")||fc.startsWith("6*22")||fc.startsWith("6*35")||fc.startsWith("6*42")||fc.startsWith("6*50")||fc.startsWith("6*57")){
            return 6;                         
        }else if(fc.startsWith("7*1")||fc.startsWith("7*7")||fc.startsWith("7*8")||fc.startsWith("7*21")||fc.startsWith("7*120")||fc.startsWith("7*35")){
            return 7;                         
        }else if(fc.startsWith("8*1")||fc.startsWith("8*8")||fc.startsWith("8*9")||fc.startsWith("8*28")||fc.startsWith("8*56")||fc.startsWith("8*70")||fc.startsWith("8*247")){
            return 8;                         
        }else{
            return -1;
        }

    }
}

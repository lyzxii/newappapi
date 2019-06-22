package com.caiyi.lottery.tradesystem.tradecenter.util.code.lc;

import com.caipiao.plugin.helper.CodeFormatException;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.FilterBase;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;
import com.mina.rbc.util.StringUtil;
import trade.bean.CodeBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterLcHH extends FilterBase{

    public static final HashMap<String, String> sfMaps = new HashMap<String, String>();
    public static final HashMap<String, String> dxfMaps = new HashMap<String, String>();
    public static final HashMap<String, String> sfcMaps = new HashMap<String, String>();
    static
    {
        sfMaps.put("3", "3");
        sfMaps.put("0", "0");
        dxfMaps.put("3", "3");
        dxfMaps.put("0", "0");
        sfcMaps.put("01", "01");
        sfcMaps.put("02", "02");
        sfcMaps.put("03", "03");
        sfcMaps.put("04", "04");
        sfcMaps.put("05", "05");
        sfcMaps.put("06", "06");
        sfcMaps.put("11", "11");
        sfcMaps.put("12", "12");
        sfcMaps.put("13", "13");
        sfcMaps.put("14", "14");
        sfcMaps.put("15", "15");
        sfcMaps.put("16", "16");
    }
    
    @Override
    public void filter(CodeBean bean, FilterResult result) throws CodeFormatException 
    {
        if (bean.getItemType() == CodeBean.NOITEM)   //文件不含场次(需指定场次)
        {
            doSimple(bean,result);
        }
        else if(bean.getItemType() == CodeBean.HAVEITEM)    //文件含场次
        {
            //检测投注选项
            String codeString = bean.getCodeitems();
            HashMap<String, String> codeMaps = new HashMap<String, String>();
            if(codeString != null){
                codeString = codeString.replaceAll("\\s+", "");
                String [] codeitems = codeString.split(",");
                for(int i = 0; i < codeitems.length; i++){
                    String [] ccs = codeitems[i].split("=");
                    if(ccs.length != 2){
                        throw new CodeFormatException(-1, "投注选项替换格式不符合要求", bean.getCode());
                    }
                    codeMaps.put(ccs[1].trim(), ccs[0].trim());
                }
            }
            
            Pattern pattern = Pattern.compile("\\s*^(HH|hh)\\s*\\|(.+)\\|(\\s*\\d+\\s*\\*\\s*\\d+\\s*)([\\w\\W]*)");
            Matcher matcher = pattern.matcher(bean.getCode());
            if(matcher.find())
            {
                try
                {
                    doDyjWeb(bean, result, matcher.group(2),matcher.group(3), codeMaps);
                }
                catch(Exception e)
                {
                    throw new CodeFormatException(-1, "投注选项替换格式不符合要求", bean.getCode());
                }
            }
        }
    }
    
    /**
     * 篮彩混投-单式上传-单式解析(包含场次)
     * @param   bean
     * @param   result
     * @param   code
     * @param   guoguan
     * @param   codeMaps
     * @throws  CodeFormatException
     */
    private void doDyjWeb(CodeBean bean, FilterResult result, String code,String guoguan, HashMap<String, String> codeMaps) throws CodeFormatException
    {
        HashMap<String, String> teamsMaps = new HashMap<String, String>();
        String [] cs = code.replaceAll("\\s*", "").split(",");
        StringBuffer sb = new StringBuffer();
        sb.append(bean.getPlaytype());
        sb.append("|");
        int len = cs.length;
        String gg = guoguan.replaceAll("\\s*", "");

//        //验证过关方式是否合法
//        if(bean.getGuoguan().equals("1*1") && !(bean.getGuoguan().equals(gg)))
//        {
//            throw new CodeFormatException(-1, "浮动奖金玩法仅支持单关投注", bean.getCode());
//        }
//        if(gg.equals("1*1") && !(bean.getGuoguan().equals("1*1")))
//        {
//            throw new CodeFormatException(-1, "固定奖金玩法不支持单关投注", bean.getCode());
//        }
        
        //检查玩法和过关方式是否匹配
        if(!LcUtil.check(bean.getPlaytype(), gg))
        {
            throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
        }
        
        bean.setGuoguan(gg);                //设置过关方式
        for(int i = 0; i < len; i++)        //循环投注列表
        {
            String [] ccs = cs[i].split(">");
            if(ccs.length != 2)
            {
                throw new CodeFormatException(-1, "投注格式不符合要求", bean.getCode());
            }
            teamsMaps.put(ccs[0], ccs[0]);
            sb.append(ccs[0]);
            sb.append(">");
            String [] csc = StringUtil.splitter(ccs[1], "+");
            
            //循环单个投注选项的玩法(比如：SF=3+RFSF=0+SFC=03)
            for(int j = 0; j < csc.length; j++)
            {
                String[] codes = csc[j].split("=");
                if((codes[0].indexOf("[") != -1 && codes[0].indexOf("[") != -1))    //让分胜负玩法([-6.5]让分格式)
                {
                    codes[1] = codes[0].substring(codes[0].indexOf("]") + 1);
                    codes[0] = "rfsf";
                }
                if(codes == null || codes.length != 2 || codes[0] == null || codes[1] == null)      //对投注号码格式进行验证
                {
                    throw new CodeFormatException(-1, "投注格式不符合要求", bean.getCode());
                }
                sb.append(codes[0] + "=");
                
                //拆分单个玩法的投注内容
                int length = codes[1].length();
                if(codes[0].equalsIgnoreCase("sfc"))    //如果是胜分差
                {
                    if(codes[1].indexOf("/") != -1)
                    {
                        String[] tzxx = codes[1].split("/");
                        for(int k = 0; k < tzxx.length; k ++)
                        {
                            sb.append(tzxx[k]);
                            if(k != tzxx.length - 1)
                            {
                                sb.append("/");
                            }
                        }
                    }
                    else
                    {
                        for(int k = 0; k < length; k = k + 2)
                        {
                            sb.append(codes[1].substring(k,k + 2));
                            if(k != length - 2)
                            {
                                sb.append("/");
                            }
                        }
                    }
                }
                else    //其他玩法
                {
                    if(codes[1].indexOf("/") != -1)
                    {
                        String[] tzxx = codes[1].split("/");
                        for(int k = 0; k < tzxx.length; k ++)
                        {
                            sb.append(tzxx[k]);
                            if(k != tzxx.length - 1)
                            {
                                sb.append("/");
                            }
                        }
                    }
                    else
                    {
                        for(int k = 0; k < length; k ++)
                        {
                            sb.append(codes[1].substring(k,k + 1));
                            if(k != length - 1)
                            {
                                sb.append("/");
                            }
                        }
                    }
                }
                if(j != csc.length - 1)
                {
                    sb.append("+");
                }
            }
            sb.append(",");
        }
        if(teamsMaps.size() != len)
        {
            throw new CodeFormatException(-1, "投注场次存在重复", bean.getCode());
        }
        code = sb.toString();
        if(code.endsWith(","))
        {
            code = code.substring(0, code.lastIndexOf(","));
        }
        code += "|" + bean.getGuoguan();
        result.putGglist(gg);
        result.putItems(teamsMaps);
        result.addCode(code);
    }
    
    /**
     * 竞彩篮球-混投-单式上传-单式解析(不带场次)
     * @param   bean
     * @param   result
     * @throws  CodeFormatException
     */
    private void doSimple(CodeBean bean, FilterResult result) throws CodeFormatException
    {
        //检查玩法和过关方式是否匹配
        if(!LcUtil.check(bean.getPlaytype(), bean.getGuoguan()))
        {
            throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
        }

        //兼容各种投注分割符号
        String code = bean.getCode();
        code = code.replaceAll("\\||,|-", ",").replaceAll("\\*", "#");
        String[] codes = code.split(",");   //实际投注号码数组
        
        //检测投注场次
        String itemString = bean.getTeamitems(); 
        String [] teamitems = itemString.split(",");
        int teamlen = teamitems.length;
        if(teamlen < codes.length)
        {
            throw new CodeFormatException(-1, "所选场次数量不能少于实际投注场次数量", bean.getCode());
        }
        HashMap<String, String> teamsMaps = new HashMap<String, String>();
        for(String s: teamitems)
        {
            try 
            {
                Integer.parseInt(s);
            }
            catch (Exception e) 
            {
                throw new CodeFormatException(-1, "所选场次不符合要求", bean.getCode());
            }
            teamsMaps.put(s, s);
        }
        if(teamsMaps.size() != teamlen)
        {
            throw new CodeFormatException(-1, "所选场次存在重复场次", bean.getCode());
        }
        
        //生成标准格式
        StringBuffer sb = new StringBuffer();
        sb.append(bean.getPlaytype());
        sb.append("|");
        int count = 0;
        for(int i = 0; i < codes.length; i++)
        {
            String temp = String.valueOf(codes[i]);
            if("#".equals(temp))
            {
                continue;
            }
            sb.append(teamitems[i]).append(">").append(temp).append(",");
            count++;
        }
        if(count < LcUtil.getType(bean.getGuoguan()))
        {
            throw new CodeFormatException(-1, "场次不足以支持过关方式", bean.getCode());
        }
        code = sb.toString();
        if(code.endsWith(","))
        {
            code = code.substring(0, code.lastIndexOf(","));
        }
        code += "|" + bean.getGuoguan();
        result.putItems(teamsMaps);
        result.addCode(code);
    }
    
    /**
     * 投注内容验证
     * @param   value
     * @param   bean
     * @return  符合条件的标准值
     * @throws CodeFormatException
     */
    private String getCodeItem(String type,String value,CodeBean bean) throws CodeFormatException
    {
        if(type.equalsIgnoreCase("sf") || type.equalsIgnoreCase("rfsf"))
        {
            if(!sfMaps.containsKey(value))
            {
                throw new CodeFormatException(-1, "投注号码号码不合法,", bean.getCode());
            }
            else
            {
                return sfMaps.get(value);
            }
        }
        else if(type.equalsIgnoreCase("sfc"))
        {
            if(!sfcMaps.containsKey(value))
            {
                throw new CodeFormatException(-1, "投注号码号码不合法,", bean.getCode());
            }
            else
            {
                return sfcMaps.get(value);
            }
        }
        else if(type.equalsIgnoreCase("dxf"))
        {
            if(!dxfMaps.containsKey(value))
            {
                throw new CodeFormatException(-1, "投注号码号码不合法,", bean.getCode());
            }
            else
            {
                return dxfMaps.get(value);
            }
        }
        else
        {
            throw new CodeFormatException(-1, "投注号码号码不合法,", bean.getCode());
        }
    }
    
    public static void main(String[] args) 
    {
        FilterResult result = new FilterResult();
        try 
        {
            List<String> nlist = new ArrayList<String>();
            //nlist.add("SF=3+RFSF=3+SFC=06+DXF=3,RFSF=3+DXF=0+SFC=13|2*1");
            //nlist.add("SF=0+RFSF=3+SFC=02+DXF=0,RFSF=3+DXF=0+SFC=13|2*1");
            nlist.add("HH|160328301>DXF=3,160328302>RFSF=0,160328303>RFSF=0,160328304>RFSF=0,160328305>RFSF=0|5*1");
//            nlist.add("HH|140821301>SF=0+RFSF=3+SFC=02+DXF=0,140821302>RFSF=3+DXF=0+SFC=13|2*1");
            CodeBean bean = new CodeBean();
            bean.setLottype(71);
            bean.setPlaytype("HH");
            bean.setCodeitems("3=3,0=0");
            bean.setTeamitems("160328301,160328302,160328303,160328304,160328305");
            bean.setGuoguan("5*1");
            bean.setItemType(CodeBean.HAVEITEM);
            for (String c : nlist)
            {
                bean.setCode(c);
                FilterBase.doFilterLc(bean, result);
                System.out.println(result.getCurrentCode());
            }
//          
//          for(String c : ylist){
//              bean.setItemType(CodeBean.HAVEITEM);
//              bean.setCode(c);
//              spf.doFilterJc(bean, fr);
//          }
            
        } catch (CodeFormatException e) {

        }
    }
}
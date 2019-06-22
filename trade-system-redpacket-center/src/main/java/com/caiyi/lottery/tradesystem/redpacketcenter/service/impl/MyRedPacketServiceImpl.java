package com.caiyi.lottery.tradesystem.redpacketcenter.service.impl;

import com.caiyi.lottery.tradesystem.redpacketcenter.dao.UserRedpacket_RedpacketMapper;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.MyRedPacketService;
import com.caiyi.lottery.tradesystem.redpacketcenter.util.RedPacketCenterUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import redpacket.bean.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 查询我的红包
 */
@Service
@Slf4j
public class MyRedPacketServiceImpl  implements MyRedPacketService {

    @Autowired
    private UserRedpacket_RedpacketMapper userRp_rpMapper;

    @Override
    public MyRedPacketPage queryMyRedPacket(RedPacketBean bean) throws Exception {
        String nickid = bean.getUid();
        String state = bean.getState();
        int flag = 1;
        log.info("查询我的红包详情,nickid = " + nickid +",state = " + state);
        if(StringUtil.isEmpty(nickid) && StringUtil.isEmpty(state)){
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("参数为空");
            return null;
        }
        Map<String,Integer> map=titleNum(nickid);
        if("1".equals(state)){//可用
            return queryRedpacket(bean,flag,map);
        }else if ("2".equals(state) ){//过期 已用完
            return queryRedpacket(bean,flag,map);
        }else if("3".equals(state)){//待派发
            flag = 0;
            return queryRedpacket(bean,flag,map);
        }
        return null;
    }

    private MyRedPacketPage queryRedpacket(RedPacketBean bean, int flag, Map<String,Integer> map) throws ParseException {
        PageHelper.startPage(bean.getPn(),bean.getPs());
        List<RedPacketBean> redpackets=null;
        if("1".equals(bean.getState())){
            redpackets= userRp_rpMapper.queryredpacketUseabel(bean.getUid());
        }else if("2".equals(bean.getState())){
            redpackets=userRp_rpMapper.queryredpacketUseless(bean.getUid());
        }else if("3".equals(bean.getState())){
            redpackets=userRp_rpMapper.queryredpacketWait(bean.getUid());
        }
        if(redpackets==null){
            return  null;
        }
        PageInfo<RedPacketBean> pageInfo=new PageInfo<>(redpackets);
        return AssemblyRedpacketInfo(redpackets,bean,map,pageInfo,flag);

    }
    /**
     * 根据查询组装红包信息
     */
    private MyRedPacketPage AssemblyRedpacketInfo(List<RedPacketBean> redpackets, RedPacketBean bean,Map<String,Integer> map, PageInfo<RedPacketBean> pageInfo,int flag) throws ParseException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowdate = sdf.format(date);
        List<Map<String,Object>> dataslist=new ArrayList<>();
        for(RedPacketBean redpacket:redpackets){
            Map<String,Object> rpMap=new HashMap<>();
            rpMap.put("rpid",redpacket.getCupacketid());
            rpMap.put("nickid",redpacket.getCnickid());
            if(StringUtil.isEmpty(redpacket.getImoney())){
                rpMap.put("money",0);
            }else{
                rpMap.put("money",redpacket.getImoney());
            }
            rpMap.put("state",redpacket.getIstate());
            rpMap.put("rpname",StringUtil.isEmpty(redpacket.getCrpname())?"":redpacket.getCrpname());
            rpMap.put("scale",StringUtil.isEmpty(redpacket.getScale())?"":redpacket.getScale());
            if(StringUtil.isEmpty(redpacket.getIrmoney())){
                rpMap.put("rmoney",0);
            }else{
                rpMap.put("rmoney",redpacket.getIrmoney());
            }
            if(flag==1){
               rpMap.put("deaddate",StringUtil.isEmpty(redpacket.getCdeaddate())?"":redpacket.getCdeaddate());
               if(StringUtil.isEmpty(redpacket.getCdeaddate())){
                   rpMap.put("residuetime","");
               }else{
                   long[] datas = RedPacketCenterUtil.getDistanceTimes(redpacket.getCdeaddate(),nowdate);
                   if(datas[0] <= 2){
                       rpMap.put("residuetime",datas[0]+1+"");//过期时间
                   }else{
                       rpMap.put("residuetime","");//过期时间
                   }
               }
            }else if(flag==0){
                if(StringUtil.isEmpty(redpacket.getCdispatchtime())){
                    rpMap.put("dispatch","");
                }else{
                    rpMap.put("dispatch",redpacket.getCdispatchtime());//红包派发时间
                }
            }
            dataslist.add(rpMap);
        }
        return new MyRedPacketPage(bean.getPs(),bean.getPn(),pageInfo.getPages(),
                pageInfo.getTotal(),dataslist,map.get("unum"),map.get("wnum"));
    }

    /**
     * 查询标题红包数目
     */
    private Map<String,Integer> titleNum(String cnickid){
        Map<String,Integer> map = new HashMap<>();
        int count= userRp_rpMapper.querytitleNumU(cnickid);
        if(count>=1){
            map.put("unum", count);
        }else{
            map.put("unum", 0);
        }
        count= userRp_rpMapper.querytitleNumW(cnickid);
        if(count>=1){
            map.put("wnum", count);
        }else{
            map.put("wnum", 0);
        }
        return map;
    }

     //发送红包
    @Override
    public void sendRedpacket(RedPacketBean bean){
        try {
            userRp_rpMapper.sendRedPacket(bean);
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("发送红包成功");
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("发送红包失败");
            log.error("发送红包失败失败,uid:{}",bean.getUid(),e);

        }
    }

}

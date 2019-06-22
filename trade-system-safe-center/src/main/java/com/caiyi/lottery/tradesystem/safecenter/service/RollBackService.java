package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.RollbackDTO;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.safecenter.mq.Producers;
import com.caiyi.lottery.tradesystem.util.AESUtils;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 回滚处理
 *
 * @author GJ
 * @create 2017-12-29 18:08
 **/
@Slf4j
@Service
public class RollBackService {

    private static final String INSERT = "insert";
    private static final String DELETE = "delete";
    private static final String UPDATE = "update";
    private static final String TB_USER_VICE = "tb_user_vice";

    private static final String path = "/opt/export/data/rollbackfail";

    @Autowired
    private TbUserViceService tbUserViceService;
    @Autowired
    private Producers producers;
    @Autowired
    private RedisClient redisClient;
    private String key = "l8i9KqIw4AN0gj3ihny7OVnG";

    public void transactionalCompensateSafeCenter(RollbackDTO rollbackDTO) {
        if (UPDATE.equals(rollbackDTO.getRollbackOperation())) {
            if (TB_USER_VICE.equals(rollbackDTO.getTarget())) {
                Map<String, Object> map = rollbackDTO.getDataMap();
                if (!isNull(map)) {
                    if (map.get("object") instanceof SafeBean) {
                        SafeBean safeBean = (SafeBean) map.get("object");
                        log.info("安全中心进行事务补偿-uid:{}", safeBean.getNickid());
                        if (!StringUtil.isEmpty(safeBean.getUsersource()) && !StringUtil.isEmpty(safeBean.getNickid())) {
                            String crealname = !StringUtil.isEmpty(safeBean.getRealname()) ? AESUtils.aesEncode(key, safeBean.getRealname()) : null;
                            String cidcard = !StringUtil.isEmpty(safeBean.getIdcard()) ? AESUtils.aesEncode(key, safeBean.getIdcard()) : null;
                            String cmobileno = !StringUtil.isEmpty(safeBean.getMobileno()) ? AESUtils.aesEncode(key, safeBean.getMobileno()) : null;
                            String cbankcard = !StringUtil.isEmpty(safeBean.getBankcard()) ? AESUtils.aesEncode(key, safeBean.getBankcard()) : null;
                            String ccardmobile = !StringUtil.isEmpty(safeBean.getCardmobile()) ? AESUtils.aesEncode(key, safeBean.getCardmobile()) : null;
                            CacheBean cacheBean = new CacheBean();
                            cacheBean.setKey("transactionalCompensate_safe_"+safeBean.getNickid());
                            cacheBean.setTime(60*60*1000);
                            Integer integer = StringUtil.isEmpty(redisClient.getString(cacheBean, log, SysCodeConstant.SAFECENTER))?0:Integer.valueOf(redisClient.getString(cacheBean, log, SysCodeConstant.SAFECENTER));
                            boolean iscontiue = true;
                            if (integer < 20) {
                                int count = tbUserViceService.addUserVice(crealname, cidcard, cmobileno, cbankcard, ccardmobile, safeBean.getUsersource(), safeBean.getNickid());
                                if (count < 1) {
                                    List<RollbackDTO> rollbackDTOList = new ArrayList<>();
                                    rollbackDTOList.add(rollbackDTO);
                                    producers.sendLocalList(rollbackDTOList);
                                    integer=integer++;
                                    cacheBean.setValue(String.valueOf(integer));
                                    redisClient.setString(cacheBean, log, SysCodeConstant.SAFECENTER);
                                }else {
                                    iscontiue=false;
                                }
                                log.info("安全中心本地重试补偿事务{}次,uid:{}", integer, safeBean.getNickid());

                            }
                            //重试超过次数，存储文件
                            if (integer >= 20&&iscontiue){
                                String date = DateTimeUtil.formatDate(new Date(), DateTimeUtil.DATE_FORMAT);
                                String res = JSONObject.toJSONString(safeBean);
                                createFileNew(path+File.separator+date+File.separator, safeBean.getNickid(), res);
                            }

                        }
                    }
                }
            }
        }
    }

    public static void createFileNew(String path, String filename, String content) {
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            bw = new BufferedWriter(new FileWriter(new File(path + filename)));
            bw.write(content);
        } catch (IOException e) {
            log.error("createFileNew异常,path:"+path+" filename:"+filename+" content:"+content,e);
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            } catch (IOException ex) {
                log.error("error", ex.getMessage(), ex);
            }
        }
    }

    private Boolean isNull(Map<String, Object> map) {
        if (map == null) {
            return true;
        } else if (map.get("object") == null) {
            return true;
        }
        return false;
    }
}

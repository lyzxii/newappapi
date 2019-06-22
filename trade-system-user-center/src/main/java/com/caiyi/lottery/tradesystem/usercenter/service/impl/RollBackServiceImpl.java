package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import bean.*;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.RollbackDTO;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.safecenter.clientwrapper.SafeCenterWrapper;
import com.caiyi.lottery.tradesystem.usercenter.dao.RollBackMapper;
import com.caiyi.lottery.tradesystem.usercenter.mq.Producers;
import com.caiyi.lottery.tradesystem.usercenter.service.RollBackService;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class RollBackServiceImpl implements RollBackService {

    private static final String  INSERT="insert";
    private static final String  DELETE="delete";
    private static final String  UPDATE="update";
    private static final String TB_USER = "tb_user";
    private static final String TB_ALLY = "tb_ally";
    private static final String TB_USER_ACCT = "tb_user_acct";
    private static final String TB_ALLY_LOG = "tb_ally_log";
    private static final String TB_CELLPHONE_IMEI = "tb_cellphone_imei";
    private static final String TB_USER_VICE = "tb_user_vice";
    private static final String TB_MOBILE = "tb_mobile";
    @Autowired
    private RollBackMapper rollBackMapper;
    @Autowired
    private SafeCenterWrapper safeCenterWrapper;
    @Autowired
    private SafeCenterInterface safeCenterInterface;
    @Autowired
    private Producers producers;
    @Autowired
    private RedisClient redisClient;

    private Integer maxAttempts=2;

    @Override
    public synchronized void transactionalCompensateSafeCenter(RollbackDTO rollbackDTO) {
        if (UPDATE.equals(rollbackDTO.getRollbackOperation())) {
            if (TB_USER_VICE.equals(rollbackDTO.getTarget())) {
                Map<String, Object> map = rollbackDTO.getDataMap();
                if (!isNull(map)) {
                    if (map.get("object") instanceof SafeBean) {
                        SafeBean safeBean = (SafeBean) map.get("object");
                        log.info("SafeBean,业务功能-{},安全中心{}表,事务补偿，uid:{}",rollbackDTO.getSource(),TB_USER_VICE,safeBean.getNickid());
                        int attempt=0;
                        Boolean isFail=false;
                        //本次尝试2次连接安全中心操作
                        while(attempt<maxAttempts){
                            try {
                                boolean flag = safeCenterWrapper.addUserTable(safeBean, log, SysCodeConstant.USERCENTER);
                                if (!flag) {
                                    log.info("SafeBean,业务功能-{},安全中心-{}表,补偿事务失败,[uid:{}]",rollbackDTO.getSource(),TB_USER_VICE,safeBean.getNickid());
                                    attempt++;
                                    isFail=true;
                                }else {
                                    attempt=2;
                                    isFail=false;
                                }
                            } catch (Exception e) {
                                log.error("SafeBean,业务功能-{},安全中心-{}表,补偿事务失败,发生异常",e);
                                attempt++;
                                isFail=true;
                            }
                        }
                        //重试失败，再放入队列
                        if (isFail) {
                            CacheBean cacheBean = new CacheBean();
                            cacheBean.setKey("transactionalCompensate_user_"+safeBean.getNickid());
                            cacheBean.setTime(60*60*1000);
                            Integer integer = StringUtil.isEmpty(redisClient.getString(cacheBean, log, SysCodeConstant.USERCENTER))?0:Integer.valueOf(redisClient.getString(cacheBean, log, SysCodeConstant.USERCENTER));
                            List<RollbackDTO> rollbackDTOList = new ArrayList<>();
                            rollbackDTOList.add(rollbackDTO);
                            if (integer < maxAttempts) {
                                producers.sendSafeCenterList(rollbackDTOList);
                                integer=integer++;
                                cacheBean.setValue(String.valueOf(integer));
                                redisClient.setString(cacheBean, log, SysCodeConstant.USERCENTER);
                            }else {
                                //2次放入队列没消费之后，放入另一个队列，安全中心本地循环消费，直到成功。
                                if (integer < 3){
                                    producers.sendLocalList(rollbackDTOList);
                                }
                                integer=integer++;
                                cacheBean.setValue(String.valueOf(integer));
                                redisClient.setString(cacheBean, log, SysCodeConstant.USERCENTER);
                            }
                        }

                     }else if (map.get("object") instanceof AlipayLoginBean) {
                        AlipayLoginBean alipayLoginBean = (AlipayLoginBean) map.get("object");
                        SafeBean safeBean2 = new SafeBean();
                        safeBean2.setNickid(alipayLoginBean.getUid());
                        safeBean2.setRealname(alipayLoginBean.getRealName());
                        safeBean2.setIdcard(alipayLoginBean.getCertNo());
                        safeBean2.setMobileno(alipayLoginBean.getMobileNo());
                        safeBean2.setUsersource(SourceConstant.CAIPIAO);
                        log.info("AlipayLoginBean,业务功能-{},安全中心{}表,事务补偿，uid:{}",rollbackDTO.getSource(),TB_USER_VICE,alipayLoginBean.getUid());
                        boolean flag = safeCenterWrapper.addUserTable(safeBean2, log, SysCodeConstant.USERCENTER);
                        if (!flag) {
                            log.error("AlipayLoginBean,业务功能-{},安全中心-{}表,补偿事务失败,[uid:{}]",rollbackDTO.getSource(),TB_USER_VICE,alipayLoginBean.getUid());
                        }
                    }else if (map.get("object") instanceof UserBean) {
                        UserBean userBean = (UserBean) map.get("object");
                        SafeBean safeBean2 = new SafeBean();
                        safeBean2.setNickid(userBean.getUid());
                        safeBean2.setRealname(userBean.getRealName());
                        safeBean2.setIdcard(userBean.getIdCardNo());
                        safeBean2.setMobileno(userBean.getMobileNo());
                        safeBean2.setBankcard(userBean.getBankCard());
                        safeBean2.setUsersource(SourceConstant.CAIPIAO);
                        log.info("UserBean,业务功能-{},安全中心{}表,事务补偿，uid:{}",rollbackDTO.getSource(),TB_USER_VICE,userBean.getUid());
                        boolean flag = safeCenterWrapper.addUserTable(safeBean2, log, SysCodeConstant.USERCENTER);
                        if (!flag) {
                            log.error("UserBean,业务功能-{},安全中心-{}表,补偿事务失败,[uid:{}]",rollbackDTO.getSource(),TB_USER_VICE,userBean.getUid());
                        }
                    }else if (map.get("object") instanceof WeChatBean) {
                        WeChatBean wechatBean = (WeChatBean) map.get("object");
                        SafeBean safeBean2 = new SafeBean();
                        safeBean2.setNickid(wechatBean.getUid());
                        safeBean2.setMobileno(wechatBean.getMphone());
                        safeBean2.setUsersource(SourceConstant.CAIPIAO);
                        log.info("WeChatBean,业务功能-{},安全中心{}表,事务补偿，uid:{}",rollbackDTO.getSource(),TB_USER_VICE,wechatBean.getUid());
                        boolean flag = safeCenterWrapper.addUserTable(safeBean2, log, SysCodeConstant.USERCENTER);
                        if (!flag) {
                            log.error("WeChatBean,业务功能-{},安全中心-{}表,补偿事务失败,[uid:{}]",rollbackDTO.getSource(),TB_USER_VICE,wechatBean.getUid());
                        }
                    }

                }
            }else  if (TB_MOBILE.equals(rollbackDTO.getTarget())) {
                Map<String, Object> map = rollbackDTO.getDataMap();
                if (!isNull(map)) {
                    if (map.get("object") instanceof WeChatBean) {
                        WeChatBean wechatBean = (WeChatBean) map.get("object");
                        SafeBean safeBean = new SafeBean();
                        safeBean.setMobileno(wechatBean.getMphone());
                        BaseReq<SafeBean> safeReq = new BaseReq<SafeBean>(safeBean, SysCodeConstant.USERCENTER);
                        BaseResp<SafeBean> safeResp = safeCenterInterface.mobileNo(safeReq);
                        log.info("WeChatBean,业务功能-{},安全中心{}表,事务补偿，uid:{}",rollbackDTO.getSource(),TB_MOBILE,wechatBean.getUid());
                        if (!BusiCode.SUCCESS.equals(safeResp.getCode())) {
                            log.error("WeChatBean,业务功能-{},安全中心-{}表,补偿事务失败,[uid:{}]",rollbackDTO.getSource(),TB_MOBILE,wechatBean.getUid());
                        }
                    }

                }
            }
        }
    }

    private Boolean isNull(Map<String, Object> map){
        if (map == null) {
            return true;
        } else if (map.get("object") == null) {
            return true;
        }
        return false;
    }

    @Override
    public void transactionalCompensateDelete(RollbackDTO rollbackDTO) {
        if (DELETE.equals(rollbackDTO.getRollbackOperation())) {
            if (TB_USER.equals(rollbackDTO.getTarget())) {
                transactionalCompensateDeleteUser(rollbackDTO);
            } else if (TB_USER_ACCT.equals(rollbackDTO.getTarget())) {
                transactionalCompensateDeleteUserAcct(rollbackDTO);
            } else if (TB_ALLY.equals(rollbackDTO.getTarget())) {
                transactionalCompensateDeleteAlly(rollbackDTO);
            } else if (TB_ALLY_LOG.equals(rollbackDTO.getTarget())) {
                transactionalCompensateDeleteAllyLog(rollbackDTO);
            }else  if (TB_CELLPHONE_IMEI.equals(rollbackDTO.getTarget())) {
                transactionalCompensateDeleteCellphoneImei(rollbackDTO);
            }
        }

    }
    @Override
    public void transactionalCompensateDeleteUser(RollbackDTO rollbackDTO) {
        try {
                Map<String, Object> map = rollbackDTO.getDataMap();
                if (map.get("cnickid")!=null&&map.get("sysdate")!=null) {
                    rollBackMapper.deleteUser((String)map.get("cnickid"),(Date)map.get("sysdate"));
                }
        } catch (Exception e) {
            log.error("回滚删除tb_user表出错",e);
        }

    }

    @Override
    public void transactionalCompensateUpdateUser(RollbackDTO rollbackDTO) {

    }

    @Override
    public void transactionalCompensateDeleteUserAcct(RollbackDTO rollbackDTO) {
        try {

                Map<String, Object> map = rollbackDTO.getDataMap();
                if (map.get("cnickid")!=null&&map.get("sysdate")!=null) {
                    rollBackMapper.deleteUserAcct((String)map.get("cnickid"),(Date)map.get("sysdate"));
                }

        } catch (Exception e) {
            log.error("回滚删除tb_user_acct表出错",e);
        }
    }

    @Override
    public void transactionalCompensateUpdateUserAcct(RollbackDTO rollbackDTO) {

    }

    @Override
    public void transactionalCompensateDeleteAlly(RollbackDTO rollbackDTO) {
        try {
                Map<String, Object> map = rollbackDTO.getDataMap();
                if (map.get("nickid")!=null&&map.get("allyid")!=null&&map.get("sysdate")!=null) {
                    rollBackMapper.deleteAlly((String)map.get("nickid"),(String)map.get("allyid"),(Date)map.get("sysdate"));
                }

        } catch (Exception e) {
            log.error("回滚删除tb_ally表出错",e);
        }
    }

    @Override
    public void transactionalCompensateUpdateAlly(RollbackDTO rollbackDTO) {

    }

    @Override
    public void transactionalCompensateDeleteAllyLog(RollbackDTO rollbackDTO) {
        try {
                Map<String, Object> map = rollbackDTO.getDataMap();
                if (map.get("cnickid")!=null&&map.get("sysdate")!=null) {
                    rollBackMapper.deleteAllyLog((String)map.get("cnickid"),(Date)map.get("sysdate"));
                }

        } catch (Exception e) {
            log.error("回滚删除tb_ally_log表出错",e);
        }
    }

    @Override
    public void transactionalCompensateUpdateAllyLog(RollbackDTO rollbackDTO) {

    }

    @Override
    public void transactionalCompensateDeleteCellphoneImei(RollbackDTO rollbackDTO) {
        try {

                Map<String, Object> map = rollbackDTO.getDataMap();
                if (map.get("cnickid")!=null&&map.get("sysdate")!=null) {
                    rollBackMapper.deleteCellphoneImei((String)map.get("cnickid"),(Date)map.get("sysdate"));
                }

        } catch (Exception e) {
            log.error("回滚删除tb_cellphone_imei表出错",e);
        }
    }

    @Override
    public void transactionalCompensateUpdateCellphoneImei(RollbackDTO rollbackDTO) {

    }
}

package com.caiyi.lottery.tradesystem.usercenter.service;

import com.caiyi.lottery.tradesystem.base.RollbackDTO;

/**
 * 回调处理Service
 *
 * @author GJ
 * @create 2017-12-29 18:08
 **/
public interface RollBackService {

    /**
     * 安全中心事务回滚
     * @param rollbackDTO
     */
    void  transactionalCompensateSafeCenter(RollbackDTO rollbackDTO);

    void transactionalCompensateDelete(RollbackDTO rollbackDTO);

    /**
     * 删除用户回滚
     */
    void transactionalCompensateDeleteUser(RollbackDTO rollbackDTO);

    /**
     * 更新用户回滚
     */
    void transactionalCompensateUpdateUser(RollbackDTO rollbackDTO);

    /**
     * tb_user_acct
     */
    void transactionalCompensateDeleteUserAcct(RollbackDTO rollbackDTO);
    /**
     * tb_user_acct
     */
    void transactionalCompensateUpdateUserAcct(RollbackDTO rollbackDTO);

    /**
     * tb_ally
     */
    void transactionalCompensateDeleteAlly(RollbackDTO rollbackDTO);
    /**
     * tb_ally
     */
    void transactionalCompensateUpdateAlly(RollbackDTO rollbackDTO);

    /**
     * tb_ally_log
     */
    void transactionalCompensateDeleteAllyLog(RollbackDTO rollbackDTO);
    /**
     * tb_ally_log
     */
    void transactionalCompensateUpdateAllyLog(RollbackDTO rollbackDTO);
    /**
     * tb_cellphone_imei
     */
    void transactionalCompensateDeleteCellphoneImei(RollbackDTO rollbackDTO);
    /**
     * tb_cellphone_imei
     */
    void transactionalCompensateUpdateCellphoneImei(RollbackDTO rollbackDTO);
}

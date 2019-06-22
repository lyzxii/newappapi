package com.caiyi.lottery.tradesystem.base;

import java.io.Serializable;
import java.util.Map;

/**
 * 回滚操作DTO
 *
 * @author GJ
 * @create 2017-12-29 17:07
 **/
public class RollbackDTO  implements Serializable{


    /**
     * 提交操作
     */
    private String commitOperation;
    /**
     * 回滚操作
     */
    private String rollbackOperation;

    /**
     * 操作表
     */
    private String target;
    /**
     * 回滚业务源
     */
    private String source;

    /**
     * 回滚数据
     */
    private Map<String,Object> dataMap;


    public RollbackDTO(){

    }

    public RollbackDTO(String commitOperation, String rollbackOperation) {
        this.commitOperation = commitOperation;
        this.rollbackOperation = rollbackOperation;
    }

    public RollbackDTO(String commitOperation, String rollbackOperation, String target,String source,Map<String,Object> dataMap) {
        this.commitOperation = commitOperation;
        this.rollbackOperation = rollbackOperation;
        this.target = target;
        this.source = source;
        this.dataMap = dataMap;
    }


    public String getCommitOperation() {
        return commitOperation;
    }

    public void setCommitOperation(String commitOperation) {
        this.commitOperation = commitOperation;
    }

    public String getRollbackOperation() {
        return rollbackOperation;
    }

    public void setRollbackOperation(String rollbackOperation) {
        this.rollbackOperation = rollbackOperation;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }
}

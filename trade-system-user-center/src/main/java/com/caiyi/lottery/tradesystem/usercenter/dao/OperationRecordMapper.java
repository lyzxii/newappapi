package com.caiyi.lottery.tradesystem.usercenter.dao;

import dto.FeedBackDTO;
import dto.OperationReCordDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by User on 2017/11/30.
 */
@Mapper
public interface OperationRecordMapper {

    @Insert("insert into tb_operation_record(cid,cnickid,iwhitegrade,cbanginginfo,clogintype,cclientname,csource,cclientvarsion,cphonemodel,cphonesys,cnetwork,cip,cposition,caddtime) values(#{cid,jdbcType=VARCHAR},#{cnickid,jdbcType=VARCHAR},#{iwhitegrade,jdbcType=INTEGER},#{cbanginginfo,jdbcType=VARCHAR},#{clogintype,jdbcType=VARCHAR},#{cclientname,jdbcType=VARCHAR},#{csource,jdbcType=VARCHAR},#{cclientvarsion,jdbcType=VARCHAR},#{cphonemodel,jdbcType=VARCHAR},#{cphonesys,jdbcType=VARCHAR},#{cnetwork,jdbcType=VARCHAR},#{cip,jdbcType=VARCHAR},#{cposition,jdbcType=VARCHAR},sysdate)")
    int addUserOperationRecord (OperationReCordDTO operationReCordDTO);

    @Insert("insert into TB_PRODUCT_FEEDBACK(cid,CNICKID,CFEEDBACKCONTENT,CFEEDBACKPICONE,CFEEDBACKPICTWO,CFEEDBACKPICTHREE,CCONTACTWAY,IWHITEGRADE,CBANGINGINFO,CLOGINTYPE,CCLIENTNAME,CSOURCE,CCLIENTVARSION,CPHONEMODEL,CPHONESYS,CNETWORK,CIP,CPOSITION,CADDTIME) values(#{cid,jdbcType=VARCHAR},#{cnickid,jdbcType=VARCHAR},#{cfeedbackcontent,jdbcType=VARCHAR},#{cfeedbackpicone,jdbcType=VARCHAR},#{cfeedbackpictwo,jdbcType=VARCHAR},#{cfeedbackpicthree,jdbcType=VARCHAR},#{ccontactway,jdbcType=VARCHAR},#{iwhitegrade,jdbcType=INTEGER},#{cbanginginfo,jdbcType=VARCHAR},#{clogintype,jdbcType=VARCHAR},#{cclientname,jdbcType=VARCHAR},#{csource,jdbcType=VARCHAR},#{cclientvarsion,jdbcType=VARCHAR},#{cphonemodel,jdbcType=VARCHAR},#{cphonesys,jdbcType=VARCHAR},#{cnetwork,jdbcType=VARCHAR},#{cip,jdbcType=VARCHAR},#{cposition,jdbcType=VARCHAR},sysdate)")
    int addProductFeedBack (FeedBackDTO feedBackDTO);
}

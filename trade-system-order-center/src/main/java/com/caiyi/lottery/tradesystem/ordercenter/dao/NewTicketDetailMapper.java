package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.NewTicketDetailPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author GJ
 * @create 2018-04-02 17:07
 **/
@Mapper
public interface NewTicketDetailMapper {
    @Select("select cprojid projid,cperiodid periodid,istate,capplyid applyid,imulity mulity,imulity nums,ccodes codes,clscode lscode,ilsmoney lsmoney,clswininfo lswininfo from  tb_new_ticket_detail   where cprojid=#{hid}")
    List<NewTicketDetailPojo> getTicketDetail(@Param("hid") String hid);
}

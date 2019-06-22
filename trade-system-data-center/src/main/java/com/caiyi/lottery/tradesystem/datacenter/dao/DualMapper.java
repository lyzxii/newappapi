package com.caiyi.lottery.tradesystem.datacenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 系统测试用的dual表
 */
@Mapper
public interface DualMapper {
    @Select(" select 1 from dual ")
    String check();
}

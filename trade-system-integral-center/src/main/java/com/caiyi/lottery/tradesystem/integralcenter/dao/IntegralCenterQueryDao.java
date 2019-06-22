package com.caiyi.lottery.tradesystem.integralcenter.dao;

import integral.bean.IntegralPageBean;
import org.apache.ibatis.annotations.Param;
import integral.bean.IntegralBean;
import integral.bean.IntegralParamBean;

import java.util.List;

public interface IntegralCenterQueryDao {

    IntegralBean queryBasicInfo(@Param("uid") String uid);

    int hasSignRecord(@Param("uid") String uid);

    IntegralBean isContinuous(@Param("uid") String uid);

    int clearSignDays(@Param("uid") String uid);

    int clearSignStatus(@Param("uid") String uid);

    IntegralBean getSignInfo(@Param("uid") String uid);

    String queryDayPoints(@Param("uid") String uid);

    int isBindIcCard(@Param("uid") String uid);

    int isBindBankCard(@Param("uid") String uid);

    String queryIsGetPoints(@Param("uid") String uid);

    String cannotSign(@Param("uid") String uid);

    int insertUserToSign(@Param("uid") String uid, @Param("date") String date, @Param("isigned") String isigned, @Param("istatus") String istatus);

    int clickToSign(IntegralParamBean bean);

    int insertPointCharge(IntegralParamBean bean);

    int updateUserPoint(IntegralParamBean bean);

    int clickToGetPoints(@Param("itask") String itask, @Param("uid") String uid, @Param("task") String task, @Param("bitand") String bitand);

    IntegralBean queryVipUserInfo(@Param("uid") String uid);

    IntegralBean getNextLevel(@Param("level") int level);

    List<IntegralPageBean> experienceDetail(@Param("uid") String uid);

    List<IntegralPageBean> pointsDetail(@Param("uid") String uid);
}

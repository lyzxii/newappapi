父模块
--------------------------------------------------------------------------
                        模块说明
trade-system-eureka-server:         注册中心，集群部署
    application-server1.properties          节点1
    application-server2.properties          节点2
    application-server3.properties          节点3
trade-system-config-server:         配置中心，集群部署，端口：8201/8202
    trade-system-user-web.properties             用户中心(前端)配置文件
    trade-system-user-center.properties          用户中心(后端)配置文件
    trade-system-trade-web.properties            交易中心(前端)配置文件
    trade-system-trade-center.properties         交易中心(后端)配置文件
    trade-system-pay-web.properties              支付中心(前端)配置文件
    trade-system-pay-center.properties           支付中心(后端)配置文件
    trade-system-activity-web.properties         活动中心(前端)配置文件
    trade-system-activity-center.properties      活动中心(后端)配置文件
    trade-system-redpacket-web.properties        红包中心(前端)配置文件
    trade-system-redpacket-center.properties     红包中心(后端)配置文件
    trade-system-integral-web.properties         积分中心(前端)配置文件
    trade-system-integral-center.properties      积分中心(后端)配置文件
    trade-system-memcached-controller.properties 缓存中心memcached配置文件

trade-system-memcached:             缓存中心memcached，端口：9201
trade-system-redis:                 缓存中心redis，    端口：9202
trade-system-parent:                父POM
trade-system-pojo:                  pojo类和bean类，被依赖
trade-system-user-web:              用户中心前端，集群部署，端口：9101
trade-system-user-center:           用户中心后端，集群部署，端口：9102
trade-system-user-center-client:    用户中心后端Feign接口，被依赖
trade-system-trade-web:             交易中心前端，集群部署，端口：9103
trade-system-trade-center:          交易中心后端，集群部署，端口：9104
trade-system-trade-center-client:   交易中心后端Feign接口，被依赖
trade-system-pay-web:               支付中心前端，集群部署，端口：9105
trade-system-pay-center:            支付中心后端，集群部署，端口：9106
trade-system-pay-center-client:     支付中心后端Feign接口，被依赖
trade-system-activity-web:          活动中心前端，集群部署，端口：9107
trade-system-activity-center:       活动中心后端，集群部署，端口：9108
trade-system-activity-center-client:活动中心后端Feign接口，被依赖
trade-system-safe-center:           安全中心后端，集群部署，端口：9109
trade-system-safe-center-client:    安全中心后端Feign接口，被依赖
trade-system-util:                  工具类模块，被依赖

trade-system-redpacket-web:          红包中心前端，集群部署，端口：9110
trade-system-redpacket-center:       红包中心后端，集群部署，端口：9111
trade-system-redpacket-center-client 红包中心后端Feign接口，被依赖

trade-system-integral-web:          积分中心前端，集群部署，端口：9112
trade-system-integral-center:       积分中心后端，集群部署，端口：9113
trade-system-integral-center-client 积分中心后端Feign接口，被依赖

trade-system-order-web:          订单前端，集群部署，端口：9115
trade-system-order-center:       订单后端，集群部署，端口：9114
trade-system-order-client        订单后端Feign接口，被依赖

trade-system-task:                任务中心，    端口：9210
--------------------------------------------------------------------------
                        测试服务器
服务器1：   192.168.1.121
服务器2：   192.168.1.122

eureka-server:    192.168.1.121:8101    节点1     hostname:server1
                  192.168.1.121:8102    节点2     hostname:server2
                  192.168.1.122:8103    节点3     hostname:server3
config-server:    192.168.1.121:8201    节点1
                  192.168.1.122:8202    节点2
user-web:         192.168.1.121:9101    节点1
                  192.168.1.122:9101    节点2
user-center:      192.168.1.121:9102    节点1
                  192.168.1.122:9102    节点2
trade-web:        192.168.1.121:9103    节点1
                  192.168.1.122:9103    节点2
trade-center:     192.168.1.121:9104    节点1
                  192.168.1.122:9104    节点2
pay-web:          192.168.1.121:9105    节点1
                  192.168.1.122:9105    节点2
pay-center:       192.168.1.121:9106    节点1
                  192.168.1.122:9106    节点2
activity-web:     192.168.1.121:9107    节点1
                  192.168.1.122:9107    节点2
activity-center:  192.168.1.121:9108    节点1
                  192.168.1.122:9108    节点2
safe-center:      192.168.1.121:9109    节点1
                  192.168.1.122:9109    节点2

redpacket-web:    192.168.1.121:9110    节点1
                  192.168.1.122:9110    节点2

redpacket-center: 192.168.1.121:9111    节点1
                  192.168.1.122:9111    节点2

integral-web:     192.168.1.121:9112    节点1
                  192.168.1.122:9112    节点2

integral-center:  192.168.1.121:9113    节点1
                  192.168.1.122:9113    节点2

--------------------------------------------------------------------------
                        测试URL
eureka-server:
    http://192.168.1.121:8101/
    http://192.168.1.121:8102/
    http://192.168.1.122:8103/
config-server:
    http://192.168.1.121:8202/trade-system-user-center.properties
    http://192.168.1.122:8202/trade-system-user-center.properties
trade-web 例子：
    1、对阵列表接口:
    http://192.168.1.121:9103/trade/m.go?gid=1&pid=2
    http://192.168.1.122:9103/trade/m.go?gid=1&pid=2
    相当于 rbc 框架的配置文件 trade_action.xml ：
    <action name="m" forward="" bean="com.caipiao.trade.PeriodBean" scope="request" desc="对阵列表">
    	<execute method="loadCacheMatch" group="2" />
    </action>

    2、展开二级菜单：
    http://192.168.1.121:9103/trade/detail.go?appversion=459&source=1000&accesstoken=+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvsAav07OjvB6A3Q448S7mpVEZEMBJuwuM1uZlWhzQBcXZ9Tgsb0zvCTO8Pl5QtxWw0mWKU+tX+aR2XeMHKdkldLMsGpnb+PzfIdvpE0uJgo5/skzpPhxAojYFiot6VeAY9xK/bsYjA2Sg==&appid=lt20ZZ17U1JFY0P200D2K5130T8BB8IQ3&logintype=1&mtype=1&rversion=4.5.9&imei=Qzk5MzZBMDkwNTkzM0JGQjg5MEQ3MERGRjVCODNGN0Y=&osversion=5.1.0&gid=50&pid=2017121&
    相当于 rbc 框架的配置文件 trade_action.xml ：
    <action name="detail" forward="" bean="com.caipiao.trade.TradeBean" scope="request" desc="开奖结果二级菜单">
    	<execute method="getDetail" />
    </action>

    3、删除追号记录：（典型例子）
    http://192.168.1.121:9103/trade/hidezhdetail.go?appversion=454&source=1027&accesstoken=+NEflO3uj02eOaWPdCVSbDiORgxuKQuyVLKkfCeHMvsAuYKEpwj+UiWksCZGTLCOEH5LnBQdHKQ79Zohq2XQE1YD2tOXDjuCY8RdpwhzE8SuEzPIDecmyQ6aWQIGpW+sPSM6EhoawpjkJ/12SdaPLGduSIPRFNYE1jWzQHWnI/17YdZt8q279Q==&appid=lt2K017C0EE7F2M80D0XY0004ON88JGO3&logintype=1&mtype=1&rversion=4.5.4&imei=Q0JDOTE3OTUyQzQyMzk1MThDRTM2NkExQTlFM0YwRDM=&osversion=7.0.0&pid=01ZH10110017&bid=01ZH10110017&gid=01&
    相当于 rbc 框架的配置文件 trade_action.xml ：
    <action name="hidebuyrecord" bean="com.caipiao.trade.TradeBean" scope="request" desc="隐藏投注记录">
    	<execute method="set_user_data"  />
    	<execute method="check_login" />
    	<execute method="hideBuyRecord" group="2" />
    </action>

    rabbitmq 管理后台 http://192.168.99.237:15672


    1，DAO 层 返回一个字段时，可以直接用 基本类型
    2，DAO 层 返回超过一个字段时，用相应的pojo作为返回值，禁止用Map
    3,DAO 层 返回超过一个字段时且含有多张表的数据，用T1pojo_T2pojo对象作为返回值，
    以上
    1)没有相应的pojo，自行新建，一个pojo对应一个表
    2)当存在联表查询时，没有相应的pojo,自行新建，命名格式T1_T2_.....Pojo  T1,T2 按英文字母排序
    3)存在相应的pojo,没有所需的字段，自行添加


   1， 一个dao层类对应一个表增删改查，没有相应的dao层，自行新建
   2， 当SQL语句关联多张表时，没有相应的dao层，自行新建 ，命名规则，B1_B2_......Mapper B1,B2按英文字母排序
   3， 超过3张表关联的语句时，统一添加在ComplexMapper

   入参规则
   1，app——>web       业务各自bean
   2，web——>center    Request及子类
   3，center——>center Request及子类
   出参规则
   1，center——>center Response及子类
   2，center——>web    Response及子类
   3，web——>app       Result
   
   缓存规则
   存取内容共三类:分为普通字符串,xml字符串和json字符串
		           对象全部实现toJson()方法  
		           存取对象全部将对象转成json字符串格式存取

	数据库添加字段
	tb_user_bankbinding 加了  CREALNAMEMD5，CMOBILEMD5，CIDCARDMD5 ，CBANKCARDMD5,COLDBANKCARDMD5
	tb_user 加了 CREALNAMEMD5，CMOBILENOMD5，CIDCARDMD5，CBANKCARDMD5，  CCARDMOBILEMD5
	tb_ally 加了 CREALNAMEMD5，CMOBILENOMD5，CIDCARDMD5
	tb_bind_msg 加了CMOBILENOMD5
	tb_sms 加了CMOBILENOMD5
	TB_WX_USER_BIND 加了 CMOBILENOMD5
	TB_REDPACKET_NEW_TASK 加了CMOBILENOMD5， CIDCARDMD5

	tb_proj_50 添加字段ilsmoney,ilsaward
	tb_zh_detail_50 添加字段ilsmoney,ilsaward
	tb_new_ticket_detail 添加字段ilsmoney,clscode,clswininfo


	alter table tb_user add (CREALNAMEMD5 varchar(100),CMOBILENOMD5 varchar(100),CIDCARDMD5 varchar(100),CBANKCARDMD5 varchar(100),CCARDMOBILEMD5 varchar(100));
    comment on column tb_user.CREALNAMEMD5 is '真实姓名md5值';
    comment on column tb_user.CMOBILENOMD5 is '手机号MD5值';
    comment on column tb_user.CIDCARDMD5 is '身份证MD5值';
    comment on column tb_user.CBANKCARDMD5 is '银行卡号MD5值';
    comment on column tb_user.CCARDMOBILEMD5 is '银行卡绑定MD5值';


    alter table tb_user_bankbinding add (CREALNAMEMD5 varchar(100),CMOBILEMD5 varchar(100),CIDCARDMD5 varchar(100),CBANKCARDMD5 varchar(100),COLDBANKCARDMD5 varchar(100));
    comment on column tb_user_bankbinding.CREALNAMEMD5 is '真实姓名md5值';
    comment on column tb_user_bankbinding.CMOBILEMD5 is '手机号MD5值';
    comment on column tb_user_bankbinding.CIDCARDMD5 is '身份证MD5值';
    comment on column tb_user_bankbinding.CBANKCARDMD5 is '银行卡号MD5值';
    comment on column tb_user_bankbinding.COLDBANKCARDMD5 is '旧银行卡绑定MD5值';



    alter table tb_ally add (CREALNAMEMD5 varchar(100),CMOBILENOMD5 varchar(100),CIDCARDMD5 varchar(100));
    comment on column tb_ally.CREALNAMEMD5 is '真实姓名md5值';
    comment on column tb_ally.CMOBILENOMD5 is '手机号MD5值';
    comment on column tb_ally.CIDCARDMD5 is '身份证MD5值';

    alter table tb_bind_msg add (CMOBILENOMD5 varchar(100));
    comment on column tb_bind_msg.CMOBILENOMD5 is '手机号MD5值';

    alter table tb_sms add (CMOBILENOMD5 varchar(100));
    comment on column tb_sms.CMOBILENOMD5 is '手机号MD5值';

    alter table TB_WX_USER_BIND add (CMOBILENOMD5 varchar(100));
    comment on column TB_WX_USER_BIND.CMOBILENOMD5 is '手机号MD5值';

    alter table TB_REDPACKET_NEW_TASK add (CMOBILENOMD5 varchar(100),CIDCARDMD5 varchar(100));
    comment on column TB_REDPACKET_NEW_TASK.CMOBILENOMD5 is '手机号MD5值';
    comment on column TB_REDPACKET_NEW_TASK.CIDCARDMD5 is '身份证MD5值';




	存储过程修改
      cpuser_new.sp_user_bind_yz
      cpuser_new.sp_user_allyregister
      cpuser_new.sp_user_register
      cpuser_new.sp_user_login (平移)

检测url:
192.168.1.121:8201/config/checklocalhealth.api

192.168.1.121:9107/activity/checklocalhealth.api
192.168.1.122:9108/activity/checklocalhealth.api

192.168.1.121:9112/integral/checklocalhealth.api
192.168.1.122:9113/integral/checklocalhealth.api

192.168.1.121:9115/order/checklocalhealth.api
192.168.1.122:9114/order/checklocalhealth.api

192.168.1.121:9105/pay/checklocalhealth.api
192.168.1.122:9106/pay/checklocalhealth.api

192.168.1.122:9202/redis/checklocalhealth.api

192.168.1.121:9110/redpacket/checklocalhealth.api
192.168.1.122:9111/redpacket/checklocalhealth.api

192.168.1.121:9109/safe/checklocalhealth.api

192.168.1.121:9101/user/checklocalhealth.api
192.168.1.122:9102/user/checklocalhealth.api

192.168.1.121:9103/trade/checklocalhealth.api
192.168.1.122:9104/trade/checklocalhealth.api


public class GodComingThreadEX 此线程为分享神单用户明细数据统计,后期需要存在redis中

新增xml文件 /opt/export/www/cms/news/ad/gyj_name.xml，/opt/export/www/cms/news/ad/topic_football.xml
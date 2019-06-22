<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE8" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta name="author" content="m.9188.com" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta name="format-detection" content="telephone=no" />
<title>请求页面过期</title>

<style type="text/css">
body{ background:#f0eff4; font-size:14px; font-family:arial,Microsoft YaHei, Helvetica, sans-serif; color:#222; tap-highlight-color:rgba(0,0,0,0); -webkit-tap-highlight-color:rgba(0,0,0,0); -moz-tap-highlight-color:rgba(0,0,0,0)}
html,body,p,span{ margin:0; padding:0}
@media screen and (min-width:480px){html,body,button,input,select,textarea{font-size:18px}}
@media screen and (min-width:640px){html,body,button,input,select,textarea{font-size:24px}}
em,cite{ font-style:normal}
p{ text-align:center; font-size:.875rem; color:#5c5c5c}
.header{ background:#fff; height:2.45rem; padding:.3rem; border-bottom:1px solid #dadada}
.logo{ width:8.1rem; margin:0 auto; overflow:hidden; zoom:1}
/*.logo span{ float:left; margin-right:.3rem; width:2.44rem; height:2.44rem; background:url(../../../image/logo2.png) no-repeat center; -webkit-background-size:2.44rem 2.44rem}*/
.logo p{ float:left; text-align:left; font-size:.75rem; color:#444; line-height:1.2rem}
/*.logo p cite{ font-size:1.2rem; color:#222; display:block; margin-top:.1rem}*/
.pic{ width:4.625rem; height:4.625rem; margin:3.2rem auto .6rem auto; background:url(/META-INF/resources/image/pic.png) no-repeat center; -webkit-background-size:4.625rem 4.625rem}
.tell{ display:block; color:#5d5d5d; width:80%; margin:2rem auto 0 auto; text-align:center; font-size:1rem; text-decoration:none}
.tell em{ color:#f68a2c; font-family:arial; font-style:normal; font-size:1.1rem}
.back{ display:block; width:93%; text-decoration:none; margin:1.2rem auto 5rem auto; line-height:2.5rem; text-align:center; color:#fff; font-size:1.1rem; background:#f6851f; border-radius:.2rem; -webkit-border-radius:.2rem; -moz-border-radius:.2rem}
</style>

<script type="text/javascript">
function returnClient(){
	var url = 'caiyi9188Lottery';
	var tradeUrl = document.getElementById("urlTrade").value;
	var bankUrl = document.getElementById("urlBank").value;
	if (tradeUrl != 'no' && tradeUrl != '') {
        url = tradeUrl;
    } else if (bankUrl != 'no' && bankUrl != '') {
        url = bankUrl;
    }
    
	url = url + '://purcharesLottery?returnCode=-1';
    window.location.href = url;
}

</script>
</head>

<body style="background:#f0eff4">
<input id="urlBank" type="hidden" name="appSchemeBank" value="${drawInfo.appScheme}"/>
<input id="urlTrade" type="hidden" name="appSchemeTrade" value="${betInfo.appScheme}"/>
	<header class="header">
        <div class="logo">
            <p>安全支付</p>
        </div>
	</header>
    <div class="pic"></div>
    <p>请求页面过期</p>
    <a href="#" onclick="javascript:returnClient();" class="back">返回客户端</a>
    <a class="tell" href="tel:400-673-9188">客服电话：<em>400-673-9188</em></a>
</body>

</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta name="author" content="m.9188.com">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0;" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta name="format-detection" content="telephone=no" />
<meta name="msapplication-tap-highlight" content="no">
<meta http-equiv="cleartype" content="on">
<title></title>
<link href="../../../css/global.css" rel="stylesheet" media="screen">
<script type="text/javascript" src="../../../js/jquery-1.5.2.js"></script>
</head>
<script type="text/javascript">
$(document).ready(function(){		
	var times = 0;
	$("#submitOrder").click(function() {
		times++;
		if (times == 1) {
			$("#betForm").submit();
		} else {
			alert("若要重复投注请刷新本页面!");
		}
	});
});
</script>
<body style="background-color: #fff;">
	<form id="betForm" action="betJjyh.go" method="post">
		<input type="hidden" name="cType" value="${betInfo.cType}" /> <input
			id="url" type="hidden" name="appScheme" value="${betInfo.appScheme}" />
		<input type="hidden" name="cupacketid" value="${betInfo.cupacketid}" />
		<input type="hidden" name="redpacket_money"
			value="${betInfo.redpacket_money}" /> <input type="hidden"
			name="func" value="${betInfo.func}" /> <input type="hidden"
			name="source" value="${betInfo.source}" /> <input type="hidden"
			name="logintype" value="${betInfo.logintype}" /> <input
			type="hidden" name="accesstoken" value="${betInfo.accesstoken}" /> <input
			type="hidden" name="appid" value="${betInfo.appid}" /> <input
			type="hidden" name="appversion" value="${betInfo.appversion}" /> <input
			type="hidden" name="mtype" value="${betInfo.mtype}" />

		<c:if test="${betInfo.cType=='FQHM'||betInfo.cType=='ZG'}">
			<input type="hidden" name="allnum" value="${betInfo.allnum}" />
			<input type="hidden" name="amoney" value="${betInfo.amoney}" />
			<input type="hidden" name="baodinum" value="${betInfo.baodinum}" />
			<input type="hidden" name="beishu" value="${betInfo.beishu}" />
			<input type="hidden" name="buynum" value="${betInfo.buynum}" />
			<input type="hidden" name="codes" value="${betInfo.codes}" />
			<input type="hidden" name="comeFrom" value="${betInfo.comeFrom}" />
			<input type="hidden" name="expect" value="${betInfo.expect}" />
			<input type="hidden" name="initems" value="${betInfo.initems}" />
			<input type="hidden" name="ishm" value="${betInfo.ishm}" />
			<input type="hidden" name="isshow" value="${betInfo.isshow}" />
			<input type="hidden" name="items" value="${betInfo.items}" />
			<input type="hidden" name="lotid" value="${betInfo.lotid}" />
			<input type="hidden" name="newcodes" value="${betInfo.newcodes}" />
			<input type="hidden" name="tcbili" value="${betInfo.tcbili}" />
			<input type="hidden" name="title" value="${betInfo.title}" />
			<input type="hidden" name="content" value="${betInfo.content}" />
			<input type="hidden" name="upay" value="${betInfo.upay}" />
			<input type="hidden" name="totalMoney" value="${betInfo.totalMoney}" />
			<input type="hidden" name="extendtype" value="${betInfo.extendtype}" />
			<input type="hidden" name="yhfs" value="${betInfo.yhfs}" />
			<input type="hidden" name="imoneyrange"
				value="${betInfo.imoneyrange}" />
		</c:if>

	</form>
	<header>
		<div class="logo_zf"></div>
	</header>
	<div class="money">
		<h2>
			￥
			<c:if test="${betInfo.cType=='FQHM'}">${betInfo.buynum+betInfo.bdMoney}</c:if>
			<c:if test="${betInfo.cType!='FQHM'}">${betInfo.amoney}</c:if>
			元
		</h2>
	</div>
	<div class="main">
		<ul>
			<li><span>玩法</span> <span>${betInfo.gid_name }</span></li>
			<li><span>类型</span> <span><c:if
						test="${betInfo.cType=='FQHM'}">
					 	合买发起
					 </c:if> <c:if test="${betInfo.cType=='ZG'}">
					 	自购
					 </c:if></span></li>
			<li><span>详细</span> <span><c:if
						test="${betInfo.cType=='ZG'}">
					 	${betInfo.zs}注
					 </c:if></span></li>
		</ul>
	</div>
	<div class="btn">
		<button id="submitOrder">确认预约</button>
	</div>
	<!-- 底部电话号码 -->
	<!-- <div class="tel">
		<a href=”tel:400-637-9188″>400-637-9188</a>
	</div> -->
</body>
</html>
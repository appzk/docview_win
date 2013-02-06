<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title></title>
	<link rel="stylesheet" rev="stylesheet" type="text/css" media="all" href="/static/preview/css/base/doc-preview.css" />
	<script type="text/javascript" src="/static/js/jquery.js"></script>
	<script type="text/javascript" src="/static/preview/js/jScrollPane.js"></script>
	<script type="text/javascript" src="/static/preview/js/jquery.mousewheel.js"></script>
	<script type="text/javascript" src="/static/preview/js/bootstrap-tab.js"></script>
	<script type="text/javascript">
$(function()
{
	$('.scroll-pane').jScrollPane();
});
</script>
</head>
<body>
	<div class="doc wrapClear">
		<div id="hd" class="fixed">
			<div class="header wrapClear">
				<div class="logo">
					<a href="http://www.idocv.com"><img src="/static/preview/images/base/logo.png" alt="" /></a>
				</div>
			</div>
		</div>
		<div id="bd">
			<div class="backToTop" style="cursor: pointer; display: block;" title="返回顶部">
				<img src="/static/preview/images/base/backtop.png" />
			</div>
			<div class="fileTitle">
				<a href="/doc/download?id=${page.rid}">下载</a>
				<div>
					<strong><c:out value="${page.name}"></c:out></strong>
				</div>
			</div>
			<div class="page">
				<div class="contentPPT">
					<center>
						<c:forEach items="${page.data}" var="vo" varStatus="status">
							<img src="${vo.url}" alt="" /><br />
							第&nbsp;<c:out value="${status.count}"></c:out>&nbsp;页<br /><br />
						</c:forEach>
					</center>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
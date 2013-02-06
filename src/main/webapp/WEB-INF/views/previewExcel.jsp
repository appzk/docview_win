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
	<link rel="stylesheet" rev="stylesheet" type="text/css" media="all" href="/static/bootstrap/css/bootstrap.css" />
	<script type="text/javascript" src="/static/js/jquery.js"></script>
	<script type="text/javascript" src="/static/bootstrap/js/bootstrap.js"></script>
	<script type="text/javascript">
		$(function() {
			$('.scroll-pane').jScrollPane();
		});
	</script>
</head>
<body>
	<div class="totaldoc wrapClear">
		<div id="hd" class="fixed">
			<div class="header wrapClear">
				<div class="logo">
					<a href="http://www.idocv.com"><img src="/static/preview/images/base/logo.png" alt="" /></a>
				</div>
			</div>
		</div>
		<div id="bd">
			<div class="fileTitle">
				<div class="titleContent">
					<a href="/doc/download?id=${page.rid}">下载</a>
					<div>
						<strong><c:out value="${page.name}"></c:out></strong>
					</div>
				</div>
			</div>
			<div class="tabBox">
				<div class="tabbable">
					<ul class="nav nav-tabs">
						<c:forEach items="${page.data}" var="vo" varStatus="status">
							<c:choose>
								<c:when test="${status.count=='1'}">
									<li class="active"><a href="#tab${status.count}" data-toggle="tab"><c:out value="${vo.title}"></c:out></a></li>
								</c:when>
								<c:otherwise>
									<li><a href="#tab${status.count}" data-toggle="tab"><c:out value="${vo.title}"></c:out></a></li>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</ul>
				</div>
			</div>
			<div class="tab-content">
				<c:forEach items="${page.data}" var="vo" varStatus="status">
					<c:choose>
						<c:when test="${status.count=='1'}">
							<div class="tab-pane active" id="tab${status.count}">
								<br /><br /><c:out value="${vo.content}" escapeXml="false"></c:out>
							</div>
						</c:when>
						<c:otherwise>
							<div class="tab-pane" id="tab${status.count}">
								<br /><br /><c:out value="${vo.content}" escapeXml="false"></c:out>
							</div>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</div>
		</div>
	</div>
</body>
</html>
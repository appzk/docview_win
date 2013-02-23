<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<title>Presentation - I Doc View</title>

<meta charset='utf-8'>
<script src='/static/googleppt/slides.js'></script>
</head>

<style>
/* Your individual styles here, or just use inline styles if that’s what you want. */
</style>

<body style='display: none'>

	<section class='slides layout-regular template-default'>

		<!-- Your slides (<article>s) go here. Delete or comment out the slides below. -->

		<c:set var="pageSize" value="${fn:length(page.data)}"></c:set>
		
		<c:forEach items="${page.data}" var="vo" varStatus="status">
			<article>
				<img src="${vo.url}" width="772" height="579" /><br />
				page&nbsp;<c:out value="${status.count}"></c:out>&nbsp;of&nbsp;<c:out value="${pageSize}"></c:out> <br /><br />
			</article>
		</c:forEach>

	</section>

	<script type="text/javascript" src="/static/idocv/js/stat.js"></script>

</body>
</html>
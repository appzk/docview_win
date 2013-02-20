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
				<img src="${vo.url}" width="772" height="579" class="ppt-slide-img" /><br />
				page&nbsp;<c:out value="${status.count}"></c:out>&nbsp;of&nbsp;<c:out value="${pageSize}"></c:out> <br /><br />
			</article>
		</c:forEach>

	</section>

<!-- Start Google Analytics -->
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-38005269-2']);
  _gaq.push(['_trackPageview']);
  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script><!-- end Google Analytics -->

<!-- Start of StatCounter Code for Default Guide -->
<script type="text/javascript">
var sc_project=8658950; 
var sc_invisible=1; 
var sc_security="d4122c0b"; 
var scJsHost = (("https:" == document.location.protocol) ?
"https://secure." : "http://www.");
document.write("<sc"+"ript type='text/javascript' src='" +
scJsHost+
"statcounter.com/counter/counter.js'></"+"script>");
</script>
<noscript><div class="statcounter"><a title="create counter"
href="http://statcounter.com/free-hit-counter/"
target="_blank"><img class="statcounter"
src="http://c.statcounter.com/8658950/0/d4122c0b/1/"
alt="create counter"></a></div></noscript>
<!-- End of StatCounter Code for Default Guide -->

</body>
</html>
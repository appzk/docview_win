<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<!--[if IE 7]>                  <html class="ie7 no-js" lang="en">     <![endif]-->
<!--[if lte IE 8]>              <html class="ie8 no-js" lang="en">     <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!--> <html class="not-ie no-js" lang="en">  <!--<![endif]-->
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	
	<title>Text - I Doc View</title>
	
	<meta name="description" content="Online Document Preview, synchronous preview and annotating. types including Microsoft Office types(.doc, .docx, .xls, .xlsx, ppt, pptx), txt, pdf, odt, ods, odp and more.">
	<meta name="keywords" content="Online Document Preview doc view viewer office word excel 在线 文档 同步 协作 预览" />
	<meta name="author" content="Godwin">
	
	<!--[if !lte IE 6]><!-->
		<link rel="stylesheet" href="/static/smart/css/style.css" media="screen" />

		<link rel="stylesheet" href="//fonts.googleapis.com/css?family=Open+Sans:400,600,300,800,700,400italic|PT+Serif:400,400italic" />
		
		<link rel="stylesheet" href="/static/smart/css/fancybox.min.css" media="screen" />
	<!--<![endif]-->

	<!--[if lte IE 6]>
		<link rel="stylesheet" href="//universal-ie6-css.googlecode.com/files/ie6.1.1.css" media="screen, projection">
	<![endif]-->

	<!-- HTML5 Shiv + detect touch events -->
	<script src="/static/smart/js/modernizr.custom.js"></script>

</head>
<body>
<section id="content" class="container clearfix">
	<div class="doc">
		<div class="doc-top">
			<div class="doc-title">
				<a href="/doc/download?id=${page.rid}">Download</a>
				<div>
					<strong><c:out value="${page.name}"></c:out></strong>
				</div>
			</div>
		</div>
		<br /><br />
		<div class="doc-page">
			<div class="doc-content">
				<c:forEach items="${page.data}" var="vo">
					<c:out value="${vo.content}" escapeXml="false"></c:out>
				</c:forEach>
			</div>
		</div>
	</div>

</section><!-- end #content -->

<footer id="footer" class="clearfix">

	<div class="container">

		<nav id="footer-nav" class="clearfix">

			<ul>
				<li><a href="http://www.idocv.com/index.html">Home</a></li>
				<li><a href="http://www.idocv.com/features.html">Features</a></li>
				<li><a href="http://www.idocv.com/examples.html">Examples</a></li>
				<li><a href="http://www.idocv.com/docs.html">Docs</a></li>
				<li><a href="http://www.idocv.com/price.html">Price</a></li>
				<li><a href="http://www.idocv.com/contact.html">Contact</a></li>
			</ul>
			
		</nav><!-- end #footer-nav -->

		<ul class="contact-info">
			<li class="address">Zhongguancun Software Park Incubator, Beijing, Haidian District, Beijing. P.R.China</li>
			<li class="phone">(+86) 1861-189-8831</li>
			<li class="email"><a href="mailto:godwin668@gmail.com">godwin668@gmail.com</a></li>
		</ul><!-- end .contact-info -->
		
	</div><!-- end .container -->

</footer><!-- end #footer -->

<footer id="footer-bottom" class="clearfix">

	<div class="container">

		<ul>
			<li>IDocView &copy; 2013</li>
			<li><a href="#">All rights reserved.</a></li>
		</ul>

	</div><!-- end .container -->

</footer><!-- end #footer-bottom -->

<!--[if !lte IE 6]><!-->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script>window.jQuery || document.write('<script src="/static/smart/js/jquery-1.7.1.min.js"><\/script>')</script>
	<!--[if lt IE 9]> <script src="js/selectivizr-and-extra-selectors.min.js"></script> <![endif]-->
	<script src="/static/smart/js/respond.min.js"></script>
	<script src="/static/smart/js/jquery.easing-1.3.min.js"></script>
	<script src="/static/smart/js/jquery.fancybox.pack.js"></script>
	<script src="/static/smart/js/jquery.jcarousel.min.js"></script>
	<script src="/static/smart/js/jquery.cycle.all.min.js"></script>
	<script src="/static/smart/js/jquery.isotope.min.js"></script>
	<script src="/static/smart/js/jquery.touchSwipe.min.js"></script>
	<script src="/static/smart/js/custom.js"></script>
<!--<![endif]-->

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
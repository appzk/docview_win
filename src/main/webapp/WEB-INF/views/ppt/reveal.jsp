<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!doctype html>
<html lang="en">

	<head>
		<meta charset="utf-8">

		<title>Presentation - I Doc View</title>

		<meta name="description" content="Online Document Preview, synchronous view and annotating. types including Microsoft Office(doc, docx, xls, xlsx, ppt, pptx), txt, pdf, odt, ods, odp and more.">
		<meta name="author" content="Godwin">

		<meta name="apple-mobile-web-app-capable" content="yes" />
		<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />

		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

		<link rel="stylesheet" href="/static/reveal/css/reveal.min.css">
		<link rel="stylesheet" href="/static/reveal/css/theme/default.css" id="theme">

		<!-- For syntax highlighting -->
		<link rel="stylesheet" href="/static/reveal/lib/css/zenburn.css">

		<!-- If the query includes 'print-pdf', use the PDF print sheet -->
		<script>
			document.write( '<link rel="stylesheet" href="/static/reveal/css/print/' + ( window.location.search.match( /print-pdf/gi ) ? 'pdf' : 'paper' ) + '.css" type="text/css" media="print">' );
		</script>

		<!--[if lt IE 9]>
		<script src="lib/js/html5shiv.js"></script>
		<![endif]-->
	</head>

	<body>

		<div class="reveal">

			<!-- Any section element inside of this container is displayed as a slide -->
			<div class="slides">

				<c:set var="pageSize" value="${fn:length(page.data)}"></c:set>
		
				<c:forEach items="${page.data}" var="vo" varStatus="status">
					<section>
						<img src="${vo.url}" width="772" height="579" class="ppt-slide-img" />
					</section>
				</c:forEach>


			</div>

		</div>

		<script src="/static/reveal/lib/js/head.min.js"></script>
		<script src="/static/reveal/js/reveal.min.js"></script>

		<a class="fork-reveal" href="http://www.idocv.com"><img style="position: absolute; top: 0; right: 0; border: 0;" src="http://data.idocv.com/logo-ppt-reveal.png" alt="I Doc View"></a>

		<script>

			// Full list of configuration options available here:
			// https://github.com/hakimel/reveal.js#configuration
			Reveal.initialize({
				controls: true,
				progress: true,
				history: true,
				center: true,

				theme: Reveal.getQueryHash().theme, // available themes are in /css/theme
				transition: Reveal.getQueryHash().transition || 'default', // default/cube/page/concave/zoom/linear/fade/none

				// Optional libraries used to extend on reveal.js
				dependencies: [
					{ src: '/static/reveal/lib/js/classList.js', condition: function() { return !document.body.classList; } },
					{ src: '/static/reveal/plugin/markdown/showdown.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },
					{ src: '/static/reveal/plugin/markdown/markdown.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },
					{ src: '/static/reveal/plugin/highlight/highlight.js', async: true, callback: function() { hljs.initHighlightingOnLoad(); } },
					{ src: '/static/reveal/plugin/zoom-js/zoom.js', async: true, condition: function() { return !!document.body.classList; } },
					{ src: '/static/reveal/plugin/notes/notes.js', async: true, condition: function() { return !!document.body.classList; } }
					// { src: '/static/reveal/plugin/remotes/remotes.js', async: true, condition: function() { return !!document.body.classList; } }
				]
			});

		</script>

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

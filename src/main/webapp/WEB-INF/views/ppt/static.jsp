<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">

	<head>
		<meta charset="utf-8">
	    <title>Presentation - I Doc View</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
	    <meta name="description" content="Online Document Preview, synchronous preview and annotating. types including Microsoft Office types(.doc, .docx, .xls, .xlsx, ppt, pptx), txt, pdf, odt, ods, odp and more.">
	    <meta name="keywords" content="Online Document Preview doc view viewer office word excel 在线 文档 同步 协作 预览" />
	    <meta name="author" content="Godwin">
	
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />

		<!-- BOOTSTRAP STYLE start -->
		<!-- Le styles -->
		<link href="/static/bootstrap/css/bootstrap.css" rel="stylesheet">
		<link href="/static/idocv/css/style.css" rel="stylesheet">
		<link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
		
		<!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
	    <!--[if lt IE 9]>
	      <script src="/static/bootstrap/js/html5shiv.js"></script>
	    <![endif]-->
		<!-- BOOTSTRAP STYLE end -->

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

		<div class="navbar navbar-inverse navbar-fixed-top hidden-phone" style="margin-bottom: 0px;">
			<div class="navbar-inner">
				<div class="container-fluid" style="padding: 0px 20px;">
					<button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<!-- FILE NAME HERE -->
					<a class="brand" style="text-decoration: none;" href="/doc/download/${page.uuid}">${page.name}</a>
				</div>
			</div>
		</div>

		<div class="reveal" style="position: absolute; top: 0px">

			<!-- Any section element inside of this container is displayed as a slide -->
			<div class="slides">

				<!-- SLIDES HERE -->
				<!-- <section><img src="#" class="ppt-slide-img" /></section> -->
				<c:forEach var="pg" items="${page.data}" varStatus="rowCounter">
					<section><img src="${pg.url}" class="ppt-slide-img" /></section>	
	        	</c:forEach>
			</div>

		</div>

		<script src="/static/reveal/lib/js/head.min.js"></script>
		<script src="/static/reveal/js/reveal.min.js"></script>

		<!-- Le javascript
	    ================================================== -->
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
		<script>window.jQuery || document.write('<script src="/static/jquery/js/jquery-1.9.1.min.js"><\/script>')</script>
		<script src="/static/bootstrap/js/bootstrap.min.js"></script>
		<script src="/static/jquerycookie/js/jquery.cookie.js"></script>
		<script src="/static/formvalidator/js/jquery.formvalidator.min.js"></script>
		<!-- <script src="/static/idocv/js/user.js"></script> -->
		<script src="/static/loading/js/simpleLoadingModal.min.js"></script>
		<script src="/static/urlparser/js/purl.js"></script>
		<script src="/static/idocv/js/ppt-static.js"></script>
		<script src="/static/idocv/js/stat.js"></script>
		
		<!-- 
		<a class="fork-reveal" href="http://www.idocv.com"><img style="position: absolute; top: 41px; right: 0; border: 0; max-width: 30%;" src="http://data.idocv.com/idocv-logo-ribbon.png" alt="I Doc View"></a>
		 -->

	</body>
</html>
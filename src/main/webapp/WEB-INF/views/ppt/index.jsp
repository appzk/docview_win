<%@ page contentType="text/html; charset=utf-8"%>
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
  
    <div class="loading-mask" style="display: none;">
      <div class="loading-zone">
        <div class="text">正在载入...0%</div>
        <div class="progress progress-striped active">
          <div class="bar" style="width: 0%;"></div>
        </div>
      </div>
      <div class="brand">
        <footer>
          Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
        </footer>
      </div>
    </div>

	<div class="navbar navbar-inverse navbar-fixed-top hidden-phone" style="margin-bottom: 0px;">
	  <div class="navbar-inner">
		<div class="container-fluid" style="padding: 0px 20px;">
		  <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
		  </button>
		  <!-- FILE NAME HERE -->
		  <!-- QR Code start -->
          <div class="nav-collapse collapse">
            <ul class="nav pull-right">
              <li class="dropdown">
                <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="icon-qrcode icon-white"></i><strong class="caret"></strong></a>
                <div class="dropdown-menu" style="padding: 15px; padding-bottom: 0px;">
                  <div class="qrcode"></div>
                  <div style="text-align: center; margin: 8px;">扫描二维码，在手机或Pad中查看文档</div>
                </div>
              </li>
            </ul>
          </div>
          <!-- QR Code end -->
		</div>
	  </div>
	</div>

	<div class="reveal" style="position: absolute; top: 0px">

	  <!-- Any section element inside of this container is displayed as a slide -->
	  <div class="slides">

		<!-- SLIDES HERE -->
		<!-- <section><img src="#" class="ppt-slide-img" /></section> -->

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
	<script src="/static/idocv/js/progress.js"></script>
	<script src="/static/urlparser/js/purl.js"></script>
	<script src="/static/qrcode/js/jquery.qrcode.min.js"></script>
	<script src="/static/formvalidator/js/jquery.formvalidator.min.js"></script>
	<!-- <script src="/static/idocv/js/user.js"></script> -->
	<script src="/static/idocv/js/ppt.js"></script>
	<script src="/static/idocv/js/stat.js"></script>
	
	<!-- 
	<a class="fork-reveal" href="http://www.idocv.com"><img style="position: absolute; top: 41px; right: 0; border: 0; max-width: 30%;" src="http://data.idocv.com/idocv-logo-ribbon.png" alt="I Doc View"></a>
	 -->
	
	<!-- Baidu Share BEGIN -->
	<script type="text/javascript" id="bdshare_js" data="type=slide&amp;img=6&amp;pos=right&amp;uid=6693451" ></script>
	<script type="text/javascript" id="bdshell_js"></script>
	<script type="text/javascript">
	document.getElementById("bdshell_js").src = "http://bdimg.share.baidu.com/static/js/shell_v2.js?cdnversion=" + Math.ceil(new Date()/3600000);
	</script>
	<!-- Baidu Share END -->

  </body>
</html>
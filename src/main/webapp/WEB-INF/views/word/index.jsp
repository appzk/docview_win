<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Word - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Online Document Preview, synchronous preview and annotating. types including Microsoft Office types(.doc, .docx, .xls, .xlsx, ppt, pptx), txt, pdf, odt, ods, odp and more.">
    <meta name="keywords" content="Online Document Preview doc view viewer office word excel 在线 文档 同步 协作 预览" />
    <meta name="author" content="Godwin">

    <!-- Le styles -->
    <link href="/static/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="/static/idocv/css/style.css" rel="stylesheet">
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="/static/bootstrap/js/html5shiv.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <!-- to be done -->
  </head>

  <body class="word-body">

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <!-- FILE NAME HERE -->
          <!-- SIGN UP & SIGN IN -->
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

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span12">
       		<!-- WORD PAGES HERE -->
        	<!-- <div class="word-page"><div class="word-content">WORD CENTENT HERE</div></div> -->
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
			Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>

    </div><!--/.fluid-container-->

    <!-- Le javascript
    ================================================== -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
	<script>window.jQuery || document.write('<script src="/static/jquery/js/jquery-1.9.1.min.js"><\/script>')</script>
	<script src="/static/bootstrap/js/bootstrap.min.js"></script>
	<script src="/static/jquerycookie/js/jquery.cookie.js"></script>
	<script src="/static/formvalidator/js/jquery.formvalidator.min.js"></script>
	<script src="/static/idocv/js/user.js"></script>
	<script src="/static/loading/js/simpleLoadingModal.min.js"></script>
	<script src="/static/urlparser/js/purl.js"></script>
	<script src="/static/qrcode/js/jquery.qrcode.min.js"></script>
	<script src="/static/idocv/js/word.js"></script>
	<script src="/static/smart/js/jquery.easing-1.3.min.js"></script>
	<script src="/static/idocv/js/custom.js"></script>
	<script src="/static/idocv/js/stat.js"></script>
	
	<!-- Baidu Share BEGIN -->
	<script type="text/javascript" id="bdshare_js" data="type=slide&amp;img=6&amp;pos=right&amp;uid=6693451" ></script>
	<script type="text/javascript" id="bdshell_js"></script>
	<script type="text/javascript">
	document.getElementById("bdshell_js").src = "http://bdimg.share.baidu.com/static/js/shell_v2.js?cdnversion=" + Math.ceil(new Date()/3600000);
	</script>
	<!-- Baidu Share END -->

  </body>
</html>
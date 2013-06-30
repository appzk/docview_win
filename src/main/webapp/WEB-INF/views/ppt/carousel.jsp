<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>PPT - I Doc View</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="author" content="Godwin">

	<meta name="apple-mobile-web-app-capable" content="yes" />
	<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" />

	<!-- BOOTSTRAP STYLE start -->
	<!-- Le styles -->
	<link href="/static/bootstrap/css/bootstrap.css" rel="stylesheet">
	<link href="/static/idocv/css/style.css" rel="stylesheet">
	<link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
	<!-- BOOTSTRAP STYLE end -->

    <style type="text/css">
      .thumbnail{
        max-width: 200px;
        cursor: pointer;
      }
    </style>
    
    <!--[if lt IE 9]>
      <script src="/static/bootstrap/js/html5shiv.js"></script>
    <![endif]-->

  </head>

  <body onload="resetImgSize();" class="ppt-body">

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
          <!-- 
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
           -->
          <!-- QR Code end -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div id="myCarousel" class="carousel slide">
        <ol class="carousel-indicators">
          <!-- INDICATORS HERE -->
          <!-- 
          <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
          <li data-target="#myCarousel" data-slide-to="1"></li>
          <li data-target="#myCarousel" data-slide-to="2"></li>
           -->
        </ol>
        <!-- Carousel items -->
        <div class="carousel-inner">
          <!-- SLIDE IMAGES HERE -->
          <!-- 
          <div class="active item"><img src="http://data.idocv.com/test/2013/0628/112157_667145_nHGQZdp/960x720/slide1.jpg" alt="" style="margin: 0 auto;"></div>
          <div class="item"><img src="http://data.idocv.com/test/2013/0628/112157_667145_nHGQZdp/960x720/slide1.jpg" alt="" style="margin: 0 auto;"></div>
          <div class="item"><img src="http://data.idocv.com/test/2013/0628/112157_667145_nHGQZdp/960x720/slide1.jpg" alt="" style="margin: 0 auto;"></div>
           -->
        </div>
        <!-- Carousel nav -->
        <a class="carousel-control left hidden-phone" href="#myCarousel" data-slide="prev">&lsaquo;</a>
        <a class="carousel-control right hidden-phone" href="#myCarousel" data-slide="next">&rsaquo;</a>
      </div>
    </div>
    
    <div class="progress progress-striped active bottom-paging-progress">
      <div class="bar" style="width: 0%;"></div>
    </div>

    <!-- Le javascript
    ================================================== -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
	<script>window.jQuery || document.write('<script src="/static/jquery/js/jquery-1.10.1.min.js"><\/script>')</script>
	<script src="/static/bootstrap/js/bootstrap.min.js"></script>
	<script src="/static/jquery/js/jquery.mobile-events.min.js"></script>
	<script src="/static/idocv/js/progress.js"></script>
	<script src="/static/urlparser/js/purl.js"></script>
	<script src="/static/idocv/js/ppt-carousel.js"></script>
	<script src="/static/idocv/js/stat.js"></script>

  </body>
</html>
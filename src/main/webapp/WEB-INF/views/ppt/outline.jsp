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

    <div class="container-fluid" style="max-height: 100%;">
      <div class="row-fluid">
        <div class="span2 hidden-phone" style="position: fixed; top: 60px; left: 20px; bottom: 20px; padding-right: 5px; border-right: 3px solid #c8c8c8; max-height: 100%; overflow: auto; text-align: center;">
          <!--Sidebar content-->
          <!-- 
          <div class="thumbnail">
            <img src="">
          </div>
          1/20<br />
          -->
        </div>
        <div class="span9 offset2">
          <div class="slider-img" style="bottom: 20px; top: 0px; right: 5px; position: relative; width: 100%; text-align: center;">
            <!-- 
            <img src="" class="img-polaroid" style="max-height: 100%;">
             -->
          </div>
          <!-- ONLY AVAILABLE ON MOBILE -->
          <div id="btns" class="span12 visible-phone text-center" style="position: fixed; bottom: 10px; left: 0px; z-index: 100;">
            <select id="page-selector" class="span1" style="width: 80px; margin-top: 10px;">
              <!-- PAGE NUMBERS HERE -->
            </select>
          </div>
        </div>
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
	<script src="/static/idocv/js/ppt-outline.js"></script>
	<script src="/static/smart/js/jquery.easing-1.3.min.js"></script>
	<script src="/static/idocv/js/stat.js"></script>
	
	<!-- Baidu Share BEGIN -->
	<!-- 
	<script type="text/javascript" id="bdshare_js" data="type=slide&amp;img=6&amp;pos=right&amp;uid=6693451" ></script>
	<script type="text/javascript" id="bdshell_js"></script>
	<script type="text/javascript">
	document.getElementById("bdshell_js").src = "http://bdimg.share.baidu.com/static/js/shell_v2.js?cdnversion=" + Math.ceil(new Date()/3600000);
	</script>
	 -->
	<!-- Baidu Share END -->

  </body>
</html>
<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>PPT - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2013">
    <meta name="author" content="godwin668@gmail.com">

    <!-- BOOTSTRAP STYLE start -->
    <!-- Le styles -->
    <link href="/static/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="/static/idocv/css/style.css" rel="stylesheet">
    <link href="/static/contextMenu/css/jquery.contextMenu.css" rel="stylesheet">
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
          
          <div class="nav-collapse collapse">
            <p class="navbar-text pull-right">
              <a href="#" title="全屏" class="fullscreen-link"><i class="icon-fullscreen icon-white"></i></a>
            </p>
          </div><!--/.nav-collapse -->
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
          <div class="slide-img">
            <div class="ppt-turn-left-mask"></div>
            <div class="ppt-turn-right-mask"></div>
            <!-- 
            <img src="" class="img-polaroid" style="max-height: 100%;">
             -->
          </div>
          <!-- ONLY AVAILABLE ON MOBILE -->
          <div id="btns" class="span12 visible-phone text-center" style="position: fixed; bottom: 10px; left: 0px; z-index: 1000;">
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

    <!-- JavaSript
    ================================================== -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="/static/jquery/js/jquery-1.10.1.min.js"><\/script>')</script>
    <script src="/static/contextMenu/js/jquery.contextMenu.js"></script>
    <script src="/static/contextMenu/js/jquery.ui.position.js"></script>
    <script src="/static/bootstrap/js/bootstrap.min.js"></script>
    <script src="/static/jquery/js/jquery.mobile-events.min.js"></script>
    <script src="/static/idocv/js/progress.js"></script>
    <script src="/static/urlparser/js/purl.js"></script>
    <script src="/static/fullscreen/js/jquery.fullscreen-min.js"></script>
    <script src="/static/idocv/js/ppt-outline.js"></script>
    <script src="/static/idocv/js/stat.js"></script>
  </body>
</html>
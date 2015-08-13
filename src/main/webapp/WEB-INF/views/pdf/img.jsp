<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>PDF - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2014">
    <meta name="author" content="godwin668@gmail.com">

    <!-- styles -->
    <link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <link href="/static/idocv/css/style.css?v=${version}" rel="stylesheet" />
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet" />
    
    <script type="text/javascript">
      var windowWidth = document.documentElement.clientWidth;
      var searchStr = window.location.search;
      if (windowWidth < 768 && (!searchStr || !searchStr.contains('type'))) {
    	  window.location.href = 'http://' + window.location.host + window.location.pathname + '?type=mobile';
      }
    </script>

    <!--[if lt IE 9]>
      <script src="/static/bootstrap/js/html5shiv.js"></script>
    <![endif]-->
  </head>

  <body class="pdf-body">
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
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span12">
          <!-- PDF PAGES HERE -->
          <!-- <div class="word-page"><div class="word-content">WORD CENTENT HERE</div></div> -->
          <!-- 
          <div class="pdf-page">
            <div class="pdf-content">
              <!-- WORD CENTENT HERE
            </div>
          </div>
           -->
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
        Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>
      
      <div class="progress progress-striped active bottom-paging-progress">
        <div class="bar" style="width: 0%;"></div>
      </div>
      <div class="paging-bottom-all" style="z-index: 2000">
        <!-- SUB PAGING DIV(s) HERE -->
        <!-- 
        <div class="paging-bottom-sub" page-num="1" style="width: 20%;">1</div>
        ...
         -->
      </div>

    </div><!--/.fluid-container-->
    
    <div class="btn-cmd-container text-center" style="position: fixed; bottom: 10px; left: 0px; right: 0px; text-align: center; z-index: 1000;">
      <a cmd-string="out" class="btn btn-primary btn-cmd btn-cmd-zoom-out" style="display: inline-block;" href="#" title="上一页"><i class="icon-zoom-out icon-white"></i></a>
      &nbsp;
      <a cmd-string="left" class="btn btn-primary btn-cmd btn-cmd-previous" style="display: inline-block;" href="#" title="上一页">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-chevron-left icon-white"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
      &nbsp;
      <select class="select-page-selector span1" style="width: 62px; margin-top: 10px; display: inline-block;">
        <!-- PAGE NUMBERS HERE -->
      </select>
      &nbsp;
      <a cmd-string="right" class="btn btn-primary btn-cmd btn-cmd-next" style="display: inline-block;" href="#" title="下一页">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="icon-chevron-right icon-white"></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
      &nbsp;
      <a cmd-string="in" class="btn btn-primary btn-cmd btn-cmd-zoom-in" style="display: inline-block;" href="#" title="上一页"><i class="icon-zoom-in icon-white"></i></a>
    </div>
    
    <!-- JavaSript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.1.min.js?v=${version}"></script>
    <script src="/static/bootstrap/js/bootstrap.min.js?v=${version}"></script>
    <script src="/static/idocv/js/custom.js?v=${version}"></script>
    <script src="/static/jquerycookie/js/jquery.cookie.js?v=${version}"></script>
    <script src="/static/idocv/js/progress.js?v=${version}"></script>
    <script src="/static/scrollspy/js/jquery-scrollspy.js?v=${version}"></script>
    <script src="/static/urlparser/js/purl.js?v=${version}"></script>
    <script src="/static/formvalidator/js/jquery.formvalidator.min.js?v=${version}"></script>
    <script src="/static/infinite-scroll/js/jquery.infinitescroll.js?v=${version}"></script>
    <script src="/static/idocv/js/pdf-img.js?v=${version}"></script>
    <script src="/static/smart/js/jquery.easing-1.3.min.js?v=${version}"></script>
    <script src="/static/idocv/js/stat.js?v=${version}"></script>
  </body>
</html>
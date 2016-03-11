<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Word - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2014">
    <meta name="author" content="godwin668@gmail.com">

    <!-- styles -->
    <link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/idocv/css/style.css?v=${version}" rel="stylesheet">
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">

    <!--[if lt IE 9]>
      <script src="/static/bootstrap/js/html5shiv.js"></script>
    <![endif]-->
  </head>

  <body class="word-body">
    <div class="loading-mask" style="display: block;">
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
          <div class="nav-collapse collapse">
            <ul class="nav word-tab-title">
              <!-- WORD TAB TITLE(s) HERE -->
            </ul>
          </div><!--/.nav-collapse -->
          <!-- SIGN UP & SIGN IN -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span12">
          <!-- WORD PAGES HERE -->
          <!-- <div class="word-page"><div class="word-content">WORD CONTENT HERE</div></div> -->
          <div class="word-page">
            <div class="word-content">
              <!-- WORD CENTENT HERE -->
            </div>
          </div>
        </div><!--/span-->
      </div><!--/row-->
      
      <!-- SAVE BUTTON HERE -->
      <div class="save-btn-container" style="position: fixed; text-align: center; bottom: 20px; left: 0px; right: 0px; display: none;">
        <button class="btn btn-large btn-success btn-save" type="button">&nbsp;&nbsp;保&nbsp;存&nbsp;&nbsp;</button>
      </div>

      <hr>

      <footer>
        Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>
      
      <div class="progress progress-striped active bottom-paging-progress">
        <div class="bar" style="width: 0%;"></div>
      </div>
      <div class="paging-bottom-all">
        <!-- SUB PAGING DIV(s) HERE -->
        <!-- 
        <div class="paging-bottom-sub" page-num="1" style="width: 20%;">1</div>
        ...
         -->
      </div>

    </div><!--/.fluid-container-->
    
    <!-- 
    <a href="http://www.idocv.com"><img style="position: fixed; top: 80px; right: 20px; max-width: 15%" alt="在线文档预览双十一特惠" src="http://data.idocv.com/promotion_20131111.jpg"></a>
     -->

    <!-- JavaSript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.1.min.js?v=${version}"></script>
    <script src="/static/bootstrap/js/bootstrap.min.js?v=${version}"></script>
    <script src="/static/idocv/js/progress.js?v=${version}"></script>
    <script src="/static/jquerycookie/js/jquery.cookie.js?v=${version}"></script>
    <script src="/static/idocv/js/custom.js?v=${version}"></script>
    <script src="/static/urlparser/js/purl.js?v=${version}"></script>
    <script src="/static/ckeditor/ckeditor.js?v=${version}"></script>
    <script src="/static/ckeditor/adapters/jquery.js?v=${version}"></script>
    <script src="/static/ckeditor/plugins/lite/lite-interface.js?v=${version}"></script>
    <script src="/static/idocv/js/word-edit.js?v=${version}"></script>
    <script src="/static/idocv/js/stat.js?v=${version}"></script>
  </body>
</html>
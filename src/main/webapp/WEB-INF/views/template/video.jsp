<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Image - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2013">
    <meta name="author" content="godwin668@gmail.com">

    <!-- styles -->
    <link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/idocv/css/style.css" rel="stylesheet">
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">

    <!--[if lt IE 9]>
      <script src="/static/bootstrap/js/html5shiv.js"></script>
    <![endif]-->
  </head>

  <body>
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
          <!-- CONTENT HERE -->
          <h1>File Information - Video Template</h1>
          <h3>url: ${url}</h3>
          <h3>app: ${app}</h3>
          <h3>uid: ${uid}</h3>
          <h3>uuid: ${uuid}</h3>
          <h3>ext: ${ext}</h3>
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
        Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>
    </div><!--/.fluid-container-->

    <!-- JavaSript
    ================================================== -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="/static/jquery/js/jquery-1.10.1.min.js"><\/script>')</script>
    <script src="/static/bootstrap/js/bootstrap.min.js"></script>
    <script src="/static/jquerycookie/js/jquery.cookie.js"></script>
    <script src="/static/urlparser/js/purl.js"></script>
    <script src="/static/idocv/js/stat.js"></script>
  </body>
</html>
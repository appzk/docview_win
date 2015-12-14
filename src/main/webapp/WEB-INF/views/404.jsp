<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>404 - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2014">
    <meta name="author" content="godwin668@gmail.com">

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

  <body>

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span12">
          <div class="alert alert-error">
                                预览失败：${error}&nbsp;&nbsp;&nbsp;<span id='container'></span> 秒后返回主页！
          </div>
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
			Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>

    </div><!--/.fluid-container-->

    <!-- javascript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.1.min.js?v=${version}"></script>
	<script src="/static/bootstrap/js/bootstrap.min.js?v=${version}"></script>
	<script>
		var time = 10; //How long (in seconds) to countdown
		var page = "http://www.idocv.com"; //The page to redirect to
		function countDown(){
			time--;
			gett("container").innerHTML = time;
			if(time == 0){
				window.location = page;
			}
		}
		function gett(id){
			if(document.getElementById) return document.getElementById(id);
			if(document.all) return document.all.id;
			if(document.layers) return document.layers.id;
			if(window.opera) return window.opera.id;
		}
		function init(){
			if(gett('container')){
				setInterval(countDown, 1000);
				gett("container").innerHTML = time;
			} else {
				setTimeout(init, 50);
			}
		}
		document.onload = init();
	</SCRIPT>
	<script src="/static/idocv/js/stat.js"></script>
  </body>
</html>
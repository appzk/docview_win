<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>System Info - I Doc View</title>
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

  <body>
    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="brand" href="http://www.idocv.com">I Doc View</a>
          <!-- FILE NAME HERE -->
          <!-- SIGN UP & SIGN IN -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span2">
          <div class="well sidebar-nav">
            <ul class="nav nav-list">
              <li class="nav-header">系统</li>
              <li class="active"><a href="/system/info">基本信息</a></li>
              <li><a href="/system/memory">内存信息</a></li>
              <li class="divider"></li>
              <li class="nav-header">文档</li>
              <li><a class="nav-doc-list" href="#">文档管理</a></li>
            </ul>
          </div><!--/.well -->
        </div><!--/span-->
        <div class="span10">
          <div class="row-fluid">
            <div class="span12">
              <div class="hero-unit">
                >操作系统：<div class="system-info-os"></div>
                >CPU核数：<div class="system-info-cpu-cuont"></div>
                >转换队列文档数：<div class="system-info-queue-size"></div>
                >当前机器负载：<div class="system-info-high-load"></div>
                >系统内存总大小：<div class="system-info-os-mem-total"></div>
                >系统内存已用：<div class="system-info-os-mem-used"></div>
                >系统内存空闲：<div class="system-info-os-mem-free"></div>
                >内存初始大小（堆内存）：<div class="system-info-mem-init"></div>
                >已使用内存（堆内存）：<div class="system-info-mem-used"></div>
                >最大分配内存（堆内存）：<div class="system-info-mem-max"></div>
                >内存使用率（堆内存）：<div class="system-info-mem-rate"></div>
                >最近5分钟内每分钟平均上次文件数：<div class="system-info-upload-avg"></div>
              </div>
            </div><!--/span-->
          </div><!--/row-->
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
        Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>
      
    </div><!--/.fluid-container-->

    <!-- JavaSript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.1.min.js?v=${version}"></script>
    <script src="/static/bootstrap/js/bootstrap.js?v=${version}"></script>
	<script src="/static/jquerycookie/js/jquery.cookie.js?v=${version}"></script>
	<script src="/static/urlparser/js/purl.js?v=${version}"></script>
	<script src="/static/formvalidator/js/jquery.formvalidator.min.js?v=${version}"></script>
	<script src="/static/idocv/js/user.js?v=${version}"></script>
	<script src="/static/idocv/js/system-info.js?v=${version}"></script>
  </body>
</html>
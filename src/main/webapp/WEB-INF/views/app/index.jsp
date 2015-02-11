<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Bootstrap, from Twitter</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2014">
    <meta name="author" content="godwin668@gmail.com">

    <!-- Le styles -->
    <link href="/static/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="/static/datatable/css/DT_bootstrap.css" rel="stylesheet">
    <link href="/static/idocv/css/style.css?v=${version}" rel="stylesheet">
    <link href="/static/bootstrap/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="/static/bootstrap/js/html5shiv.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
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
          <div class="nav-collapse collapse">
            <ul class="nav">
              <li class="active"><a href="#"><i class="icon-home icon-white"></i> Home</a></li>
              <li><a href="#about">About</a></li>
              <li><a href="#contact">Contact</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row">
      	<div id="upload-result" class="span12"></div>
      	<div class="span12">
          <span class="button large fileinput-button">
			<span><button class="btn btn-large btn-success" type="button">Upload a Document</button></span>
			<input id="fileupload" type="file" name="file" data-url="/doc/upload" multiple>
		  </span>
      	</div>
      	<br /><br />
        <div class="span12">
          <!-- CONTENT HERE -->
          <div class="container" style="margin-top: 15px; overflow: auto;">
			<table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered responsive" id="doctable">
				<thead>
					<tr>
						<th width="30%" style="text-align: center;">Name</th>
						<th width="15%" style="text-align: center;">Time</th>
						<th width="10%" style="text-align: center;">Size</th>
						<th width="15%" style="text-align: center;">Share URL</th>
						<th width="10%" style="text-align: center;">Count(v/d)</th>
						<th width="20%" style="text-align: center;">Option</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="5" class="dataTables_empty">Loading data from server...</td>
					</tr>
				</tbody>
			</table>
		  </div>
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
			Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>

    </div><!--/.fluid-container-->

    <!-- Le javascript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.1.min.js?v=${version}"></script>
    <script src="/static/bootstrap/js/bootstrap.js?v=${version}"></script>
	<script src="/static/jquerycookie/js/jquery.cookie.js?v=${version}"></script>
	<script src="/static/formvalidator/js/jquery.formvalidator.min.js?v=${version}"></script>
	<script src="/static/idocv/js/user.js?v=${version}"></script>
	<script src="/static/datatable/js/jquery.dataTables.js?v=${version}"></script>
	<script src="/static/datatable/js/DT_bootstrap.js?v=${version}"></script>
	<script src="/static/js/upload/jquery.ui.widget.js?v=${version}"></script>
	<script src="/static/js/upload/jquery.iframe-transport.js?v=${version}"></script>
	<script src="/static/js/upload/jquery.fileupload.js?v=${version}"></script>
	<script src="/static/user/doclist.js?v=${version}"></script>

  </body>
</html>
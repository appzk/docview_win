<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Excel - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2014">
    <meta name="author" content="godwin668@gmail.com">

    <!-- Le styles -->
    <link href="/static/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="/static/idocv/css/style.css" rel="stylesheet">
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
    <link href="${page.styleUrl}" rel="stylesheet"/>

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
          <!-- FILE NAME HERE -->
          <a class="brand" style="text-decoration: none;" href="/doc/download/${page.uuid}">${page.name}</a>
          <div class="nav-collapse collapse">
            <ul class="nav">
              <!-- EXCEL TAB TITLE(s) HERE -->
              <c:forEach var="pg" items="${page.data}" varStatus="rowCounter">
          		<c:choose>
					<c:when test="${rowCounter.index == 0}">
						<li class="active"><a href="#tab${rowCounter.index + 1}" data-toggle="tab">${pg.title}</a></li>
					</c:when>
					<c:otherwise>
						<li><a href="#tab${rowCounter.index + 1}" data-toggle="tab">${pg.title}</a></li>
					</c:otherwise>
				</c:choose>
        	  </c:forEach>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span12" style="width: auto; padding-right: 20px">
          <!-- Excel tab content start -->
          <div class="tab-content">
          	<!-- EXCEL CONENT HERE -->
          	<c:forEach var="pg" items="${page.data}" varStatus="rowCounter">
          		<c:choose>
					<c:when test="${rowCounter.index == 0}">
						<div class="tab-pane fade in active" id="tab${rowCounter.index + 1}">${pg.content}</div>
					</c:when>
					<c:otherwise>
						<div class="tab-pane fade in" id="tab${rowCounter.index + 1}">${pg.content}</div>
					</c:otherwise>
				</c:choose>
        	</c:forEach>
          </div><!-- Excel tab content end -->
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
			Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>

    </div><!--/.fluid-container-->

    <!-- Le javascript
    ================================================== -->
	<script src="/static/jquery/js/jquery-1.10.1.min.js"></script>
    <script src="/static/bootstrap/js/bootstrap.min.js"></script>
	<script src="/static/jquerycookie/js/jquery.cookie.js"></script>
	<script src="/static/formvalidator/js/jquery.formvalidator.min.js"></script>
	<!-- <script src="/static/idocv/js/user.js"></script> -->
	<script src="/static/loading/js/simpleLoadingModal.min.js"></script>
	<script src="/static/urlparser/js/purl.js"></script>
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
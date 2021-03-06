<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
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
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <!-- 
        <div class="span2">
          <div class="well sidebar-nav">
            <ul class="nav nav-list">
              <!-- SIDEBAR LIST HERE -->
              <!-- 
              <li class="nav-header"><i class="icon-user"></i> 我的文档</li>
              <li class="active"><a href="/doc/list/all">全部</a></li>
              <li><a href="/doc/list/personal">个人</a></li>
              <li><a href="/doc/list/work">工作</a></li>
              <li><a href="#">+</a></li>
              <li class="divider"></li>
              <li class="nav-header"><i class="icon-share-alt"></i> 其他文档</li>
              <li><a href="#">好友分享</a></li>
              <li><a href="#">推荐文档</a></li>
            </ul>
          </div>
        </div>
         -->
        <div class="span12">
          <div class="row-fluid">
            <div class="span12">
              <!-- CONTENT HERE -->
              <div class="container span12" style="margin-top: 15px; overflow: auto;">
                <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered responsive" id="doctable">
                  <thead>
                    <tr>
                      <th width="60%" style="text-align: center;">名称</th>
                      <th width="20%" style="text-align: center;">时间</th>
                      <th width="20%" style="text-align: center;">大小</th>
                    </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td colspan="6" class="dataTables_empty">正在加载数据...</td>
                      </tr>
                    </tbody>
                </table>
              </div>
            </div><!--/span-->
          </div><!--/row-->
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
            &copy; <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a> 2013
      </footer>

    </div><!--/.fluid-container-->

    <!-- Le javascript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.1.min.js?v=${version}"></script>
    <script src="/static/bootstrap/js/bootstrap.js?v=${version}"></script>
    <script src="/static/urlparser/js/purl.js?v=${version}"></script>
    <script src="/static/jquerycookie/js/jquery.cookie.js?v=${version}"></script>
    <script src="/static/formvalidator/js/jquery.formvalidator.min.js?v=${version}"></script>
    <script src="/static/datatable/js/jquery.dataTables.js?v=${version}"></script>
    <script src="/static/datatable/js/DT_bootstrap.js?v=${version}"></script>
    <script src="/static/js/upload/jquery.ui.widget.js?v=${version}"></script>
    <script src="/static/js/upload/jquery.iframe-transport.js?v=${version}"></script>
    <script src="/static/js/upload/jquery.fileupload.js?v=${version}"></script>
    <script src="/static/idocv/js/doc-list-share.js?v=${version}"></script>
  </body>
</html>
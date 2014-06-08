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
    <link href="/static/idocv/css/style.css" rel="stylesheet">
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
              <li><a href="http://bbs.idocv.com">社区</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
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
               -->
              <li><a href="/system/info">系统信息</a></li>
              <li class="divider"></li>
            </ul>
          </div><!--/.well -->
        </div><!--/span-->
        <div class="span10">
          <div class="row-fluid">
            <div class="span12">
              <span class="button large fileinput-button">
                <span><button class="btn btn-large btn-success" type="button">&nbsp;&nbsp;&nbsp;上传一个文档&nbsp;&nbsp;&nbsp;</button></span>
                <input id="fileupload" type="file" name="file" data-url="/doc/upload" multiple>
              </span>
              <div id="upload-result" style="display: inline-block; margin: 5px 10px;">
              </div>
            </div><!--/span-->
          </div><!--/row-->
          <div class="row-fluid upload-progress" style="display: none;">
            <div class="span12">
              <div class="progress progress-striped active" style="margin: 10px 0px 0px;">
                <div class="bar" style="width: 0%;"></div>
              </div>
            </div><!--/span-->
          </div><!--/row-->
          <div class="row-fluid">
            <div class="span12">
              <!-- CONTENT HERE -->
              <div class="container span12" style="margin-top: 15px; overflow: auto;">
                <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered responsive" id="doctable">
                  <thead>
                    <tr>
                      <th width="40%" style="text-align: center;">名称</th>
                      <th width="15%" style="text-align: center;">时间</th>
                      <th width="10%" style="text-align: center;">大小</th>
                      <th width="10%" style="text-align: center;">UUID</th>
                      <th width="10%" style="text-align: center;">浏览/下载</th>
                      <th width="15%" style="text-align: center;">操作</th>
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
    <script src="/static/jquery/js/jquery-1.11.1.min.js"></script>
    <script src="/static/bootstrap/js/bootstrap.js"></script>
    <script src="/static/urlparser/js/purl.js"></script>
    <script src="/static/jquerycookie/js/jquery.cookie.js"></script>
    <script src="/static/formvalidator/js/jquery.formvalidator.min.js"></script>
    <script src="/static/idocv/js/user.js"></script>
    <script src="/static/datatable/js/jquery.dataTables.js"></script>
    <script src="/static/datatable/js/DT_bootstrap.js"></script>
    <script src="/static/js/upload/jquery.ui.widget.js"></script>
    <script src="/static/js/upload/jquery.iframe-transport.js"></script>
    <script src="/static/js/upload/jquery.fileupload.js"></script>
    <script src="/static/idocv/js/doc-list-user.js"></script>
  </body>
</html>
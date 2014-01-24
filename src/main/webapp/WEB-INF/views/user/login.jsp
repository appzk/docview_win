<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HOME - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Online Document Preview, synchronous preview and annotating. types including Microsoft Office types(.doc, .docx, .xls, .xlsx, ppt, pptx), txt, pdf, odt, ods, odp and more.">
    <meta name="keywords" content="Online Document Preview doc view viewer office word excel 在线 文档 同步 协作 预览" />
    <meta name="author" content="Godwin">

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
          <a class="brand" href="http://www.idocv.com">I Doc View</a>
          <!-- SIGN UP & SIGN IN HERE -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span2">

        </div><!--/span-->
        <div class="span8">
          <div class="row-fluid">
            <div class="span8">
              <div class="hero-unit">
                <h1>在线文档预览</h1>
                <p>在线文档预览、协作编辑，幻灯片远程控制、同步演示、画笔同步展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf和txt等。</p>
                <p><a href="http://www.idocv.com" class="btn btn-primary btn-large">了解详情 &raquo;</a></p>
              </div>
            </div><!--/span-->
            <div class="span4">
              <div class="well">
                <ul class="nav nav-tabs">
                  <li class="active"><a href="#login" data-toggle="tab">登录</a></li>
                </ul>
                <div id="myTabContent" class="tab-content">
                  <div class="tab-pane active in" id="login">
                    <form id="form-signin" class="form-horizontal" action="" method="post" accept-charset="UTF-8">
                      <div id="sign-in-result"></div>
                      <p><input id="login_username" placeholder="用户名/邮箱" type="text" class="span12" name="login_username" size="30" data-validation="validate_min_length length3" /></p>
                      <p><input id="login_password" placeholder="密码" type="password" class="span12" name="login_password" size="30" data-validation="validate_min_length length4" /></p>
                      <p><input id="login_rememberme" style="float: left; margin-right: 10px;" type="checkbox" name="login_rememberme" value="1" /></p>
                      <p><label class="string optional" for="user_remember_me">记住我</label></p>
                      <p><input class="btn btn-primary" style="clear: left; width: 100%; height: 32px; font-size: 13px;" type="submit" name="commit" value="登录" /></p>
                      <p><span id="qqLoginBtn"></span></p>
                      <p>没有账号？点击<a href="/signup">注册</a></p>
                    </form>
                  </div>
                </div>
              </div><!--/well-->
            </div><!--/span-->
          </div><!--/row-->
        </div><!--/span-->
        <div class="span2">

        </div><!--/span-->

      </div><!--/row-->

      <hr>

      <footer>
			Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>

    </div><!--/.fluid-container-->

    <!-- Le javascript
    ================================================== -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
    <script>window.jQuery || document.write('<script src="/static/jquery/js/jquery-1.10.1.min.js"><\/script>')</script>
    <script src="/static/bootstrap/js/bootstrap.js"></script>
    <script src="/static/urlparser/js/purl.js"></script>
    <script src="/static/jquerycookie/js/jquery.cookie.js"></script>
    <script src="/static/formvalidator/js/jquery.formvalidator.min.js"></script>
    <script src="/static/idocv/js/user-login.js"></script>
    <!-- 
    <script src="http://qzonestyle.gtimg.cn/qzone/openapi/qc_loader.js" data-appid="100444886" data-redirecturi="http://www.idocv.com" charset="utf-8"></script>
    <script type="text/javascript">
        QC.Login({
            btnId:"qqLoginBtn",	
            size: "A_M"
        }, function(reqData, opts){//登录成功
            //根据返回数据，更换按钮显示状态方法
            alert("Login success...");
            var dom = document.getElementById(opts['btnId']),
            _logoutTemplate=[
                 //头像
                 '<span><img src="{figureurl}" class="{size_key}"/></span>',
                 //昵称
                 '<span>{nickname}</span>',
                 //退出
                 '<span><a href="javascript:QC.Login.signOut();">退出</a></span>'	
                          ].join("");
                dom && (dom.innerHTML = QC.String.format(_logoutTemplate, {
                nickname : QC.String.escHTML(reqData.nickname),
                figureurl : reqData.figureurl
            }));
        }, function(opts){//注销成功
            alert('QQ登录 注销成功');
        });
    </script>
     -->

  </body>
</html>
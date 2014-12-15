<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="/favicon.ico">

    <title>Dir - I Doc View</title>

    <!-- Bootstrap core CSS -->
    <link href="/static/bootstrap3/css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
      body {
        padding-top: 50px;
      }
      .starter-template {
        padding: 40px 15px;
/*         text-align: center; */
      }
      .cmd-result pre {
        height: 400px;
        overflow-y: scroll;
      }
    </style>

  </head>

  <body>

    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="http://www.idocv.com">I Doc View</a>
        </div>
        <div class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="/">Home</a></li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>

    <div class="container">
      <div class="row starter-template">
        <div class="col-lg-12">
          <div class="input-group">
            <input type="text" class="form-control">
            <span class="input-group-btn">
              <button class="btn btn-default btn-cmd-commit" type="button">Go!</button>
            </span>
          </div><!-- /input-group -->
        </div><!-- /.col-lg-6 -->
      </div><!-- /.row -->
      
      <div class="row cmd-result">
        <h3>Result:</h3>
        <pre>
        </pre>
      </div>
    </div><!-- /.container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.1.min.js"></script>
    <script src="/static/bootstrap3/js/bootstrap.js"></script>
    <script src="/static/urlparser/js/purl.js"></script>
    <script src="/static/formvalidator/js/jquery.formvalidator.min.js"></script>
	<script src="/static/idocv/js/user.js"></script>
	<script src="/static/idocv/js/dir-load.js"></script>
  </body>
</html>
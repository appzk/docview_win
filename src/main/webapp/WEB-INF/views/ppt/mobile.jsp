<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="在线文档预览、在线文档编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf, txt, jpg, gif, png, bmp, tif, mp3, m4a, midi, wma, zip, rar, tar, 7z, dwg, dxf, dwf等">
    <meta name="author" content="godwin668@gmail.com">
    <link rel="icon" href="favicon.ico">

    <title>PPT - I Doc View</title>

    <!-- styles -->
    <link href="/static/bootstrap3/css/bootstrap.min.css" rel="stylesheet">
    <link href="/static/idocv/css/style.css?v=${version}" rel="stylesheet">
    <link rel='stylesheet prefetch' href='/static/photoswipe/css/photoswipe.css'>
    <link rel='stylesheet prefetch' href='/static/photoswipe/default-skin/default-skin.css'>
    <style type="text/css">
      body {
        padding-top: 70px;
      }
      .my-gallery {
        width: 100%;
        float: left;
      }
      .my-gallery img {
        width: 100%;
        height: auto;
      }
      .my-gallery figure {
        display: block;
        float: left;
        margin: 0 5px 5px 0;
        width: 200px;
      }
      .my-gallery figcaption {
        display: none;
      }
    </style>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="/static/bootstrap3/js/html5shiv.min.js"></script>
      <script src="/static/bootstrap3/js/respond.min.js"></script>
    <![endif]-->
  </head>

  <body class="ppt-body">
  
    <div class="loading-mask" style="display: none;">
      <div class="loading-zone">
        <div class="text">正在载入...0%</div>
        <div class="progress">
          <div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
          </div>
        </div>
      </div>
      <div class="brand">
        <footer>
          Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
        </footer>
      </div>
    </div>

    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container-fluid">
        <div class="navbar-header">
          <!-- 
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
           -->
          <!-- FILE NAME HERE -->
          <!-- SIGN UP & SIGN IN -->
        </div>
      </div>
    </nav>

    <div class="container-fluid">
      <!-- Example row of columns -->
      <div class="row">
        <div class="col-md-12">
          <div class="my-gallery" itemscope itemtype="http://schema.org/ImageGallery">
            <!-- SLIDE HERE
            <figure itemprop="associatedMedia" itemscope itemtype="http://schema.org/ImageObject">
              <a href="http://data.idocv.com/test/2015/0417/150951_378649_LvxnJwp/1024/slide1.jpg" itemprop="contentUrl" data-size="1024x576">
                <img src="http://data.idocv.com/test/2015/0417/150951_378649_LvxnJwp/200/slide1.jpg" itemprop="thumbnail" alt="Image description" />
              </a>
            </figure>
             -->
          </div>
          
          <!-- Root element of PhotoSwipe. Must have class pswp. -->
          <div class="pswp" tabindex="-1" role="dialog" aria-hidden="true">
            <!-- Background of PhotoSwipe. 
        It's a separate element, as animating opacity is faster than rgba(). -->
            <div class="pswp__bg"></div>
            <!-- Slides wrapper with overflow:hidden. -->
            <div class="pswp__scroll-wrap">
              <!-- Container that holds slides. PhotoSwipe keeps only 3 slides in DOM to save memory. -->
              <!-- don't modify these 3 pswp__item elements, data is added later on. -->
              <div class="pswp__container">
                <div class="pswp__item"></div>
                <div class="pswp__item"></div>
                <div class="pswp__item"></div>
              </div>
              <!-- Default (PhotoSwipeUI_Default) interface on top of sliding area. Can be changed. -->
              <div class="pswp__ui pswp__ui--hidden">
                <div class="pswp__top-bar">
                  <!--  Controls are self-explanatory. Order can be changed. -->
                  <div class="pswp__counter"></div>
                  <button class="pswp__button pswp__button--close" title="关闭 (Esc)"></button>
                  <button class="pswp__button pswp__button--fs" title="全屏"></button>
                  <button class="pswp__button pswp__button--zoom" title="缩放"></button>
                  <!-- Preloader demo http://codepen.io/dimsemenov/pen/yyBWoR -->
                  <!-- element will get class pswp__preloader--active when preloader is running -->
                  <div class="pswp__preloader">
                    <div class="pswp__preloader__icn">
                      <div class="pswp__preloader__cut">
                        <div class="pswp__preloader__donut"></div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="pswp__share-modal pswp__share-modal--hidden pswp__single-tap">
                  <div class="pswp__share-tooltip"></div>
                </div>
                <button class="pswp__button pswp__button--arrow--left" title="上一张 (左箭头)">
                </button>
                <button class="pswp__button pswp__button--arrow--right" title="下一张 (右箭头)">
                </button>
                <div class="pswp__caption">
                  <div class="pswp__caption__center"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <hr>

      <footer>
        Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>
    </div> <!-- /container -->

    <!-- JavaScript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.3.min.js"></script>
    <script src="/static/bootstrap3/js/bootstrap.min.js"></script>
    <script src="/static/idocv/js/custom.js?v=${version}"></script>
    <script src="/static/contextMenu/js/jquery.contextMenu.js?v=${version}"></script>
    <script src="/static/contextMenu/js/jquery.ui.position.js?v=${version}"></script>
    <script src="/static/jquery/js/jquery.mobile-events.min.js?v=${version}"></script>
    <script src="/static/idocv/js/progress.js?v=${version}"></script>
    <script src="/static/urlparser/js/purl.js?v=${version}"></script>
    <script src="/static/fullscreen/js/jquery.fullscreen-min.js?v=${version}"></script>
    <script src='/static/photoswipe/js/photoswipe.min.js'></script>
    <script src='/static/photoswipe/js/photoswipe-ui-default.min.js'></script>
    <script src='/static/idocv/js/ppt-mobile.js'></script>
    <script src="/static/idocv/js/stat.js?v=${version}"></script>
  </body>
</html>

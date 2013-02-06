<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title></title>
	<link rel="stylesheet" rev="stylesheet" type="text/css" media="all" href="/static/preview/css/base/doc-preview.css" />
	<script type="text/javascript" src="/static/js/jquery.js"></script>
	<script type="text/javascript" src="/static/preview/js/jScrollPane.js"></script>
	<script type="text/javascript" src="/static/preview/js/jquery.mousewheel.js"></script>
	<script type="text/javascript" src="/static/preview/js/bootstrap-tab.js"></script>
	<script src="http://cdn.sockjs.org/sockjs-0.3.min.js"></script>
	<script src="/static/stomp/js/stomp.js"></script>
	
	<style>
		#cnvs {
			border: none;
			-moz-border-radius: 4px;
			cursor: url(pencil.cur), crosshair;
			position: absolute;
			overflow: hidden;
			width: 100%;
			height: 100%;
		}
		
		#cnvs:active {
			cursor: url(pencil.cur), crosshair;
		}
		
		body {
			overflow: hidden;
		}
	</style>
	
	<script>
		function goToByScroll(id){
     		$('html,body').animate({scrollTop: $("#"+id).offset().top-70},600);
     	}
	</script>
</head>
<body>
	
	<canvas id="cnvs"></canvas>
	<script>
	
		var send;
		var draw;
		send = draw = function() {
		};

		var lines = [];

		var canvas = document.getElementById('cnvs');

		if (canvas.getContext) {
			var ctx = canvas.getContext('2d');

			var img = new Image();

			draw = function(p) {
				ctx.beginPath();
				ctx.moveTo(p.x1, p.y1 - 54);
				ctx.lineTo(p.x2, p.y2 - 54);
				ctx.stroke();
			};

			var do_resize = function() {
				canvas.width = window.innerWidth;
				canvas.height = window.innerHeight;

				ctx.strokeStyle = "#fa0";
				ctx.lineWidth = "10";
				ctx.lineCap = "round";

				$.map(lines, function(p) {
					draw(p);
				});
			};

			$(window).resize(do_resize);
			$(do_resize);

			var pos = $('#cnvs').position();
			var prev = null;
			$('#cnvs').mousedown(
					function(evt) {
						evt.preventDefault();
						evt.stopPropagation();
						$('#cnvs').bind(
										'mousemove',
										function(e) {
											var curr = {
												x : e.pageX - pos.left,
												y : e.pageY - pos.top
											};
											if (!prev) {
												prev = curr;
												return;
											}
											if (Math.sqrt(Math.pow(prev.x
													- curr.x, 2)
													+ Math.pow(prev.y - curr.y,
															2)) > 8) {
												var p = {
													x1 : prev.x,
													y1 : prev.y,
													x2 : curr.x,
													y2 : curr.y
												}
												lines.push(p);
												draw(p);
												send(JSON.stringify(p));
												prev = curr;
											}
										});
					});
			$('html').mouseup(function() {
				prev = null;
				$('#cnvs').unbind('mousemove');
			});
		} else {
			document.write("Sorry - this demo requires a browser with canvas tag support.");
		}

		// Stomp.js boilerplate
		Stomp.WebSocketClass = SockJS;

		var client = Stomp.client('http://stompq.idocv.com/stomp');

		client.debug = function() {
			if (window.console && console.log && console.log.apply) {
				console.log.apply(console, arguments);
			}
		};

		send = function(data) {
			client.send('/topic/docviewdraw', {}, data);
		};
		
		sendp = function(data) {
			client.send('/topic/docviewpage', {}, data);
		};

		var on_connect = function(x) {
			drawid = client.subscribe('/topic/docviewdraw', function(d) {
				var p = JSON.parse(d.body);
				lines.push(p);
				draw(p, true);
			});
			pageid = client.subscribe('/topic/docviewpage', function(d) {
				var p = JSON.parse(d.body);
				// *** receive page number *** //
				goToByScroll('p' + p);
			});
		};
		var on_error = function() {
			console.log('error');
		};
		client.connect('guest', 'guest', on_connect, on_error, '/');
		
		// *** send page number *** //
	    $(document).ready(function (){
	    	$('.pg').click(function() {
		    	var $this = $(this);
		    	var pid = $this.attr('name').substr(4);
		    	goToByScroll('p' + pid);
		    	sendp(pid);
	    	});
	    });
	</script>

	<div class="doc wrapClear">
		<div id="hd" class="fixed">
			<div class="header wrapClear">
				<div class="logo">
					<a href="http://www.idocv.com"><img src="/static/preview/images/base/logo.png" alt="" /></a>
				</div>
			</div>
		</div>
		<div id="bd">
			<div class="backToTop" style="cursor: pointer; display: block;" title="返回顶部">
				<img src="/static/preview/images/base/backtop.png" />
			</div>
			<div class="fileTitle">
				<a href="/doc/download?id=${page.rid}">下载</a>
				<div>
					<strong><c:out value="${page.name}"></c:out></strong>
				</div>
			</div>
			<div class="page">
				<div class="contentPPT">
					<center>
						<c:forEach items="${page.data}" var="vo" varStatus="status">
							<a id="p${status.count}">&nbsp;</a>
							<img src="${vo.url}" alt="" /><br />
							第&nbsp;<c:out value="${status.count}"></c:out>&nbsp;页<br /><br />
						</c:forEach>
					</center>
					<div style="position: fixed; top: 30%; right: 5%">
						<c:forEach items="${page.data}" var="vo" varStatus="status">
							<a href="javascript:void(0);" name="page${status.count}" class="pg">第<c:out value="${status.count}"></c:out>页</a><br />
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var uuid;
var slideUrls = new Array();
var slideThumbUrls = new Array();
var curSlide = 1;

// Draw parameters
var drawServer = 'http://draw.idocv.com:8080';
var doc = $(document);
var win = $(window);
var canvas;
var ctx;
var img;

var lines = [];									// all screen lines

// Generate an unique ID
var clientId = Math.round($.now()*Math.random());

// A flag for drawing activity
var drawing = false;

var clients = {};
var cursors = {};

// previous position
var prev = {};
// current position
var curr = {};
// percent position
var perc = {};
// points (from -> to)
var p;

var socket = io.connect(drawServer);

$(document).ready(function() {
	uuid = $.url().segment(2);
	// var uuid = $.url().segment(2);
	var sessionId = $.url().param('session');
	﻿var address = 'http://api.idocv.com/view/' + uuid;
	
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.container-fluid:first .btn:first').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			// $(".qrcode").qrcode(address);
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				slideUrls[i] = page.url;
				slideThumbUrls[i] = page.thumbUrl;
				$('.row-fluid .span2').append('<div class="thumbnail" page="' + (i + 1) + '"><img src="' + page.thumbUrl + '"></div>' + (i + 1) + '/' + pages.length + '<br />');
				$('#page-selector').append('<option>' + (i + 1) + '</option>');
			}
			
			$('.thumbnail').click(function () {
				var page_num = $(this).attr('page');
				gotoSlide(page_num);
				/*
				$('.slide-img img').fadeOut().attr("src", slideUrls[page_num - 1]).fadeIn();
				var percent = Math.ceil((page_num / slideUrls.length) * 100);
				$('.bottom-paging-progress .bar').width('' + percent + '%');
				curSlide = page_num;
				*/
			});
			
			$('.slide-img').html(
					'<img id="slide-img-1" src="' + pages[0].url + '" class="img-polaroid" style="height: 100%;">' + 
					'<canvas id="slide-canvas-1" width="960" height="720" style="border: 1px solid orange; position: absolute;">Your browser does NOT support canvas!</canvas>'
				);
			
			// Initialize draw.
			initDraw();
			
			afterLoad();
		} else {
			$('.container-fluid .row-fluid').html('<section><div class="alert alert-error">' + data.desc + '</div></section>');
		}
		
		clearProgress();
	});
	
	$('.fullscreen-link').toggle($(document).fullScreen() != null);
	$('.fullscreen-link').click(function(){
		$('.slide-img').fullScreen(true);
	});
	$(document).bind("fullscreenchange", function() {
		var isFullscreen = $(document).fullScreen() ? true : false;
		if (isFullscreen) {
			$('.slide-img').css('background-color', 'black');
		} else {
			$('.slide-img').css('background-color', '');
		}
	});
	
	$('#page-selector').change(function() {
		var selectNum = $("#page-selector option:selected").text();
		gotoSlide(selectNum);
	});
	
	// Swipe method is NOT supported in IE6, so it should be the last one.
	$('.slide-img').swipeleft(function() { nextSlide(); });
	$('.slide-img').swiperight(function() { preSlide(); });
});

$(window).resize(function() {
	resetSlideSize();
});

function resetSlideSize() {
	var leftW = $('.row-fluid .span2').width() + 20;
	var windowW = $(window).width();
	if (windowW < 768) {
		leftW = -40;
	}
	var ww = $(window).width() - 90 - leftW;
	var wh = $(window).height() - 90;
	var isFullScreen = $(document).fullScreen() ? true : false;
	if (isFullScreen) {
		ww = ww + 90 + leftW;
		wh = wh + 80;
	}
	if (ww / wh > 4 / 3) {
		$('.slide-img').height(wh);
		$('.slide-img').width(wh * 4 / 3);
	} else {
		$('.slide-img').width(ww);
		$('.slide-img').height(ww * 3 / 4);
	}
}

$(document).keydown(function(event){
	if (event.keyCode == 37 || event.keyCode == 38) {
		preSlide();
	} else if (event.keyCode == 39 || event.keyCode == 40 || event.keyCode == 32){
		nextSlide();
	} else if (event.keyCode == 13) {
		var isFullscreen = $(document).fullScreen() ? true : false;
		$('.slide-img').toggleFullScreen();
	}
});

/* ---------------------------------------------------------------------- */
/*	Slide change
/* ---------------------------------------------------------------------- */
	function getCurSlide() {
		return curSlide;
	}
	
	function preSlide() {
		var preSlide = eval(Number(getCurSlide()) - 1);
		gotoSlide(preSlide);
	}
	
	function nextSlide() {
		var nextSlide = eval(Number(getCurSlide()) + 1);
		gotoSlide(nextSlide);
	}
	
	function gotoSlide(slide) {
		var slideSum = slideUrls.length;
		if (slide <= 0) {
			slide = 1;
		} else if (slideSum < slide) {
			slide = slideSum;
		}
		curSlide = slide;
		$('.slide-img img').fadeOut(function() {
			$(this).attr('src', slideUrls[slide - 1]).attr('id', 'slide-img-' + slide).fadeIn();
			$('.slide-img canvas').attr('id', 'slide-canvas-' + slide).fadeIn();
		});
		
		var percent = Math.ceil((curSlide / slideUrls.length) * 100);
		$('#page-selector').val(slide);
		$('.bottom-paging-progress .bar').width('' + percent + '%');
	}

/* ---------------------------------------------------------------------- */
/*	Draw init
/* ---------------------------------------------------------------------- */
	function initDraw() {
	
		if(!('getContext' in document.createElement('canvas'))){
			alert('对不起，您的浏览器不支持画笔同步，推荐您使用新版Chrome或火狐浏览器！');
			return false;
		}
		
		// Initialize parameters
		canvas = document.getElementById('slide-canvas-1');
		ctx = canvas.getContext("2d");
		ctx.strokeStyle = 'red';
	    ctx.lineWidth = "10";
	    ctx.lineCap = "round";
		img = $('#slide-img-1');
		
		socket.on('moving', function (data) {
	
			if(! (data.id in clients)){
				// a new user has come online. create a cursor for them
				cursors[data.id] = $('<div class="cursor">').appendTo('#cursors');
			}
			
			var absX = data.x * canvas.width;
			var absY = data.y * canvas.height;
	
			// Move the mouse pointer
			cursors[data.id].css({
				'left' : absX,
				'top' : absY
			});
			
			// Is the user drawing?
			if(data.drawing && clients[data.id]){
				
				// Draw a line on the canvas. clients[data.id] holds
				// the previous position of this user's mouse pointer
				
				var preAbsX = clients[data.id].x * canvas.width;
				var preAbsY = clients[data.id].y * canvas.height;
				drawLine(preAbsX, preAbsY, absX, absY);
				
				// save percent points
				p = {x1:clients[data.id].x, y1:clients[data.id].y, x2:data.x, y2:data.y};
				lines.push(p);
			}
			
			// Saving the current client state
			clients[data.id] = data;
			clients[data.id].updated = $.now();
		});
	
		// Send page turning command
		$('.btn-cmd').on('click touchstart', function(e) {
			e.preventDefault();
			if ($(this).attr('cmd-string')) {
				var cmdString = $(this).attr('cmd-string');
				var pageNum = 0;
				if ('left' == cmdString) {
					if (curSlide > 0) {
						curSlide = curSlide - 1;
					}
					// 1. update current page.
					$('#page-selector').val(curSlide + 1);
					gotoSlide(curSlide);
					
					// 2. send page number.
					socket.emit('flip', {
						'uuid': uuid,
						'page': curSlide,
					});
				} else if ('right' == cmdString) {
					var pageCount = slideUrls.length;
					if (curSlide < pageCount - 1) {
						curSlide = curSlide + 1;
					}
					// 1. update current page.
					$('#page-selector').val(curSlide + 1);
					gotoSlide(curSlide);
					
					// 2. send page number.
					socket.emit('flip', {
						'uuid': uuid,
						'page': curSlide,
					});
					
				} else if ('clear' == cmdString) {
					socket.emit('clear', {
						'uuid': uuid,
					});
					ctx.clearRect(0, 0, canvas.width, canvas.height);
					// location.reload();
				}
				// iosocket.send($(this).attr('data-key'));
			}
		});
		
		$('#page-selector').change(function() {
			var selectNum = $("#page-selector option:selected").text();
			curSlide = selectNum - 1;
			
			// send page event
			socket.emit('flip', {
				'uuid': uuid,
				'page': curSlide,
			});
			
			// update current page
			gotoSlide(curSlide);
		});
	
		socket.on('clear', function (data) {
			// lines = [];
			ctx.clearRect(0, 0, canvas.width, canvas.height);
			// location.reload();
			// draw();
		});
	
		/*
		$(document).mousemove(function(e) {
			var left = $('#slide-img-0').offset().left;
			var top = $('#slide-img-0').offset().top;
			var left2 = $('#slide-canvas-0').offset().left;
			var top2 = $('#slide-canvas-0').offset().top;
		});
		*/
		
		// Remove inactive clients after 10 seconds of inactivity
		setInterval(function() {
			
			for(ident in clients) {
				if($.now() - clients[ident].updated > 10000) {
					
					// Last update was more than 10 seconds ago. 
					// This user has probably closed the page
					
					cursors[ident].remove();
					delete clients[ident];
					delete cursors[ident];
				}
			}
			
		},10000);
	
		bindStart();
		bindMove();
		bindEnd();
		
		$('img').load(function() {
			draw();
		});
	}

/* ---------------------------------------------------------------------- */
/*	Bind events
/* ---------------------------------------------------------------------- */
	function bindStart() {
		$('canvas').bind('mousedown touchstart', function(e) {
			var type = e.type;
			var oleft = img.offset().left;
			var otop = img.offset().top;
			if ("mousedown" == type) {
				curr.x = Math.round(e.pageX - oleft);
				curr.y = Math.round(e.pageY - otop);
			} else if ("touchstart" == type) {
				var touch = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
				curr.x = Math.round(touch.pageX - oleft);
				curr.y = Math.round(touch.pageY - otop);
	
				// Move touchstart start position
				perc.x = (curr.x / canvas.width).toFixed(4);
				perc.y = (curr.y / canvas.height).toFixed(4);
				socket.emit('mousemove', {
					'uuid': uuid,
					'x': perc.x,
					'y': perc.y,
					'drawing': false,
					'id': clientId
				});
			}
			drawing = true;
			prev.x = curr.x;
			prev.y = curr.y;
		});
	}
	
	function bindMove() {
		$('canvas').bind('mousemove touchmove', function(e) {
			e.preventDefault();
			var oleft = img.offset().left;
			var otop = img.offset().top;
			var type = e.type;
			if ("mousemove" == type) {
				curr.x = Math.round(e.pageX - oleft);
				curr.y = Math.round(e.pageY - otop);
			} else if ("touchmove" == type) {
				var touch = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
				curr.x = Math.round(touch.pageX - oleft);
				curr.y = Math.round(touch.pageY - otop);
			}
			
			perc.x = (curr.x / canvas.width).toFixed(4);
			perc.y = (curr.y / canvas.height).toFixed(4);
	
			if (Math.sqrt(Math.pow(prev.x - curr.x, 2) + Math.pow(prev.y - curr.y, 2)) > 8) {
				/*
				var p = {x1:prev.x, y1:prev.y, x2:curr.x, y2:curr.y}
				lines.push(p);
				draw(p);
				send(JSON.stringify(p));
				prev = curr;
				*/
				socket.emit('mousemove', {
					'uuid': uuid,
					'x': perc.x,
					'y': perc.y,
					'drawing': drawing,
					'id': clientId
				});
			}
	
			// Draw a line for the current user's movement, as it is
			// not received in the socket.on('moving') event above
			
			if(drawing){
				
				drawLine(prev.x, prev.y, curr.x, curr.y);
	
				// save percent points
				var prePercX = (prev.x / canvas.width).toFixed(4);
				var prePercY = (prev.y / canvas.height).toFixed(4);
				p = {x1:prePercX, y1:prePercY, x2:perc.x, y2:perc.y};
				lines.push(p);
				
				prev.x = curr.x;
				prev.y = curr.y;
			}
		});
	}
	
	function bindEnd() {
		doc.bind('mouseup mouseleave touchend touchcancel', function(e) {
			e.preventDefault();
			drawing = false;
		});
	}
	
	
/* ---------------------------------------------------------------------- */
/*	Bind events
/* ---------------------------------------------------------------------- */
	function updatePage(pageNum) {
		$('.slide-img').html(
				'<img id="slide-img-' + pageNum + '" src="' + slideUrls[pageNum] + '" class="img-polaroid" style="height: 100%;">' + 
				'<canvas id="slide-canvas-' + pageNum + '" width="960" height="720" style="border: 1px solid orange; position: absolute;">Your browser does NOT support canvas!</canvas>'
			);
		
		draw();
		
		bindStart();
		bindMove();
		bindEnd();
	}

	function drawLine(fromx, fromy, tox, toy){
		ctx.moveTo(fromx, fromy);
		ctx.lineTo(tox, toy);
		/*
		ctx.strokeStyle = 'red';
	    ctx.lineWidth = "10";
	    ctx.lineCap = "round";
	    */
		ctx.stroke();
	}

	// Scale Canvas
	function draw() {
		// resize
		$('img').load(function() {
			resizeCanvas('slide-canvas-' + curSlide, 'slide-img-' + curSlide, 1.33);
			canvas = document.getElementById('slide-canvas-' + curSlide);
			ctx = canvas.getContext("2d");
			ctx.strokeStyle = 'red';
			ctx.lineWidth = "5";
			ctx.lineCap = "round";
			img = $('#slide-img-' + curSlide);
		});
		resizeCanvas('slide-canvas-' + curSlide, 'slide-img-' + curSlide, 1.33);
		canvas = document.getElementById('slide-canvas-' + curSlide);
		ctx = canvas.getContext("2d");
		ctx.strokeStyle = 'red';
		ctx.lineWidth = "5";
		ctx.lineCap = "round";
		img = $('#slide-img-' + curSlide);
		
		// location.reload();
		// ctx.clearRect (0, 0, 800, 600);
	}

	// -----------------------------------------------------
	// Scale canvas element canvas_id to have same width as element
	// match_id, and height = width/aspect.
	// -----------------------------------------------------
	function resizeCanvas(canvas_id, match_id, aspect) {
		var match_element = document.getElementById(match_id);
		if (match_element==undefined) {
			alert("Undefined element: " + match_id);
			return false; 
		}
		var cv_element = document.getElementById(canvas_id);	
		if (cv_element.tagName.toUpperCase() != 'CANVAS') {
			alert("resize canvas called for " + cv_element.tagName + " element instead of canvas");
			return false;
		}

		var canvas_width = match_element.offsetWidth;
		// To sort out: Opera and Firefox are a few pixels too large here

		var canvas_height = Math.round(canvas_width / aspect);

		cv_element.width = canvas_width;
		cv_element.height = canvas_height;

		$('#' + canvas_id).css({
			'left' : $('#' + match_id).offset().left,
			'top' : $('#' + match_id).offset().top,
		});
		
		/*
		$.map(lines, function (p) {
			var img = $('#' + match_id);
			var preAbsX = p.x1 * img.width();
			var preAbsY = p.y1 * img.height();
			var absX = p.x2 * img.width();
			var absY = p.y2 * img.height();
			drawLine(preAbsX, preAbsY, absX, absY);
		});
		*/
		return true;   
	}
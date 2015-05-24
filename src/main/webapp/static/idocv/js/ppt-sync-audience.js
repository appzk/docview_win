/**
 * Copyright 2015 I Doc View
 * 
 * @author Godwin <godwin668@gmail.com>
 */

// predefined params in ppt.js
// slideUrls, slideThumbUrls, ratio, curSlide, uuid, sessionId

// draw server
var drawServer = $('.server-param-container :text[key="conf-draw-server"]')
		.val();

var doc = $(document);
var win = $(window);
var canvas;
var ctx;
var img;

// all screen lines
var lines = [];

// Generate an unique ID
var id = Math.round($.now() * Math.random());

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
	// Check whether current browser support canvas
	if (!('getContext' in document.createElement('canvas'))) {
		alert('对不起，您的浏览器不支持画笔同步，推荐您使用新版Chrome或360浏览器！');
		return false;
	}
	
	gotoSlideSync(1);

	// receive moving event
	socket.on('moving', function (data) {
		var remoteUuid = data.uuid;
		if (uuid != remoteUuid) {
			return;
		}
		
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

	// receive flip event
	socket.on('flip', function (data) {
		var remoteUuid = data.uuid;
		if (uuid != remoteUuid) {
			return;
		}
		gotoSlideSync(data.page);
	});

	// receive clear event
	socket.on('clear', function (data) {
		// TODO
		// clear canvas
		var remoteUuid = data.uuid;
		if (uuid == remoteUuid) {
			location.reload();
		}
	});
	
	// fullscreen
	$('.fullscreen-link').click(function(){
		$('.slide-img-container-sync').fullScreen(true);
	});
	$(document).bind("fullscreenchange", function() {
		var isFullscreen = $(document).fullScreen() ? true : false;
		if (isFullscreen) {
			$('.slide-img-container-sync').css('background-color', 'black');
			$('.slide-img-container-sync').contextMenu(true);
		} else {
			$('.slide-img-container-sync').css('background-color', '');
			$('.slide-img-container-sync').contextMenu(false);
		}
	});
	
	// page selector
	$('.select-page-selector-sync').val(curSlide);
	$('.select-page-selector-sync').change(function() {
		var selectNum = $(".select-page-selector-sync option:selected").text();
		gotoSlideSync(selectNum);
	});
	$('.thumbnail').click(function () {
		var page_num = $(this).attr('page');
		gotoSlideSync(page_num);
	});
	
	// keyboard
	$(document).keydown(function(event){
		if (event.keyCode == 37 || event.keyCode == 38) {
			preSlideSync();
		} else if (event.keyCode == 39 || event.keyCode == 40 || event.keyCode == 32){
			nextSlideSync();
		} else if (event.keyCode == 13) {
			var isFullscreen = $(document).fullScreen() ? true : false;
			$('.slide-img-container-sync').toggleFullScreen();
		}
	});
});

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

$(window).resize(function() {
	resetImgSizeSync();
});

function resetImgSizeSync() {
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
	if (wh / ww < ratio) {
		$('.slide-img-container-sync').width(wh / ratio);
		$('.slide-img-container-sync').height(wh);
	} else {
		$('.slide-img-container-sync').width(ww);
		$('.slide-img-container-sync').height(ww * ratio);
	}
	$('.slide-canvas').width($('.slide-img-container-sync').width());
	$('.slide-canvas').height($('.slide-img-container-sync').height());
	$('.slide-canvas')[0].width = $('.slide-img-container-sync').width();
	$('.slide-canvas')[0].height = $('.slide-img-container-sync').height();
	
	resetStroke();
	resetContextMemu();
}

function resetStroke() {
	if (canvas) {
		ctx = canvas.getContext("2d");
		ctx.strokeStyle = 'red';
	    ctx.lineWidth = "2";
	    ctx.lineCap = "round";
	} else {
		alert('Please set canvas first!');
	}
}

function resetContextMemu() {
	// Right click (NOT supported in SOUGOU browser)
	$.contextMenu({
        selector: '.slide-img-container-sync',
        items: {
        	"next": {
                name: "下一张",
                callback: function(key, options) {
                	nextSlideSync();
                }
            },
            "previous": {
                name: "上一张",
                callback: function(key, options) {
                	preSlideSync();
                }
            },
            "sep1": "---------",
            "exit": {
                name: "结束放映",
                callback: function(key, options) {
                	$('.slide-img-container-sync').fullScreen(false);
                }
            },
        }
    });
	$('.slide-img-container-sync').contextMenu(true);
}

function preSlideSync() {
	var preSlide = eval(Number(getCurSlide()) - 1);
	gotoSlideSync(preSlide);
}

function nextSlideSync() {
	var nextSlide = eval(Number(getCurSlide()) + 1);
	gotoSlideSync(nextSlide);
}

function gotoSlideSync(slide) {
	// slide turning
	var preSlide = curSlide;
	var slideSum = slideUrls.length;
	if (slide <= 0) {
		slide = 1;
	} else if (slideSum < slide) {
		slide = slideSum;
	}
	curSlide = slide;
	
	// set img & canvas
	var imgHtml = '<img class="slide-img-' + (slide - 1) + '" src="' + slideUrls[slide - 1] + '" class="img-polaroid" style="height: 100%;">';
	var canvasHtml = '<canvas class="slide-canvas-' + (slide - 1) + ' slide-canvas" style="width: 100%; height: 100%; border: 1px solid orange; position: absolute; left: 0px; top: 0px;">您的浏览器不支持画布！</canvas>';
	$('.slide-img-container-sync').html(imgHtml + canvasHtml);
	canvas = $('.slide-canvas-' + (slide - 1))[0];
	resetImgSizeSync();
	img = $('.slide-img-' + (slide - 1));
	
	/*
	 * $(".slide-img-container-sync img").fadeOut(function() { $(this).attr("src",
	 * slideUrls[slide - 1]).fadeIn(); });
	 */
	$(".slide-img-container-sync img").attr("src", slideUrls[slide - 1]);
	var percent = Math.ceil((curSlide / slideUrls.length) * 100);
	$('.thumbnail').removeClass('ppt-thumb-border');
	$('.thumbnail[page="' + slide + '"]').addClass('ppt-thumb-border');
	$('.select-page-selector').val(slide);
	$('.select-page-selector-sync').val(slide);
	$('.bottom-paging-progress .bar').width('' + percent + '%');

	// sync flip event to audience
	socket.emit('flip', {
		'uuid' : uuid,
		'page' : curSlide,
	});
}
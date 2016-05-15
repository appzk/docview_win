/**
 * Copyright 2015 I Doc View
 *
 * @author Godwin <godwin668@gmail.com>
 */

// predefined params in word-pdf-single.js
// slideUrls, curSlide, uuid, sessionId

// draw server
var drawServer = $('.server-param-container :text[key="conf-draw-server"]')
	.val();

var uid = !!($.url().param('uid')) ? $.url().param('uid') : 'default';
var localStorageKey = 'IDOCV_DRAW_LINES_' + uuid + '_' + uid;

var canvasArray = new Array();
var ctxArray = new Array();
var imgArray = new Array();

var doc = $(document);
var win = $(window);
var canvas;
var ctx;
var img;

// all screen lines, page->lines
var lines = [[]];
// whether the canvas lines were changed or NOT, if it is changed, it should be saved again.
var isChanged = false;

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

var imgWidth;
var imgHeight;
var imgRatio;	// height/width

var socket = io.connect(drawServer);

$(document).ready(function() {

	// Check whether current browser support canvas
	if (!('getContext' in document.createElement('canvas'))) {
		alert('对不起，您的浏览器不支持画笔同步，推荐您使用新版Chrome或360浏览器！');
		return false;
	}

	gotoSlideSync(1);

	// receive socket event
	// speaker does NOT need receive moving event from audience

	// send flip event
	// Send page turning command
	$('.btn-cmd').on('click touchstart', function(e) {
		e.preventDefault();
		if ($(this).attr('cmd-string')) {
			var cmdString = $(this).attr('cmd-string');
			var pageNum = 0;
			if ('left' == cmdString) {
				preSlideSync();
			} else if ('right' == cmdString) {
				nextSlideSync();
			} else if ('clear' == cmdString) {
				socket.emit('clear', {
					'uuid' : uuid,
				});
				clear();
				// location.reload();
			}
			// iosocket.send($(this).attr('data-key'));
		}
	});
	// color selector
	$('.colorselector').change(function() {
		$('.colorselector').css('background-color', $(this).val());
		resetStroke();
	});

	// page selector
	$('.select-page-selector-sync').val(curSlide);
	$('.select-page-selector-sync').change(function() {
		var selectNum = $(".select-page-selector-sync option:selected").text();
		gotoSlideSync(selectNum);
	});

	// send draw event

	// keyboard
	$(document).keydown(function(event){
		if (event.keyCode == 37 || event.keyCode == 38) {
			preSlideSync();
		} else if (event.keyCode == 39 || event.keyCode == 40 || event.keyCode == 32){
			nextSlideSync();
		} else if (event.keyCode == 13) {
			var isFullscreen = $(document).fullScreen() ? true : false;
			$('.pdf-content').toggleFullScreen();
		}
	});
});

var imgLoadInterval = setInterval(initDraw, 50);
function initDraw() {
	if ($('img').length <= 0 || $('img')[0].height <= 0) {
		return;
	}
	clearInterval(imgLoadInterval);
	imgWidth = $('img')[0].width;
	imgHeight = $('img')[0].height;
	imgRatio = imgHeight / imgWidth;

	console.log('[INIT DRAW] uid=' + uid);

	// set img & canvas
	for (var i = 0; i < slideUrls.length; i++) {
		var canvasHtml = '<canvas class="slide-canvas-' + (i + 1) + ' slide-canvas" style="width: 100%; height: 100%; border: 1px solid orange; position: absolute; left: 0px; top: 0px;">您的浏览器不支持画布！</canvas>';
		$('.pdf-content:eq(' + i + ')').append(canvasHtml);
		canvasArray.push($('.slide-canvas-' + (i + 1))[0]);
		ctxArray.push(canvasArray[i].getContext("2d"));
		imgArray.push($('.slide-img-' + (i + 1)));
		lines[i] = [];
	}
	// set all canvas size
	setTimeout(function () {
		var fstCvsW = canvasArray[0].width;
		var fstCvsH = canvasArray[0].height;
		for (var i = 1; i < canvasArray.length; i++) {
			canvasArray[i].width = fstCvsW;
			canvasArray[i].height = fstCvsH;
		}
		resetStroke();
		$('#back-to-top').remove();
	}, 5);

	curSlide = 1;
	canvas = canvasArray[curSlide - 1];
	ctx = canvasArray[curSlide - 1].getContext("2d");
	img = imgArray[curSlide - 1];

	/*
	$('.pdf-content').on("mousedown touchstart", function() {
		curSlide = $(this).attr('id');
		canvas = canvasArray[curSlide - 1];
		ctx = canvasArray[curSlide - 1].getContext("2d");
		img = imgArray[curSlide - 1];
	});
	*/

	resetImgSizeSync();
	bindCanvasEvent();

	restore();
}

// bind canvas event: start -> move -> end
function bindCanvasEvent() {
	// start
	$('canvas').bind('mousedown touchstart', function(e) {
		console.log('[CANVAS EVENT] mousedown touchstart ...');
		resetStroke();

		// focus on current canvas
		curSlide = $(this).parent().attr('id');
		canvas = canvasArray[curSlide - 1];
		ctx = ctxArray[curSlide - 1];
		img = imgArray[curSlide - 1];
		
		var type = e.type;
		var oleft = img.offset().left;
		var otop = img.offset().top;
		if ("mousedown" == type) {
			curr.x = Math.round(e.pageX - oleft);
			curr.y = Math.round(e.pageY - otop);
		} else if ("touchstart" == type) {
			var touch = e.originalEvent.touches[0]
				|| e.originalEvent.changedTouches[0];
			curr.x = Math.round(touch.pageX - oleft);
			curr.y = Math.round(touch.pageY - otop);

			// Move touchstart start position
			perc.x = (curr.x / canvas.width).toFixed(4);
			perc.y = (curr.y / canvas.height).toFixed(4);
			socket.emit('mousemove', {
				'uuid' : uuid,
				'x' : perc.x,
				'y' : perc.y,
				'drawing' : false,
				'id' : id
			});
		}
		drawing = true;
		prev.x = curr.x;
		prev.y = curr.y;
	});

	// move
	$('canvas').bind('mousemove touchmove', function(e) {
		console.log('[CANVAS EVENT] mousemove touchmove ...');
		e.preventDefault();
		var oleft = img.offset().left;
		var otop = img.offset().top;
		var type = e.type;
		if ("mousemove" == type) {
			curr.x = Math.round(e.pageX - oleft);
			curr.y = Math.round(e.pageY - otop);
		} else if ("touchmove" == type) {
			var touch = e.originalEvent.touches[0]
				|| e.originalEvent.changedTouches[0];
			curr.x = Math.round(touch.pageX - oleft);
			curr.y = Math.round(touch.pageY - otop);
		}

		perc.x = (curr.x / canvas.width).toFixed(4);
		perc.y = (curr.y / canvas.height).toFixed(4);

		console.log('[moving] slide' + curSlide + ', perc(' + perc.x + ', ' + perc.y + '), curr(' + curr.x + ', ' + curr.y + '), canvas_Width_Height(' + canvas.width + ', ' + canvas.height + '), e.page(' + e.pageX + ', ' + e.pageY + '), img.offset_LEF_TOP(' + img.offset().left + ', ' + img.offset().top + ')');

		if (Math.sqrt(Math.pow(prev.x - curr.x, 2)
				+ Math.pow(prev.y - curr.y, 2)) > 8) {
			/*
			 * var p = {x1:prev.x, y1:prev.y, x2:curr.x, y2:curr.y}
			 * lines.push(p); draw(p); send(JSON.stringify(p)); prev =
			 * curr;
			 */
			socket.emit('mousemove', {
				'uuid' : uuid,
				'x' : perc.x,
				'y' : perc.y,
				'drawing' : drawing,
				'id' : id
			});

			// Draw a line for the current user's movement, as it is
			// not received in the socket.on('moving') event above
			if (drawing) {
				drawLine(prev.x + 1, prev.y, curr.x + 1, curr.y);

				// save percent points
				var prePercX = (prev.x / canvas.width).toFixed(4);
				var prePercY = (prev.y / canvas.height).toFixed(4);
				p = {
					p: curSlide,
					x1 : prePercX,
					y1 : prePercY,
					x2 : perc.x,
					y2 : perc.y,
					c: ctx.strokeStyle,	// line color
					w: ctx.lineWidth		// line width
				};
				console.log('draw line: ' + JSON.stringify(p));
				lines[curSlide - 1].push(p);

				prev.x = curr.x;
				prev.y = curr.y;
			}
		}
	});

	// end
	doc.bind('mouseup mouseleave touchend touchcancel', function(e) {
		console.log('[CANVAS EVENT] mouseup mouseleave touchend touchcancel ...');
		drawing = false;

		save();
		// e.preventDefault();	// enable bottom tool bar event.
	});
}

function drawLine(fromx, fromy, tox, toy){
	ctx.beginPath();
	ctx.strokeStyle = ctx.strokeStyle;
	ctx.lineWidth = ctx.lineWidth;
	ctx.lineCap = "round";
	ctx.moveTo(fromx, fromy);
	ctx.lineTo(tox, toy);
	ctx.stroke();
	isChanged = true;
}

function drawLinePro(page, fromx, fromy, tox, toy, color, width){
	var curCtx = canvasArray[page - 1].getContext("2d");
	curCtx.beginPath();
	curCtx.strokeStyle = color;
	curCtx.lineWidth = width;
	curCtx.lineCap = "round";
	curCtx.moveTo(fromx, fromy);
	curCtx.lineTo(tox, toy);
	curCtx.stroke();
	isChanged = true;
}

function redrawAll() {
	if (lines) {
		for (var i = 0; i < lines.length; i++) {
			for (var j = 0; j < lines[i].length; j++) {
				var p = lines[i][j];
				drawLinePro(p.p, imgWidth * p.x1, imgHeight * p.y1, imgWidth * p.x2, imgHeight * p.y2, p.c, p.w);
			}
		}
	}
}

function save() {
	// store lines to localStorage
	if(typeof(Storage) !== "undefined") {
		localStorage.setItem(localStorageKey, JSON.stringify(lines));
		// Code for localStorage
	}

	$.post('/draw/save/' + uuid, { uid: uid, data: JSON.stringify(lines) }, function(data, status){
		console.log("[LINES SAVED] Status: " + status + ", Data: " + JSON.stringify(data));
	});

	isChanged = false;
}

function restore() {
	// restore lines from localStorage
	setTimeout(function () {
		$.get( '/draw/get/' + uuid, { uid: uid } ).done(function( data ) {
			var code = data.code;
			if (1 == code) {
				var loadedData = data.data;
				if (!!loadedData && loadedData.length > 0) {
					lines = JSON.parse(loadedData);
					redrawAll();
				}
				console.log('[DRAW DATA LOADED] ' + loadedData);
			} else {
				console.log('[DRAW DATA LOAD ERROR] data: ' + JSON.stringify(data));
				if(typeof(Storage) !== "undefined") {
					var linesStr = localStorage.getItem(localStorageKey);
					if (!!linesStr && ('null' !== linesStr)) {
						console.log('[LOADING DATA FROM LOCAL STORAGE] lines: ' + linesStr);
						lines = JSON.parse(linesStr);
						redrawAll();
					}
				}
			}
		});
		/*
		if(typeof(Storage) !== "undefined") {
			var linesStr = localStorage.getItem('lines');
			if (!!linesStr && ('null' !== linesStr)) {
				lines = JSON.parse(linesStr);
				redrawAll();
			}
		}
		*/
	}, 200);
}

function clear() {
	ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
	lines[curSlide - 1] = [];
	isChanged = true;
	save();
}

$(window).resize(function() {
	console.log('[WINDOW RESIZE] pdf-content(' + $('.pdf-content').width() + ', ' + $('.pdf-content').height() + ')');
	resetImgSizeSync();
	redrawAll();
});

function resetImgSizeSync() {
	var curWidth = $('.pdf-content').width();
	var curHeight = $('.pdf-content').height();
	var curRatio = curHeight / curWidth;
	curHeight = curWidth * imgRatio;

	imgWidth = curWidth;
	imgHeight = curHeight;

	/*
	$('.pdf-content img').load(function() {
		$('.slide-canvas').width(curWidth);
		$('.slide-canvas').height(curHeight);
		$('.slide-canvas')[0].width = curWidth;
		$('.slide-canvas')[0].height = curHeight;
		resetStroke();
	});
	*/
	$('.slide-canvas').width(curWidth);
	$('.slide-canvas').height(curHeight);
	for (var i = 0; i < $('.slide-canvas').length; i++) {
		$('.slide-canvas')[i].width = curWidth;
		$('.slide-canvas')[i].height = curHeight;
	}
	$('.pdf-content').height(curHeight);
	$('.pdf-content img').width(curWidth);
	$('.pdf-content img').height(curHeight);
	resetStroke();
}

function resetStroke() {
	if (canvasArray.length > 0) {
		for (var i = 0; i < canvasArray.length; i++) {
			var ctxLocal = ctxArray[i];
			ctxLocal.strokeStyle = $('.colorselector').val();
			ctxLocal.lineWidth = "2";
			ctxLocal.lineCap = "round";
		}
	} else {
		alert('Please set canvas first!');
	}
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

	/*
	 * $(".pdf-content img").fadeOut(function() { $(this).attr("src",
	 * slideUrls[slide - 1]).fadeIn(); });
	 */
	$(".pdf-content img").attr("src", slideUrls[slide - 1]);
	var percent = Math.ceil((curSlide / slideUrls.length) * 100);
	$('.select-page-selector').val(slide);
	$('.select-page-selector-sync').val(slide);
	$('.bottom-paging-progress .bar').width('' + percent + '%');

	// sync flip event to audience
	socket.emit('flip', {
		'uuid' : uuid,
		'page' : curSlide,
	});
}
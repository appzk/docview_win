// The URL of your web server (the port is set in app.js)
var url = 'http://draw.idocv.com:8080';

var uuid;
var doc = $(document);
var win = $(window);
var canvas;
var ctx;
var img;
var curPage = 0;

// all screen lines
var slideurls = [];
var lines = [];

// Generate an unique ID
var id = Math.round($.now()*Math.random());

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

var socket = io.connect(url);

function initDraw() {

	// This demo depends on the canvas element
	if(!('getContext' in document.createElement('canvas'))){
		alert('Sorry, it looks like your browser does not support canvas!');
		return false;
	}
	
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
				if (curPage > 0) {
					curPage = curPage - 1;
				}
				// 1. update current page.
				$('#page-selector').val(curPage + 1);
				updatePage(curPage);
				
				// 2. send page number.
				socket.emit('flip', {
					'uuid': uuid,
					'page': curPage,
				});
			} else if ('right' == cmdString) {
				var pageCount = slideurls.length;
				if (curPage < pageCount - 1) {
					curPage = curPage + 1;
				}
				// 1. update current page.
				$('#page-selector').val(curPage + 1);
				updatePage(curPage);
				
				// 2. send page number.
				socket.emit('flip', {
					'uuid': uuid,
					'page': curPage,
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
		curPage = selectNum - 1;
		
		// send page event
		socket.emit('flip', {
			'uuid': uuid,
			'page': curPage,
		});
		
		// update current page
		updatePage(curPage);
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
				'id': id
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
				'id': id
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

function updatePage(pageNum) {
	$('#slides').html(
		'<slide>' +
			'<img id="slide-img-' + pageNum + '" src="' + slideurls[pageNum] + '" class="ppt-slide-img" />' +
			'<canvas id="slide-canvas-' + pageNum + '" width="960" height="720" style="border: 1px solid orange; position: absolute;">Your browser does NOT support canvas!</canvas>' +
		'</slide>'
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
		resize_canvas('slide-canvas-' + curPage, 'slide-img-' + curPage, 1.33);
		canvas = document.getElementById('slide-canvas-' + curPage);
		ctx = canvas.getContext("2d");
		ctx.strokeStyle = 'red';
		ctx.lineWidth = "5";
		ctx.lineCap = "round";
		img = $('#slide-img-' + curPage);
	});
	resize_canvas('slide-canvas-' + curPage, 'slide-img-' + curPage, 1.33);
	canvas = document.getElementById('slide-canvas-' + curPage);
	ctx = canvas.getContext("2d");
	ctx.strokeStyle = 'red';
	ctx.lineWidth = "5";
	ctx.lineCap = "round";
	img = $('#slide-img-' + curPage);
	
	// location.reload();
	// ctx.clearRect (0, 0, 800, 600);
}

// -----------------------------------------------------
// Scale canvas element canvas_id to have same width as element
// match_id, and height = width/aspect.
// -----------------------------------------------------
function resize_canvas(canvas_id, match_id, aspect) {
	var match_element = document.getElementById(match_id);
	if (match_element==undefined) {
		alert("Undefined element: " + match_id);
		return false; 
	}
	var cv_element = document.getElementById(canvas_id);	
	if (cv_element.tagName.toUpperCase() != 'CANVAS') {
		alert("Resize_canvas called for " + cv_element.tagName + " element instead of canvas");
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
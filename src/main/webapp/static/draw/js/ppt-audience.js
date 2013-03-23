// The URL of your web server (the port is set in app.js)
var url = 'http://draw.idocv.com:8080';

var doc = $(document);
var win = $(window);
var canvas;
var ctx;
var img;
var scale;

// all screen lines
var lines = [];

function initDraw() {

	// This demo depends on the canvas element
	if(!('getContext' in document.createElement('canvas'))){
		alert('Sorry, it looks like your browser does not support canvas!');
		return false;
	}
	
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

	// Clear Canvas
	$("#btn-clear").on('click touchend', function(e) {
		socket.emit('clear', {
			
		});
		lines = [];
		// draw();
	});

	socket.on('clear', function (data) {
		lines = [];
		ctx.clearRect(0, 0, canvas.width, canvas.height);
		// location.reload();
		// draw();
	});

	$('canvas').bind('mousedown touchstart', function(e) {
		var type = e.type;
		var oleft = img.offset().left - 35;
		var otop = img.offset().top;
		if ("mousedown" == type) {
			curr.x = Math.round(e.pageX / scale - oleft);
			curr.y = Math.round(e.pageY / scale - otop);
		} else if ("touchstart" == type) {
			var touch = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
			curr.x = Math.round(touch.pageX / scale - oleft);
			curr.y = Math.round(touch.pageY / scale - otop);

			// Move touchstart start position
			perc.x = (curr.x / canvas.width).toFixed(4);
			perc.y = (curr.y / canvas.height).toFixed(4);
			socket.emit('mousemove',{
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
	
	doc.bind('mouseup mouseleave touchend touchcancel', function(e) {
		e.preventDefault();
		drawing = false;
	});

	$('canvas').bind('mousemove touchmove', function(e) {
		console.log('prev (' + prev.x + ', ' + prev.y + ') | curr(' + curr.x + ', ' + curr.y + ')');
		e.preventDefault();
		var oleft = img.offset().left - 35;
		var otop = img.offset().top;
		var type = e.type;
		if ("mousemove" == type) {
			curr.x = Math.round(e.pageX / scale - oleft);
			curr.y = Math.round(e.pageY / scale - otop);
		} else if ("touchmove" == type) {
			var touch = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
			curr.x = Math.round(touch.pageX / scale - oleft);
			curr.y = Math.round(touch.pageY / scale - otop);
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
			socket.emit('mousemove',{
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

	// draw();

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

/*
// Scale Canvas
function draw() {
	// resize_canvas('slide-canvas-0', 'slide-img-0', 1.33);
	// location.reload();
	// ctx.clearRect (0, 0, 800, 600);
}

// -----------------------------------------------------
// Scale canvas element canvas_id to have same width as element
// match_id, and height = width/aspect.
// -----------------------------------------------------
function resize_canvas(canvas_id, match_id, aspect) {
	/*
	var match_element = document.getElementById(match_id);
	if (match_element==undefined)
	{	alert("Undefined element: " + match_id);
		return false; 
	}
	var cv_element = document.getElementById(canvas_id);	
	if (cv_element.tagName.toUpperCase() != 'CANVAS')
	{	alert("Resize_canvas called for " + cv_element.tagName + " element instead of canvas");
		return false;
	}

	var canvas_width = match_element.offsetWidth;
	// To sort out: Opera and Firefox are a few pixels too large here

	var canvas_height = Math.round(canvas_width / aspect);

	cv_element.width = canvas_width;
	cv_element.height = canvas_height;

	$.map(lines, function (p) {
		var img = $('#' + match_id);
		var preAbsX = p.x1 * img.width();
		var preAbsY = p.y1 * img.height();
		var absX = p.x2 * img.width();
		var absY = p.y2 * img.height();
		drawLine(preAbsX, preAbsY, absX, absY);
	});
	return true;   
}
 */
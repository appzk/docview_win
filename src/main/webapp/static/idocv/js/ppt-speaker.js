$(document).ready(function() {
	$('body').simpleLoadingModal();
	var uuid = $.url().param('uuid');
	var sessionId = $.url().param('session');
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.container-fluid:first .btn').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				slideurls[i] = page.url;
			}
			
			$('#slides').html(
				'<img id="slide-img-0" src="' + slideurls[0] + '" class="ppt-slide-img" />' +
				'<canvas id="slide-canvas-0" width="960" height="720" style="border: 1px solid orange; position: absolute;">Your browser does NOT support canvas!</canvas>'
			);
			
			for (i = 0; i < slideurls.length; i++) {
				$('#page-selector').append('<option>' + (i + 1) + '</option>');
			}
			
			initDraw();
		} else {
			$('.span12').append('<section><div class="alert alert-error">' + data.desc + '</div></section>');
		}
		
		// hide loader
		$("#loader").fadeOut();
		$("#dvGlobalMask").fadeOut();
		
		canvas = document.getElementById('slide-canvas-0');
		ctx = canvas.getContext("2d");
		ctx.strokeStyle = 'red';
	    ctx.lineWidth = "10";
	    ctx.lineCap = "round";
		img = $('#slide-img-0');
		
	});
	
});
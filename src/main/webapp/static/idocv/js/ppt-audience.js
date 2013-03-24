$(document).ready(function() {
	$('body').simpleLoadingModal();
	uuid = $.url().param('uuid');
	var sessionId = $.url().param('session');
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.container-fluid .btn:first').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.slides').append(
					'<section>' +
						'<div style="position: relative; top: 0px; left: 0px;">' +
							'<img id="slide-img-' + i + '" src="' + page.url + '" class="ppt-slide-img" />' +
							'<canvas id="slide-canvas-' + i + '" width="896" height="672" style="border: 1px solid orange; position: absolute; left: 30px; top: 0px;"></canvas>' +
						'</div>' +
					'</section>'
				);
			}
			initDraw();
		} else {
			$('.slides').append('<section><div class="alert alert-error">' + data.desc + '</div></section>');
		}
		
		// Full list of configuration options available here:
		// https://github.com/hakimel/reveal.js#configuration
		Reveal.initialize({
			controls: true,
			progress: true,
			history: true,
			center: true,

			theme: Reveal.getQueryHash().theme, // available themes are in /css/theme
			transition: Reveal.getQueryHash().transition || 'default', // default/cube/page/concave/zoom/linear/fade/none

			// Optional libraries used to extend on reveal.js
			dependencies: [
				{ src: '/static/reveal/lib/js/classList.js', condition: function() { return !document.body.classList; } },
				{ src: '/static/reveal/plugin/markdown/showdown.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },
				{ src: '/static/reveal/plugin/markdown/markdown.js', condition: function() { return !!document.querySelector( '[data-markdown]' ); } },
				{ src: '/static/reveal/plugin/highlight/highlight.js', async: true, callback: function() { hljs.initHighlightingOnLoad(); } },
				{ src: '/static/reveal/plugin/zoom-js/zoom.js', async: true, condition: function() { return !!document.body.classList; } },
				{ src: '/static/reveal/plugin/notes/notes.js', async: true, condition: function() { return !!document.body.classList; } }
				// { src: '/static/reveal/plugin/remotes/remotes.js', async: true, condition: function() { return !!document.body.classList; } }
			]
		});
		
		Reveal.addEventListener( 'ready', function( event ) {
		    // event.currentSlide, event.indexh, event.indexv
			// hide loader
			$("#loader").fadeOut();
			$("#dvGlobalMask").fadeOut();
			
			// Draw event
			// canvas = $('#slide-canvas-' + event.indexh);
			// ctx = canvas[0].getContext('2d');
			canvas = document.getElementById('slide-canvas-' + event.indexh);
			ctx = canvas.getContext("2d");
			ctx.strokeStyle = 'red';
		    ctx.lineWidth = "8";
		    ctx.lineCap = "round";
			
			img = $('#slide-img-' + event.indexh);
			scale = Reveal.getScale();
			
			// canvas = $('#slide-canvas-' + event.indexh);
			// ctx = canvas[0].getContext('2d');
		} );
		
		Reveal.addEventListener( 'slidechanged', function( event ) {
		    // event.previousSlide, event.currentSlide, event.indexh, event.indexv
			// canvas = $('#slide-canvas-' + event.indexh);
			// ctx = canvas[0].getContext('2d');
			canvas = document.getElementById('slide-canvas-' + event.indexh);
			ctx = canvas.getContext("2d");
			ctx.strokeStyle = 'red';
		    ctx.lineWidth = "8";
		    ctx.lineCap = "round";
			
			img = $('#slide-img-' + event.indexh);
			scale = Reveal.getScale();
		} );
		
	});
	
});
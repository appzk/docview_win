/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var uuid = $.url().segment(2);
var sessionId = $.url().param('session');
﻿var address = 'http://api.idocv.com/view/' + uuid;
$(document).ready(function() {
	
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.container-fluid .btn').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			// $(".qrcode").qrcode(address);
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.slides').append('<section><img src="' + page.url + '" class="ppt-slide-img" /></section>');
			}
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
			
			// clear progress bar
			clearProgress();
		} );
		
		clearProgress();
	});
});
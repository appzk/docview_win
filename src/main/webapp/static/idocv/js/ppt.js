$(document).ready(function() {
	$('body').simpleLoadingModal();
	var uuid = $.url().param('uuid');
	$.get('http://api.idocv.com/v/' + uuid + '.json', function(data, status) {
		var rid = data.rid;
		var uuid = data.uuid;
		var pages = data.data;
		
		// title
		$('.container-fluid .btn').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
		
		// pages
		for (i = 0; i < pages.length; i++) {
			var page = pages[i];
			$('.slides').append('<section><img src="' + page.url + '" class="ppt-slide-img" /></section>');
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
		} );
		
	});
});
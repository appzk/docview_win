$(document).ready(function() {
	$('body').simpleLoadingModal();
	var uuid = $.url().param('uuid');
	$.get('/v/' + uuid + '.json', function(data, status) {
		var rid = data.rid;
		var uuid = data.uuid;
		var pages = data.data;
		
		// title
		$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
		
		// pages
		for (i = 0; i < pages.length; i++) {
			var page = pages[i];
			$('.span12').append('<div class="word-page"><div class="word-content">' + page.content + '</div></div>');
		}
		
		if (document.createStyleSheet){
			document.createStyleSheet('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />');
		} else {
			$("head").append($('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />'));
		}
		
		// hide loader
		$("#loader").fadeOut();
		$("#dvGlobalMask").fadeOut();
	});
});
$(document).ready(function() {
	$('body').simpleLoadingModal();
	var uuid = $.url().segment(2);
	var sessionId = $.url().param('session');
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
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
		} else {
			$('.span12').append('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// hide loader
		$("#loader").fadeOut();
		$("#dvGlobalMask").fadeOut();
	});
});
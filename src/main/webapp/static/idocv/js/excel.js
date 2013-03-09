$(document).ready(function() {
	$('body').simpleLoadingModal();
	var uuid = $.url().param('uuid');
	$.get('/v/' + uuid + '.json', function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				// tab navigation & tab content
				if (0 == i) {
					$('.navbar-inner .container-fluid .nav-collapse:first .nav').append('<li class="active"><a href="#tab' + (i + 1) + '" data-toggle="tab">' + page.title + '</a></li>');
					$('.tab-content').append('<div class="tab-pane fade in active" id="tab' + (i + 1) + '">' + page.content + '</div>');
				} else {
					$('.navbar-inner .container-fluid .nav-collapse:first .nav').append('<li><a href="#tab' + (i + 1) + '" data-toggle="tab">' + page.title + '</a></li>');
					$('.tab-content').append('<div class="tab-pane fade in" id="tab' + (i + 1) + '">' + page.content + '</div>');
				}
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
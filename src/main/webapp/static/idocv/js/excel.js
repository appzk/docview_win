$(document).ready(function() {
	$('body').simpleLoadingModal();
	var uuid = $.url().param('uuid');
	$.get('/v/' + uuid + '.json', function(data, status) {
		var rid = data.rid;
		var uuid = data.uuid;
		var pages = data.data;
		
		// title
		$('.container-fluid .btn').after('<a class="brand" href="/doc/download/' + uuid + '">' + data.name + '</a>');
		
		// pages
		for (i = 0; i < pages.length; i++) {
			var page = pages[i];
			// tab navigation & tab content
			if (0 == i) {
				$('ul').append('<li class="active"><a href="#tab' + (i + 1) + '" data-toggle="tab">' + page.title + '</a></li>');
				$('.tab-content').append('<div class="tab-pane fade in active" id="tab' + (i + 1) + '">' + page.content + '</div>');
			} else {
				$('ul').append('<li><a href="#tab' + (i + 1) + '" data-toggle="tab">' + page.title + '</a></li>');
				$('.tab-content').append('<div class="tab-pane fade in" id="tab' + (i + 1) + '">' + page.content + '</div>');
			}
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
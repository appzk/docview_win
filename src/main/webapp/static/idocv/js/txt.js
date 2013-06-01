$(document).ready(function() {
	var uuid = $.url().segment(2);
	var sessionId = $.url().param('session');
	﻿var address = 'http://api.idocv.com/view/' + uuid;
	
	$.get('/view/' + uuid + '.json', function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.container-fluid .btn').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			$(".qrcode").qrcode(address);
	
			// pages
			for (i = 0; i < 1; i++) {
				var page = pages[i];
				// $('.word-content pre').text(page.content);
				$('.span12').append('<div class="word-page"><div class="word-content"><pre>' + page.content + '</pre></div></div>');
			}
		} else {
			$('.span12').append('<div class="alert alert-error">' + data.desc + '</div>');
		}

		// clear progress bar
		clearProgress();
	});
});
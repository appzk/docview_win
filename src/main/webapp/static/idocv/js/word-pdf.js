/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var uuid = $.url().segment(2);
var params = $.url().param();
$(document).ready(function() {
	
	$.get('/view/' + uuid + '.json?start=1&size=5', params, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			$('title').text(data.name);
			
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.span12').append('<div class="pdf-page"><div class="pdf-content"><img alt="第' + (i + 1) + '页" src="' + page.url + '"></div></div>');
			}
			
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
	});
});
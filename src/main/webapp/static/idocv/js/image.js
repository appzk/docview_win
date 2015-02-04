/**
 * Copyright 2015 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var totalSize = 1;
var uuid = $.url().segment(2);
var sessionId = $.url().param('session');
$(document).ready(function() {
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.span12').append('<p><img src="' + page.url + '" alt="' + data.name + '" /></p>');
			}
			
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
	});
});
﻿/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var id = $.url().segment(2);
var uuid = id;
var sessionId = $.url().param('session');
$(document).ready(function() {
	
	$.ajax({
		type: "GET",
		url: '/view/' + uuid + '.json?start=1&size=0',
		data: {session:sessionId},
		async: false,
		dataType: "json"
	}).done(function( data ) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			document.title = data.name;
			
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
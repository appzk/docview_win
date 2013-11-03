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
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			// $(".qrcode").qrcode(address);
			
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
		
		// clear progress bar
		clearProgress();
	});
});
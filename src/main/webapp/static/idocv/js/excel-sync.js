/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var uuid = $.url().segment(2);
var sessionId = $.url().param('session');
$(document).ready(function() {
	
	$.ajax({
		type: "GET",
		url: '/view/' + uuid + '.json',
		data: {session:sessionId},
		async: false,
		dataType: "json",
	}).done(function( data ) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			
			// pages
			// Dropdown tabs
			var isDropDown = pages.length > 4;
			if (isDropDown) {
				var dropDownMenu = '<li class="dropdown">' +
				'<a href="#" class="dropdown-toggle" data-toggle="dropdown">' +
				'表单' +
				'<b class="caret"></b>' +
				'</a>' +
				'<ul class="dropdown-menu">' +
				'<!-- DROP DOWN EXCEL TAB TITLE(s) HERE -->' +
				'</ul>' +
				'</li>';
				$('.navbar-inner .container-fluid .nav-collapse:first .nav').append(dropDownMenu);
			}
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				// tab navigation & tab content
				if (0 == i) {
					$('.navbar-inner .container-fluid .nav-collapse:first .nav' + (isDropDown ? ' .dropdown .dropdown-menu' : '')).append('<li class="active"><a href="#tab' + (i + 1) + '" data-toggle="tab">' + page.title + '</a></li>');
					$('.tab-content').append('<div class="tab-pane fade in active" id="tab' + (i + 1) + '">' + page.content + '</div>');
				} else {
					$('.navbar-inner .container-fluid .nav-collapse:first .nav' + (isDropDown ? ' .dropdown .dropdown-menu' : '')).append('<li><a href="#tab' + (i + 1) + '" data-toggle="tab">' + page.title + '</a></li>');
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
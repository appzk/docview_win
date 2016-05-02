/**
 * Copyright 2015 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var totalSize = 1;
var id = $.url().segment(2);
var uuid = id;
var params = $.url().param();
var degree = 0;

$(document).ready(function() {
	$.get('/view/' + uuid + '.json', params, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			document.title = data.name;
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.span12').append('<p><img id="img1" src="' + page.url + '" alt="' + data.name + '" /></p>');
			}
			
			// clear progress bar
			clearProgress();
			
			$('.img-tool-container a .fa-undo').click(function(){
				degree = degree-90;
				$('#img1').rotate(degree);
			});
			$('.img-tool-container a .fa-repeat').click(function(){
				degree = degree+90;
				$('#img1').rotate(degree);
			});
			
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		clearProgress();
	});
});
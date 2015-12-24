/**
 * Copyright 2015 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var uuid = $.url().segment(2);
var params = $.url().param();
var tokenValue = $.url().param('token');
var queryString = $.url().attr('query');

$(document).ready(function() {
	
	$.get('/view/' + uuid + '.json?start=1&size=5', params, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			document.title = data.name;
			
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				var isViewable = page.viewable;
				var opStr = '';
				if (isViewable) {
					var encodedLocalViewPath = encodeURIComponent('file:///' + page.path);
					var viewUrl = '/view/url?url=' + encodedLocalViewPath;
					if (!!tokenValue) {
						viewUrl = viewUrl + '&token=' + tokenValue;
					}
					opStr = '<a href="' + viewUrl + '" target="_blank">预览</a>';
				}
				var trStr = '<tr><td>' + page.title + '</td><td>' + opStr + '</td></tr>';
				$('.span12 .table-zip-files tbody').append(trStr);
			}
			
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
	});
});
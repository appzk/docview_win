/**
 * Copyright 2015 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var id = $.url().segment(2);
var queryStr = $.url().attr('query');
var uuid = id;
var params = $.url().param();
var tokenValue = $.url().param('token');

$(document).ready(function() {
	
	$.get('/view/' + uuid + '.json?start=1&size=5', params, function(data, status) {
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
				var isViewable = page.viewable;
				var opStr = '';
				var downCheck = $.cookie('IDOCV_THD_VIEW_CHECK_DOWN_' + uuid);
				if (!downCheck || '0' != downCheck) {
					opStr += '<a href="' + page.url + '" target="_blank">下载</a>';
				}
				if (isViewable) {
					var encodedLocalViewPath = encodeURIComponent('file:///' + page.path);
					queryStr = queryStr + '&';
					if (queryStr.indexOf('url=') > -1) {
						queryStr = queryStr.replace(/(url=).*?(&)/, '$1' + encodedLocalViewPath + '$2');
					} else {
						queryStr = 'url=' + encodedLocalViewPath + '&' + queryStr;
					}
					queryStr = queryStr.replace(/(&+)$/, '');
					var viewUrl = '/view/url?' + queryStr;
					opStr += '<a style="margin-left: 20px;" href="' + viewUrl + '" target="_blank">预览</a>';
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
/**
 * Copyright 2015 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var queryStr = $.url().attr('query');

$(document).ready(function() {
	var uuid = $('.info-container-uuid').text();
	if (uuid) {
		var downCheck = $.cookie('IDOCV_THD_VIEW_CHECK_DOWN_' + uuid);
		if (!!downCheck && '0' == downCheck) {
			// $('.lnk-file-title').removeAttr('href');
		} else {
			$('.alert-error').append('<br />点击<a href="/doc/download/' + uuid + '?' + queryStr + '">下载</a>原始文件');
		}
	}
});
/**
 * Copyright 2015 I Doc View
 * 
 * @author Godwin <godwin668@gmail.com>
 */

var id = $.url().segment(2);
var uuid = id;
var sessionId = $.url().param('session');

$(document).ready(function() {
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.container-fluid:first .btn:first').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			
			var host = window.location.host;
			var pathname = window.location.pathname;
			var speakerUrl = 'http://' + host + pathname + '?type=speaker';
			var audienceUrl = 'http://' + host + pathname + '?type=audience';
			$('.qrcode-container-speaker').qrcode(speakerUrl);
			$('.qrcode-container-audience').qrcode(audienceUrl);
			$('.speaker-link-container').html('链接：' + speakerUrl + '<br />点击<a href="' + speakerUrl + '" target="_blank">进入</a>');
			$('.audience-link-container').html('链接：' + audienceUrl + '<br />点击<a href="' + audienceUrl + '" target="_blank">进入</a>');
		} else {
			$('.container-fluid .row-fluid').html('<section><div class="alert alert-error">' + data.desc + '</div></section>');
		}
		
		// clear progress bar
		clearProgress();
	});
});
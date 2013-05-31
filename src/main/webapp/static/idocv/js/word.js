$(document).ready(function() {
	$('body').simpleLoadingModal();
	var uuid = $.url().segment(2);
	var sessionId = $.url().param('session');
	﻿var address = 'http://api.idocv.com/view/' + uuid;
	
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			﻿// header
			/*
			$('.icon-qrcode').popover({
				title: '<a href="/doc/download?id=' + rid + '">下载原文件</a>',
				content: '<div id="qrcode"></div><script type="text/javascript">setTimeout(function() { $(".qrcode").qrcode("' + address + '"); }, 50);</script>',
				html: true,
				placement: 'bottom',
			});
			*/
			$(".qrcode").qrcode(address);
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.span12').append('<div class="word-page"><div class="word-content">' + page.content + '</div></div>');
			}
			
			if (document.createStyleSheet){
				document.createStyleSheet('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />');
			} else {
				$("head").append($('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />'));
			}
		} else {
			$('.span12').append('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// hide loader
		$("#loader").fadeOut();
		$("#dvGlobalMask").fadeOut();
	});
});
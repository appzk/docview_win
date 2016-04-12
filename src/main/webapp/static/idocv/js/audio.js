/**
 * Copyright 2015 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var totalSize = 1;
var id = $.url().segment(2);
var uuid = id;
var params = $.url().param();
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
			
			// pages
			// $('.span12').append('<div class="word-page"><div class="word-content"></div></div>');
			
			// clear progress bar
			clearProgress();
			var audio = pages[0];
			audioUrl = audio.url;

			$("#jquery_jplayer_1").jPlayer({
				ready: function () {
					$(this).jPlayer("setMedia", {
						title: data.name,
						mp3: audioUrl,
						m4a: audioUrl,
						oga: audioUrl
					});
				},
				cssSelectorAncestor: "#jp_container_1",
				swfPath: "/static/jplayer/js",
				supplied: "mp3, m4a, oga",
				useStateClassSkin: true,
				autoBlur: false,
				smoothPlayBar: true,
				keyEnabled: true,
				remainingDuration: true,
				toggleDuration: true
			});
			
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
	});
});
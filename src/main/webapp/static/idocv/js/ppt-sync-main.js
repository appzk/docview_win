/**
 * Copyright 2015 I Doc View
 * 
 * @author Godwin <godwin668@gmail.com>
 */

var uuid = $.url().segment(2);

$(document).ready(function() {
	var host = window.location.host;
	var pathname = window.location.pathname;
	$('.qrcode-container-speaker').qrcode("http://" + host + pathname + '?style=speaker');
	$('.qrcode-container-audience').qrcode("http://" + host + pathname + '?style=audiencenew');
});
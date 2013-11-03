/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

$(document).ready(function() {
	$.get("/version.json", function(data, status) {
		var version = data.version;
		$('.container-fluid .hero-unit').append('Version: ' + version);
	});
});
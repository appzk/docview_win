$(document).ready(function() {
	$.get("/version.json", function(data, status) {
		var version = data.version;
		$('.container-fluid .hero-unit').append('Version: ' + version);
	});
});
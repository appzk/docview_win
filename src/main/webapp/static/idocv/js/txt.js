$(document).ready(function() {
	$('body').simpleLoadingModal();
	var uuid = $.url().param('uuid');
	$.get('http://api.idocv.com/v/' + uuid + '.json', function(data, status) {
		var rid = data.rid;
		var uuid = data.uuid;
		var pages = data.data;
		
		// title
		$('.container-fluid .btn').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
		
		// pages
		for (i = 0; i < pages.length; i++) {
			var page = pages[i];
			$('.word-page').append('<div class="word-content">' + page.content + '</div>');
		}
		
		// hide loader
		$("#loader").fadeOut();
		$("#dvGlobalMask").fadeOut();
	});
});
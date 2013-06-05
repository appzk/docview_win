$(document).ready(function() {
	var uuid = $.url().segment(2);
	var sessionId = $.url().param('session');
	﻿var address = 'http://api.idocv.com/view/' + uuid;
	
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var url = data.url;
			
			var fp = new FlexPaperViewer(
				'/static/flex/FlexPaperViewer',
				'viewerPlaceHolder', { config : {
				SwfFile : escape('' + url + ''),
				Scale : 0.6, ZoomTransition : 'easeOut', ZoomTime : 0.5, ZoomInterval : 0.2, FitPageOnLoad : false, FitWidthOnLoad : true, PrintEnabled : true, FullScreenAsMaxWindow : false, ProgressiveLoading : false, MinZoomSize : 0.2, MaxZoomSize : 5, SearchMatchAll : false, InitViewMode : 'Portrait', ViewModeToolsVisible : true, ZoomToolsVisible : true, NavToolsVisible : true, CursorToolsVisible : true, SearchToolsVisible : true, localeChain: 'en_US'
			}});
			
		} else {
			$('.span12').append('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
	});
});
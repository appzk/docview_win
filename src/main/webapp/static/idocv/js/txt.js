var totalSize = 1;
$(document).ready(function() {
	var uuid = $.url().segment(2);
	var sessionId = $.url().param('session');
	﻿var address = 'http://api.idocv.com/view/' + uuid;
	
	$.get('/view/' + uuid + '.json?start=1&size=5', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			totalSize = data.totalSize;
			if (totalSize < 3) {
				$('.bottom-paging-progress').hide();
			}
			
			// title
			$('.container-fluid .btn').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			// $(".qrcode").qrcode(address);
	
			// pages
			$('.span12').append('<div class="word-page"><div class="word-content"><pre></pre></div></div>');
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.word-content pre').text($('.word-content pre').text() + page.content);
			}
			
			// bindBottomPagingProgress();
			
			// NEXT page link
			$('.span12').parent().append('<a id="next" href="/view/' + uuid + '.json?start=2&size=5"></a>');
		} else {
			$('.span12').append('<div class="alert alert-error">' + data.desc + '</div>');
		}

		// clear progress bar
		clearProgress();
		
		// infinite scroll
		var _renderItem = function(data) {
			// return '<div class="word-page"><div class="word-content">' + data.content + '</div></div>';
			return data.content;
		}
		$('.word-content pre').infinitescroll({
			// callback		: function () { console.log('using opts.callback'); },
			navSelector  	: "a#next:last",
			nextSelector 	: "a#next:last",
			itemSelector 	: ".word-content pre",
			loading: {
				finished: undefined,
		        finishedMsg: "<em>已到最底部！</em>",
		        img: '/static/loading/img/ajax-loader.gif',
		        msg: null,
		        msgText: "<em>正在加载...</em>"
            },
			debug		 	: true,
			dataType	 	: 'json',
			// behavior		: 'twitter',
			appendCallback	: false // USE FOR PREPENDING
			// pathParse     	: function( pathStr, nextPage ){ return pathStr.replace('2', nextPage ); }
		}, function( response ) {
			var code = response.code;
			if (code == 0) {
				$('.word-content pre').infinitescroll('destroy');
				return;
			}
			var jsonData = response.data;
			$theCntr = $(".word-content pre");
			var oldContent = $theCntr.text();
			oldContent = oldContent.substr(0, oldContent.length - 7);
			$theCntr.text(oldContent);
			var newElements = "";
			//var newItems = new Array();
			for(var i=0;i<jsonData.length;i++) {
				// var item = $(_renderItem(jsonData[i]));
				var content = jsonData[i].content;
				//item.css({ opacity: 0 });
				// $theCntr.append(item);
				$theCntr.text($theCntr.text() + '\n\n' + content);
				//newItems.push(item.attr('id'));
			}
			// bindBottomPagingProgress();
			//_addMasonryItem(newItems);
		});
	});
});
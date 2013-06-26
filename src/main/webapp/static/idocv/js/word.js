var totalSize = 1;
var uuid = $.url().segment(2);
var sessionId = $.url().param('session');
﻿var address = 'http://api.idocv.com/view/' + uuid;
$(document).ready(function() {
	
	$.get('/view/' + uuid + '.json?start=1&size=5', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			totalSize = data.totalSize;
			if (totalSize < 3) {
				$('.bottom-paging-progress').hide();
				$('.paging-bottom-all').hide();
			}
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			// $(".qrcode").qrcode(address);
			
			// pages
			// $('.span12').append('<div class="word-page"><div class="word-content"></div></div>');
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.span12 .word-page .word-content').append(page.content);
				// $('.span12').append('<div class="word-page"><div class="word-content">' + page.content + '</div></div>');
			}
			
			// clear progress bar
			clearProgress();
			bindBottomPagingProgress();
			
			// bottom paging positioning
			var onePagePercent = 100 / totalSize;
			var all = totalSize * onePagePercent;
			for (var i = 0; i < totalSize; i++) {
				$('.paging-bottom-all').append('<div class="paging-bottom-sub" page-num="' + (i + 1) + '" style="width: ' + onePagePercent + '%;">·</div>');
			}
			$(".paging-bottom-sub").click(function(){
				var id = $(this).attr('page-num');
				if (! $('#' + id).length) {
					// page NOT exist, load all page.
					loadAllPage();
				}
				$('html, body').animate({scrollTop:($('#' + id).position().top + 20)}, 'slow');
			});
			
			// NEXT page link
			$('.span12').parent().append('<a id="next" href="/view/' + uuid + '.json?start=2&size=5"></a>');
			
			if (document.createStyleSheet){
				document.createStyleSheet('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />');
			} else {
				$("head").append($('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />'));
			}
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
		
		// infinite scroll
		var _renderItem = function(data) {
			// return '<div class="word-page"><div class="word-content">' + data.content + '</div></div>';
			return data.content;
		}
		$('.word-content').infinitescroll({
			// callback		: function () { console.log('using opts.callback'); },
			navSelector  	: "a#next:last",
			nextSelector 	: "a#next:last",
			itemSelector 	: ".word-content",
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
				$('.word-content').infinitescroll('destroy');
				return;
			}
			var jsonData = response.data;
			$theCntr = $(".word-content");
			var newElements = "";
			//var newItems = new Array();
			for(var i=0;i<jsonData.length;i++) {
				var item = $(_renderItem(jsonData[i]));
				//item.css({ opacity: 0 });
				$theCntr.append(item);
				//newItems.push(item.attr('id'));
			}
			bindBottomPagingProgress();
			//_addMasonryItem(newItems);
		});
	});
});
/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var totalSize = 1;
var uuid = $.url().segment(2);
var sessionId = $.url().param('session');
$(document).ready(function() {
	
	$.ajax({
		type: "GET",
		url: '/view/' + uuid + '.json?start=1&size=0',
		data: {session:sessionId},
		async: false,
		dataType: "json"
	}).done(function( data ) {
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
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			
			// pages
			// $('.span12').append('<div class="word-page"><div class="word-content"></div></div>');
			
			// clear progress bar
			clearProgress();
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.span12 .word-page .word-content').append(page.content);
				// $('.span12').append('<div class="word-page"><div class="word-content">' + page.content + '</div></div>');
			}
			
			bindBottomPagingProgressBookmark();
			
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
			
			// bookmark
			var bookmarkPos = $.cookie('IDOCV_WORD_BOOKMARK_POS_' + uuid);
			if (bookmarkPos) {
				$('html, body').animate({scrollTop:(bookmarkPos + 'px')}, 'slow');
			}
			$(window).scroll(function() {
				var scrollPos = $(window).scrollTop();
				$('.info').text('scrollTop: ' + scrollPos);
				console.log('scrollTop: ' + scrollPos);
				$.cookie('IDOCV_WORD_BOOKMARK_POS_' + uuid, scrollPos, { expires: 30, path: '/' });
			});
			
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
	});
});

function bindBottomPagingProgressBookmark() {
	$('.scroll-page').each(function(i) {
		var position = $(this).position();
		$(this).scrollspy({
			min: position.top,
			max: position.top + $(this).height(),
			onEnter: function(element, position) {
				// if(console) console.log('entering ' +  element.id);
				// $("body").css('background-color', element.id);
				var percent = Math.round(element.id / totalSize * 100);
				// alert('percent: ' + percent);
				$('.bottom-paging-progress .bar').width('' + percent + '%');
				
				// bookmark
				$('.info').text('scroll pos: ' + element.id);
				// $.cookie('IDOCV_WORD_BOOKMARK_POS_' + uuid, element.id, { expires: 30, path: '/' });
			},
			onLeave: function(element, position) {
				// if(console) console.log('leaving ' +  element.id);
				//	$('body').css('background-color','#eee');
			}
		});
	});
}
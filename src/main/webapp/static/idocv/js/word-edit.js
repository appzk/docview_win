/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var totalSize = 1;
var uuid = $.url().segment(2);
var sessionId = $.url().param('session');
var version = $.url().param('v');
version = (version === undefined) ? -1 : version;

$(document).ready(function() {
	
	/*
	if ($.browser.msie && $.browser.version <= 8) {
		// IE 8 or lower
	} else {
		if(top !== window) {
			top.location.href = window.location.href;
		}
	}
	*/
	
	$.get('/edit/' + uuid + '.json?v=' + version, {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			var titles = data.titles;
			var versionCount = data.versionCount;
			totalSize = data.totalSize;
			if (totalSize < 3) {
				$('.bottom-paging-progress').hide();
				$('.paging-bottom-all').hide();
			}
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			document.title = data.name;
			
			// pages
			// $('.span12').append('<div class="word-page"><div class="word-content"></div></div>');
			
			// clear progress bar
			clearProgress();
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				$('.span12 .word-page .word-content').append(page.content);
				// $('.span12').append('<div class="word-page"><div class="word-content">' + page.content + '</div></div>');
			}
			
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
			
			// editor
			$('.span12 .word-page .word-content').attr("contenteditable", true);
			var editor = $('.span12 .word-page .word-content').ckeditor().editor;
			// console.log("content: " + editor.getData());
			/*
			editor.on('change', function( e ) {
				console.log('The editor named ' + e.editor.name + ' and the content is: ' + e.editor.getData());
			});
			*/
			editor.on('change', function( e ) {
				$('.save-btn-container').show('slow');
				// console.log('The editor named ' + e.editor.name + ' and the content is: ' + e.editor.getData());
			});
			// save button
			$('.save-btn-container .btn-save').click(function() {
				$.post('/edit/' + uuid + '/save', { body: editor.getData() }, function( data ) {
					// save data done!
				}, "json");
				$('.save-btn-container').hide('slow');
			});
			
			// version number
			if (!!versionCount && versionCount > 0) {
				// Dropdown tabs
				var dropDownMenu = '<li class="dropdown">' +
				'<a href="#" class="dropdown-toggle" data-toggle="dropdown">' +
				'版本' +
				'<b class="caret"></b>' +
				'</a>' +
				'<ul class="dropdown-menu">' +
				'<!-- DROP DOWN WORD TAB TITLE(s) HERE -->' +
				'</ul>' +
				'</li>';
				$('.word-tab-title').append(dropDownMenu);
				version = (version == -1) ? versionCount : version;
				$('.word-tab-title .dropdown .dropdown-menu').append('<li' + ((version == 0) ? ' class="active"' : '') + '><a href="/edit/' + uuid + '?v=' + 0 + '" data-toggle="tab">原始文档 </a></li>');
				for (i = versionCount; i > 0; i--) {
					$('.word-tab-title .dropdown .dropdown-menu').append('<li' + ((version == i) ? ' class="active"' : '') + '><a href="/edit/' + uuid + '?v=' + i + '" data-toggle="tab">第' + i + '个版本</a></li>');
				}
				var dropDownMenuHeight = $('.word-tab-title .dropdown-menu').height();
				var windowHeight = $(window).height();
				if (dropDownMenuHeight > (windowHeight - 80)) {
					$('.word-tab-title .dropdown-menu').height(windowHeight - 80);
					$('.word-tab-title .dropdown-menu').addClass('pre-scrollable');
				}
				$('.word-tab-title .dropdown .dropdown-menu a').click(function(){
					var vUrl = $(this).attr('href');
					window.location.href = vUrl;
				});
			}
			
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
	});
});
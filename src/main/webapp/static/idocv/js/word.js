/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var totalSize = 1;
var id = $.url().segment(2);
var uuid = id;
var params = $.url().param();
var type = $.url().param('type');
var isLoadAll = false;
var searchIndex = 0;

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
	
	$.get('/view/' + id + '.json?start=1&size=5', params, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			uuid = data.uuid;
			var pages = data.data;
			var titles = data.titles;
			totalSize = data.totalSize;
			if (totalSize < 3) {
				$('.bottom-paging-progress').hide();
				$('.paging-bottom-all').hide();
			}
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			document.title = data.name;
			
			// titles - navigation
			if (!!titles && titles.length > 3) {
				// Dropdown tabs
				var dropDownMenu = '<li class="dropdown word-tab-title-li">' +
					'<a href="#" class="dropdown-toggle" data-toggle="dropdown">' +
					'导航' +
					'<b class="caret"></b>' +
					'</a>' +
					'<ul class="dropdown-menu">' +
					'<!-- DROP DOWN WORD TAB TITLE(s) HERE -->' +
					'</ul>' +
					'</li>';
				$('.word-tab-title').append(dropDownMenu);
				for (i = 0; i < titles.length; i++) {
					var tt = titles[i];
					// tab navigation & tab content
					if (0 == i) {
						$('.word-tab-title .dropdown .dropdown-menu').append('<li><a href="#nav-title-' + i + '" data-toggle="tab">' + tt + '</a></li>');
					} else {
						$('.word-tab-title .dropdown .dropdown-menu').append('<li><a href="#nav-title-' + i + '" data-toggle="tab">' + tt + '</a></li>');
					}
				}
				var dropDownMenuHeight = $('.word-tab-title .dropdown-menu').height();
				var windowHeight = $(window).height();
				if (dropDownMenuHeight > (windowHeight - 80)) {
					$('.word-tab-title .dropdown-menu').height(windowHeight - 80);
					$('.word-tab-title .dropdown-menu').addClass('pre-scrollable');
				}
				
				$('.word-tab-title .dropdown .dropdown-menu a').click(function(){
					var id = $(this).attr('href');
					if (! $(id).length) {
						// page NOT exist, load all page.
						loadAllPage();
						isLoadAll = true;
					}
					$('html, body').animate({scrollTop:($(id).position().top + 20)}, 'slow');
				});
			}

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
					isLoadAll = true;
				}
				$('html, body').animate({scrollTop:($('#' + id).position().top + 20)}, 'slow');
			});
			
			// NEXT page link
			$('.span12').parent().append('<a id="next" href="/view/' + id + '.json?start=2&size=5&type=' + type + '"></a>');
			
			if (document.createStyleSheet){
				document.createStyleSheet('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />');
			} else {
				$("head").append($('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />'));
			}
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
		
		// search & highlight
		var hightlightHtml =
			'<!-- SEARCH & HIGHLIGHT -->' +
			'<form class="navbar-form pull-right hidden-desktop" onsubmit="return false;">' +
			  '<div class="nav-collapse collapse pull-right btn-search-toggle" style="margin-top: 10px;">&nbsp;&nbsp;<i class="icon-search icon-white"></i></div>' +
			  '<div class="input-append nav-search-container" style="display: none; float: right;">' +
			    '<input class="span2 input-search" id="appendedInputButtons" type="text" placeholder="搜索" />' +
			    '<span class="add-on"></span>' +
			    '<button class="btn btn-search-up" type="button"><i class="icon-chevron-up"></i></button>' +
			    '<button class="btn btn-search-down" type="button"><i class="icon-chevron-down"></i></button>' +
			  '</div>' +
			'</form>';
        $('.word-tab-title').after(hightlightHtml);
        $('.btn-search-toggle').click(function() {
        	$('.lnk-file-title').toggle('slow');
        	$('.word-tab-title-li').toggle('slow');
        	$('.nav-search-container').toggle('slow');
        	$('.input-search').val('');
        	$('.input-append .add-on').text('');
        	$('.highlight').removeClass('highlight-selected');
        	$('.highlight').removeClass('highlight');
		});
		$('.input-search').focus(function() {
			if(!isLoadAll) {
				loadAllPage(true);
			}
		});
		$('.input-search').on("change paste keyup", function() {
			var searchText = $('.input-search').val();
			$('.word-page').unhighlight();
			$('.word-page').highlight(searchText);
			var highLightCount = $('.highlight').length;
			if (highLightCount > 0) {
				searchIndex = 0;
				$('.input-append .add-on').text('1 / ' + highLightCount);
				$('.highlight').removeClass('highlight-selected');
				$('.highlight:eq(' + searchIndex + ')').addClass('highlight-selected');
				$('html, body').animate({scrollTop:($('.highlight:eq(' + searchIndex + ')').position().top)}, 'slow');
			} else {
				$('.input-append .add-on').text('');
			}
		});
		$('.btn-search-up').click(function(){
			if ($('.highlight').length < 1) {
				return;
			}
			searchIndex = searchIndex - 1;
			searchIndex = (-1 == searchIndex) ? (searchIndex + $('.highlight').length) : searchIndex;
			$('.input-append .add-on').text((searchIndex + 1) + ' / ' + $('.highlight').length);
			$('.highlight').removeClass('highlight-selected');
			$('.highlight:eq(' + searchIndex + ')').addClass('highlight-selected');
			$('html, body').animate({scrollTop:($('.highlight:eq(' + searchIndex + ')').position().top)}, 'slow');
		});
		$('.btn-search-down').click(function(){
			if ($('.highlight').length < 1) {
				return;
			}
			searchIndex = searchIndex + 1;
			searchIndex = ($('.highlight').length == searchIndex) ? 0 : searchIndex;
			$('.input-append .add-on').text((searchIndex + 1) + ' / ' + $('.highlight').length);
			$('.highlight').removeClass('highlight-selected');
			$('.highlight:eq(' + searchIndex + ')').addClass('highlight-selected');
			$('html, body').animate({scrollTop:($('.highlight:eq(' + searchIndex + ')').position().top)}, 'slow');
		});
		
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
			debug		 	: false,
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

function watermark() {
	var watermarkImg = 'http://data.idocv.com/idocv_logo.png';
	var watermarkText = '绝密文件';
	var step = 300;
    for (var i = 0; i < parseInt($(".word-content").height() / step); i++) {
        $('.word-content').append('<div style="width:100%;text-align:center;opacity:0.2;color:#000;position:absolute;top:' + step * (i + 1) + 'px;font-size:30px;transform:rotate(-30deg)">' + watermarkText + '<br /><img src="' + watermarkImg + '" /></div>');
    }
}
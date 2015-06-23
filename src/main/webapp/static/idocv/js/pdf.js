/**
 * Copyright 2015 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var uuid = $.url().segment(2);
var params = $.url().param();
var pages;
var curPage = 1;
var totalSize = 1;

$(document).ready(function() {
	
	$.get('/view/' + uuid + '.json?start=1&size=0', params, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			pages = data.data;
			totalSize = pages.length;
			if (pages.length < 3) {
				$('.bottom-paging-progress').hide();
				$('.paging-bottom-all').hide();
			}
			if (pages.length <= 1) {
				$('.btn-cmd-container').hide();
			}
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			
			if (!!data.styleUrl) {
				if (document.createStyleSheet){
					document.createStyleSheet('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />');
				} else {
					$("head").append($('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />'));
				}
			}
			
			$('.span12').html('<div id="page-container"></div>');
			
			gotoPage(1);
			
			for (i = 0; i < pages.length; i++) {
				$('.select-page-selector').append('<option>' + (i + 1) + '</option>');
			}
			
			// bottom paging positioning
			var onePagePercent = 100 / totalSize;
			var all = totalSize * onePagePercent;
			for (var i = 0; i < totalSize; i++) {
				$('.paging-bottom-all').append('<div class="paging-bottom-sub" page-num="' + (i + 1) + '" style="width: ' + onePagePercent + '%;">·</div>');
			}
			$(".paging-bottom-sub").click(function(){
				var id = $(this).attr('page-num');
				gotoPage(id);
			});
			$(".paging-bottom-sub").mouseover(function(){
				var id = $(this).attr('page-num');
				var curWidth = $(this).width();
				curWidth = curWidth < 30 ? 30 : curWidth;
				$(this).append('<div class="noti-page-number" style="position: absolute; bottom: 20px; width: ' + curWidth + 'px; height: 30px;"><font color="red">' + id + '</font></div>');
			});
			$('.paging-bottom-sub').bind('mouseup mouseleave touchend touchcancel', function(e) {
				e.preventDefault();
				$('.noti-page-number').remove();
			});
			
			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
	});
	
	// Send page turning command
	$('.btn-cmd').on('click touchstart', function(e) {
		e.preventDefault();
		if ($(this).attr('cmd-string')) {
			var cmdString = $(this).attr('cmd-string');
			var pageNum = 0;
			if ('left' == cmdString) {
				prePage();
			} else if ('right' == cmdString) {
				nextPage();
			}
		}
	});
	
	// page selector
	$('.select-page-selector').val(curPage);
	$('.select-page-selector').change(function() {
		var selectNum = $(".select-page-selector option:selected").text();
		gotoPage(selectNum);
	});
	
	// keyboard
	$(document).keydown(function(event){
		if (event.keyCode == 37 || event.keyCode == 38) {
			preSlideSync();
		} else if (event.keyCode == 39 || event.keyCode == 40 || event.keyCode == 32){
			nextSlideSync();
		} else if (event.keyCode == 13) {
			var isFullscreen = $(document).fullScreen() ? true : false;
			$('.img-container-sync').toggleFullScreen();
		}
	});
});


function prePage() {
	var prePage = eval(Number(curPage) - 1);
	gotoPage(prePage);
}

function nextPage() {
	var nextPage = eval(Number(curPage) + 1);
	gotoPage(nextPage);
}

function gotoPage(page) {
	var prePage = curPage;
	var pageSum = pages.length;
	if (page <= 0) {
		page = 1;
	} else if (pageSum < page) {
		page = pageSum;
	}
	curPage = page;
	
	$('#page-container').html(pages[page - 1].content);
	$('.select-page-selector').val(curPage);
	
	var percent = Math.round(page / pageSum * 100);
	$('.bottom-paging-progress .bar').width('' + percent + '%');
}
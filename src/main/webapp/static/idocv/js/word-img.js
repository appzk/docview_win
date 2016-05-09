/**
 * Copyright 2015 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var id = $.url().segment(2);
var uuid = id;
var params = $.url().param();
var pages;
var curPage = 1;
var totalSize = 1;
var lastImgWidth = 0;
var slideUrls = new Array();
var curSlide = 1;

$(document).ready(function() {
	
	$.get('/view/' + uuid + '.json?start=1&size=0', params, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			uuid = data.uuid;
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
			document.title = data.name;
			
			if (!!data.styleUrl) {
				if (document.createStyleSheet){
					document.createStyleSheet('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />');
				} else {
					$("head").append($('<link rel="stylesheet" href="' + data.styleUrl + '" type="text/css" />'));
				}
			}
			
			// Image Content
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				slideUrls[i] = page.url;
				if (i < 3) {
					$('.span12').append('<div class="pdf-page"><div id="' + (i + 1) + '" class="pdf-content img-container-sync scroll-page"><img class="slide-img-' + (i + 1) + '" alt="第' + (i + 1) + '页" src="' + page.url + '" rel="' + page.url + '"><br />' + (i + 1) + ' / ' + pages.length + '</div></div>');
				} else {
					$('.span12').append('<div class="pdf-page"><div id="' + (i + 1) + '" class="pdf-content img-container-sync scroll-page"><img class="slide-img-' + (i + 1) + '" alt="第' + (i + 1) + '页" src="" rel="' + page.url + '"><br />' + (i + 1) + ' / ' + pages.length + '</div></div>');
				}
				$('.select-page-selector').append('<option>' + (i + 1) + '</option>');
			}
			
			var lazyLoadImg = function() {
				var $ul = $('.span12');
				var $img = $ul.find("img[rel]");
				if (!$img || $img.length == 0) {
					return;
				}
				var $li = $ul.find('.pdf-content');
				var _wH = document.documentElement.clientHeight * 5;
				var _liH = $li[0].offsetHeight;
				var scrollTop = (document.documentElement && document.documentElement.scrollTop) || document.body.scrollTop;
				var curH = scrollTop;
				var allH = _wH + curH;
				var _liH = $li.get(0).offsetHeight;
				var curItem = Math.floor(curH / _liH);
				var allItem = Math.ceil(allH / _liH);
				var _max = $('.span12').find('.pdf-content').length;
				if (curItem > _max) return;
				if (allItem > _max) allItem = _max;
				for (var i = curItem - 1; i < allItem; i++) {
					var thum = $ul.find("img[rel]")[i];
					var rel = $(thum).attr("rel");
					if (rel && rel != $(thum).attr("src") && rel.indexOf("noimg") < 0 && rel.indexOf("invalid_pic") < 0) {
						$(thum).attr("src", rel);
					}
				}
			};
			
			// set all image size before image load
			$('.span12 .pdf-content img:first').load(function() {
				var imgWidth = $(this).width();
				var imgHeight = $(this).height();
				$('.pdf-content img').width(imgWidth);
				$('.pdf-content img').height(imgHeight);
				lazyLoadImg();
			});
			
			// lazyLoadImg();
			$(window).on("scroll", function(e) {
				lazyLoadImg();
			});
			
			// bottom paging positioning
			var onePagePercent = 100 / totalSize;
			var all = totalSize * onePagePercent;
			for (var i = 0; i < totalSize; i++) {
				$('.paging-bottom-all').append('<div class="paging-bottom-sub" page-num="' + (i + 1) + '" style="width: ' + onePagePercent + '%;">·</div>');
			}
			$('.paging-bottom-sub').on('click touchstart', function(e) {
				e.preventDefault();
				var id = $(this).attr('page-num');
				gotoPage(id);
			});
			$(".paging-bottom-sub").mouseover(function(){
				var id = $(this).attr('page-num');
				var curWidth = $(this).width();
				curWidth = curWidth < 30 ? 30 : curWidth;
				$(this).append('<div class="noti-page-number" style="position: absolute; bottom: 20px; width: ' + curWidth + 'px; height: 30px;"><font color="green">' + id + '</font></div>');
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
	
	// page selector
	$('.select-page-selector').val(curPage);
	$('.select-page-selector').change(function() {
		var selectNum = $(".select-page-selector option:selected").text();
		gotoPage(selectNum);
	});
	
	// keyboard
	$(document).keydown(function(event){
		if (event.keyCode == 37 || event.keyCode == 38) {
			prePage();
		} else if (event.keyCode == 39 || event.keyCode == 40 || event.keyCode == 32){
			nextPage();
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
	
	$('html, body').animate({scrollTop:($('.pdf-page:eq(' + (page - 1) + ')').position().top - 55)}, 'slow');
	
	$('.select-page-selector').val(curPage);
	
	var percent = Math.round(page / pageSum * 100);
	$('.bottom-paging-progress .bar').width('' + percent + '%');
	
	if (lastImgWidth > 0) {
		$('.pdf-page').width(lastImgWidth);
		$('.pdf-page img').width(lastImgWidth);
	}
}

function resetPageWidth(offset) {
	var curWidth = $('.pdf-page').width();
	var targetWidth = curWidth * (1 + offset);
	var windowWidth = $(window).width();
	$('.pdf-page').css("max-width", "none");
	if (targetWidth < (windowWidth * 5)) {
	//if (targetWidth < (windowWidth - 40)) {
		$('.pdf-page').width(targetWidth);
		$('.pdf-page img').width(targetWidth);
		lastImgWidth = targetWidth;
		gotoPage(curPage);
		return;
	}
}
/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var slideUrls = new Array();
var slideThumbUrls = new Array();
var curSlide = 1;
$(document).ready(function() {
	var uuid = $.url().segment(2);
	var sessionId = $.url().param('session');
	﻿var address = 'http://api.idocv.com/view/' + uuid;
	
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.container-fluid:first .btn:first').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			// $(".qrcode").qrcode(address);
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				slideUrls[i] = page.url;
				slideThumbUrls[i] = page.thumbUrl;
				$('.row-fluid .span2').append('<div class="thumbnail" page="' + (i + 1) + '"><img src="' + page.thumbUrl + '"></div>' + (i + 1) + '/' + pages.length + '<br />');
				$('#page-selector').append('<option>' + (i + 1) + '</option>');
			}
			
			$('.thumbnail').click(function () {
				var page_num = $(this).attr('page');
				gotoSlide(page_num);
				/*
				$('.slide-img img').fadeOut().attr("src", slideUrls[page_num - 1]).fadeIn();
				var percent = Math.ceil((page_num / slideUrls.length) * 100);
				$('.bottom-paging-progress .bar').width('' + percent + '%');
				curSlide = page_num;
				*/
			});
			
			$('.slide-img').html('<img src="' + pages[0].url + '" class="img-polaroid" style="height: 100%;">');
			
			afterLoad();
		} else {
			$('.container-fluid .row-fluid').html('<section><div class="alert alert-error">' + data.desc + '</div></section>');
		}
		
		clearProgress();
	});
	
	$('.fullscreen-link').toggle($(document).fullScreen() != null);
	$('.fullscreen-link').click(function(){
		$('.slide-img').fullScreen(true);
	});
	$(document).bind("fullscreenchange", function() {
		var isFullscreen = $(document).fullScreen() ? true : false;
		if (isFullscreen) {
			$('.slide-img').css('background-color', 'black');
		} else {
			$('.slide-img').css('background-color', '');
		}
	});
	
	$('#page-selector').change(function() {
		var selectNum = $("#page-selector option:selected").text();
		gotoSlide(selectNum);
	});
	
	// Swipe method is NOT supported in IE6, so it should be the last one.
	$('.slide-img').swipeleft(function() { nextSlide(); });
	$('.slide-img').swiperight(function() { preSlide(); });
});

$(window).resize(function() {
	resetImgSize();
});

function resetImgSize() {
	var leftW = $('.row-fluid .span2').width() + 20;
	var windowW = $(window).width();
	if (windowW < 768) {
		leftW = -40;
	}
	var ww = $(window).width() - 90 - leftW;
	var wh = $(window).height() - 90;
	var isFullScreen = $(document).fullScreen() ? true : false;
	if (isFullScreen) {
		ww = ww + 90 + leftW;
		wh = wh + 80;
	}
	if (ww / wh > 4 / 3) {
		$('.slide-img').height(wh);
		$('.slide-img').width(wh * 4 / 3);
	} else {
		$('.slide-img').width(ww);
		$('.slide-img').height(ww * 3 / 4);
	}
}

$(document).keydown(function(event){
	if (event.keyCode == 37 || event.keyCode == 38) {
		preSlide();
	} else if (event.keyCode == 39 || event.keyCode == 40 || event.keyCode == 32){
		nextSlide();
	} else if (event.keyCode == 13) {
		var isFullscreen = $(document).fullScreen() ? true : false;
		$('.slide-img').toggleFullScreen();
	}
});

function getCurSlide() {
	return curSlide;
}

function preSlide() {
	var preSlide = eval(Number(getCurSlide()) - 1);
	gotoSlide(preSlide);
}

function nextSlide() {
	var nextSlide = eval(Number(getCurSlide()) + 1);
	gotoSlide(nextSlide);
}

function gotoSlide(slide) {
	var slideSum = slideUrls.length;
	if (slide <= 0) {
		slide = 1;
	} else if (slideSum < slide) {
		slide = slideSum;
	}
	curSlide = slide;
	$(".slide-img img").fadeOut(function() {
		$(this).attr("src", slideUrls[slide - 1]).fadeIn();
	});
	var percent = Math.ceil((curSlide / slideUrls.length) * 100);
	$('#page-selector').val(slide);
	$('.bottom-paging-progress .bar').width('' + percent + '%');
}
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
	// ﻿var address = 'http://api.idocv.com/view/' + uuid;
	
	$.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.container-fluid .btn').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '">' + data.name + '</a>');
			// $(".qrcode").qrcode(address);
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				slideUrls[i] = page.url;
				slideThumbUrls[i] = page.thumbUrl;
				/*
				$('.row-fluid .span2').append('<div class="thumbnail" page="' + (i + 1) + '"><img src="' + page.thumbUrl + '"></div>' + (i + 1) + '/' + pages.length + '<br />');
				$('#page-selector').append('<option>' + (i + 1) + '</option>');
				*/
				$('.row-fluid .span2').append('<div class="thumbnail" page="' + (i + 1) + '"><img src="' + page.thumbUrl + '"></div>' + (i + 1) + '/' + pages.length + '<br />');
				$('#page-selector').append('<option>' + (i + 1) + '</option>');
				if (i == 0) {
					$('.carousel-indicators').append('<li data-target="#myCarousel" data-slide-to="' + i + '" class="active"></li>');
					$('.carousel-inner').append('<div class="active item"><img src="' + page.url + '" alt="" style="margin: 0 auto;"></div>');
				} else {
					$('.carousel-indicators').append('<li data-target="#myCarousel" data-slide-to="' + i + '"></li>');
					$('.carousel-inner').append('<div class="item"><img src="' + page.url + '" alt="" style="margin: 0 auto;"></div>');
				}
			}
			
			$('#myCarousel').carousel({
				interval: false
			});
			clearProgress();
			
			$('.thumbnail').click(function () {
				var page_num = $(this).attr('page');
				gotoSlide(page_num);
				/*
				$('.slider-img img').fadeOut().attr("src", slideUrls[page_num - 1]).fadeIn();
				var percent = Math.ceil((page_num / slideUrls.length) * 100);
				$('.bottom-paging-progress .bar').width('' + percent + '%');
				curSlide = page_num;
				*/
			});
			$('.carousel-indicators').toggle(false);
			$('.carousel-inner img').swipeleft(function() { nextSlide(); });
			$('.carousel-inner img').swiperight(function() { preSlide(); });
			
			// $('.slider-img').html('<img src="' + pages[0].url + '" class="img-polaroid" style="max-height: 100%;">');
			
			afterLoad();
		} else {
			$('.container-fluid .row-fluid').html('<section><div class="alert alert-error">' + data.desc + '</div></section>');
		}
		
		clearProgress();
	});
	
	$('.fullscreen-link').click(function(){
		$('.carousel-indicators').toggle(true);
		$('#myCarousel').fullScreen(true);
	});
	$('.fullscreen-link').toggle($(document).fullScreen() != null);
	$(document).bind("fullscreenchange", function() {
		var isFullscreen = $(document).fullScreen() ? true : false;
		$('.carousel-indicators').toggle(isFullscreen);
	});
	/*
	$(document).bind("fullscreenerror", function() {
	    alert("Browser rejected fullscreen change");
	});
	*/
	
	$('#page-selector').change(function() {
		var selectNum = $("#page-selector option:selected").text();
		gotoSlide(selectNum);
	});
});

$(window).resize(function() {
	resetImgSize();
});

function resetImgSize() {
	var ww = $(window).width() - 40;
	var wh = $(window).height() - 90;
	var isFullScreen = $(document).fullScreen() ? true : false;
	if (isFullScreen) {
		ww = ww + 40;
		wh = wh + 90;
	}
	// var fullscreenStatus = $('#myCarousel').fullScreen();
	// console.log('ww: ' + ww + ", wh: " + wh + ", full: " + fullscreenStatus + ", fullParam: " + JSON.stringify(fullScreen));
	if (ww / wh > 4 / 3) {
		$('.carousel-inner img').height(wh);
		$('.carousel-inner img').width(wh * 4 / 3);
	} else {
		$('.carousel-inner img').width(ww);
		$('.carousel-inner img').height(ww * 3 / 4);
	}
}
/*
function resetImgSize() {
	var ww = $(window).width();
	var wh = $(window).height();
	$('.slider-img').height(wh - 90);
}
*/

$(document).keydown(function(event){
	if(event.keyCode == 37 || event.keyCode == 38){
		preSlide();
	}else if (event.keyCode == 39 || event.keyCode == 40 || event.keyCode == 32){
		nextSlide();
	} else if (event.keyCode == 13) {
		$('#myCarousel').toggleFullScreen();
		var isFullscreen = $(document).fullScreen() ? true : false;
		$('.carousel-indicators').toggle(isFullscreen);
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
	$('#myCarousel').carousel(curSlide - 1);
	var percent = Math.ceil((curSlide / slideUrls.length) * 100);
	$('#page-selector').val(slide);
	$('.bottom-paging-progress .bar').width('' + percent + '%');
}
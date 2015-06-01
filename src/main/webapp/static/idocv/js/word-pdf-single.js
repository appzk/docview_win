/**
 * Copyright 2015 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var slideUrls = new Array();
var curSlide = 1;
var uuid = $.url().segment(2);
var sessionId = $.url().param('session');

$(document).ready(function() {
	
	// async method:
	// $.get('/view/' + uuid + '.json', {session:sessionId}, function(data, status) {
	$.ajax({
		type: "GET",
		url: '/view/' + uuid + '.json',
		async: false,
		dataType: "json",
		data: { session:sessionId },
	}).done(function( data ) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var uuid = data.uuid;
			var pages = data.data;
			
			// title
			$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand lnk-file-title" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');
			
			// pages
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				slideUrls[i] = page.url;
				$('.select-page-selector').append('<option>' + (i + 1) + '</option>');
				$('.select-page-selector-sync').append('<option>' + (i + 1) + '</option>');
			}
			
			$('.container-fluid .span12').html('<div class="pdf-page"><div class="pdf-content img-container-sync"><img class="slide-img-0" alt="第1页" src="' + slideUrls[0] + '"></div></div>');
			// resetImgSize();
			
			var percent = Math.ceil((curSlide / slideUrls.length) * 100);
			$('.select-page-selector').val(curSlide);
			$('.bottom-paging-progress .bar').width('' + percent + '%');

			afterLoad();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
		
		// clear progress bar
		clearProgress();
	});
	
	$('.select-page-selector').change(function() {
		var selectNum = $(".select-page-selector option:selected").text();
		gotoSlide(selectNum);
	});
	$('.btn-cmd-previous').click(function () {
		preSlide();
	});
	$('.btn-cmd-next').click(function () {
		nextSlide();
	});
});

$(document).keydown(function(event){
	if (event.keyCode == 37 || event.keyCode == 38) {
		preSlide();
	} else if (event.keyCode == 39 || event.keyCode == 40 || event.keyCode == 32){
		nextSlide();
	} else if (event.keyCode == 13) {
		var isFullscreen = $(document).fullScreen() ? true : false;
		$('.slide-img-container').toggleFullScreen();
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
	/*
	$(".slide-img-container img").fadeOut(function() {
		$(this).attr("src", slideUrls[slide - 1]).fadeIn();
	});
	*/
	$(".pdf-content img").attr("src", slideUrls[slide - 1]);
	var percent = Math.ceil((curSlide / slideUrls.length) * 100);
	$('.select-page-selector').val(slide);
	$('.select-page-selector-sync').val(slide);
	$('.bottom-paging-progress .bar').width('' + percent + '%');
}
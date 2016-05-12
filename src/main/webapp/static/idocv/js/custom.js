/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

$(document).ready(function() {

	/* ---------------------------------------------------------------------- */
	/*	Custom Functions
	/* ---------------------------------------------------------------------- */

	/*
	 * NOT work in IE 8
	if(top !== window) {
		top.location.href = window.location.href;
	}
	*/
	/*
	if ($.browser.msie && $.browser.version <= 8) {
		// IE 8 or lower
	} else {
		if(top !== window) {
			top.location.href = window.location.href;
		}
	}
	*/
	
	/* ---------------------------------------------------------------------- */
	/*	UItoTop (Back to Top)
	/* ---------------------------------------------------------------------- */

	(function() {

		var settings = {
				button      : '#back-to-top',
				text        : '返回顶部',
				min         : 200,
				fadeIn      : 400,
				fadeOut     : 400,
				scrollSpeed : 800
			},
			oldiOS     = false,
			oldAndroid = false;

		// Detect if older iOS device, which doesn't support fixed position
		if( /(iPhone|iPod|iPad)\sOS\s[0-4][_\d]+/i.test(navigator.userAgent) )
			oldiOS = true;

		// Detect if older Android device, which doesn't support fixed position
		if( /Android\s+([0-2][\.\d]+)/i.test(navigator.userAgent) )
			oldAndroid = true;
	
		$('body').append('<a href="#" id="' + settings.button.substring(1) + '" title="' + settings.text + '">' + settings.text + '</a>');

		$( settings.button ).click(function( e ){
				$('html, body').animate({ scrollTop : 0 }, settings.scrollSpeed );
				e.preventDefault();
			});

		$(window).scroll(function() {
			var position = $(window).scrollTop();
			if( oldiOS || oldAndroid ) {
				$( settings.button ).css({
					'position' : 'absolute',
					'top'      : position + $(window).height()
				});
			}

			if ( position > settings.min ) {
				$( settings.button ).fadeIn( settings.fadeIn );
			} else {
				$( settings.button ).fadeOut( settings.fadeOut );
			}
		});

	})();
	/* end UItoTop (Back to Top) */
});

function bindBottomPagingProgress() {
	$('.scroll-page').each(function(i) {
		var position = $(this).position();
		$(this).scrollspy({
			min: position.top - 20,
			max: position.top + $(this).height(),
			onEnter: function(element, position) {
				// if(console) console.log('entering ' +  element.id);
				// $("body").css('background-color', element.id);
				var percent = Math.round(element.id / totalSize * 100);
				// alert('percent: ' + percent);
				$('.bottom-paging-progress .bar').width('' + percent + '%');
			},
			onLeave: function(element, position) {
				// if(console) console.log('leaving ' +  element.id);
				//	$('body').css('background-color','#eee');
			}
		});
	});
}

function bindBottomPagingProgressImg() {
	$('.scroll-page').each(function(i) {
		var position = $(this).position();
		$(this).scrollspy({
			min: position.top - 60,
			max: position.top + $(this).height() - 100,
			onEnter: function(element, position) {
				// if(console) console.log('entering ' +  element.id);
				// $("body").css('background-color', element.id);
				var pos = element.id;
				var percent = Math.round(pos / totalSize * 100);
				// console.log('[onEnter>] percent: ' + percent + '(' + pos + '/' + totalSize + '), position: ' + JSON.stringify(position));
				$('.bottom-paging-progress .bar').width('' + percent + '%');
			},
			onLeave: function(element, position) {
				// if(console) console.log('leaving ' +  element.id);
				//	$('body').css('background-color','#eee');

				/*
				var pos = element.id - 1;
				var percent = Math.round(pos / totalSize * 100);
				console.log('[onLeave<] percent: ' + percent + '(' + pos + '/' + totalSize + '), position: ' + JSON.stringify(position));
				*/
			}
		});
	});
}

/**
 * Bind Anchor Scroll
 */
function bindAnchorScroll() {
	// anchor scroll
	$('a[href^=#]').click(function(e){
		var anchorHrefName = $(this).attr('href').substring(1);
		if (! anchorHrefName) {
			return;
		}
		e.preventDefault();
		gotoAnchor(anchorHrefName);
	});
}

/**
 * goto anchor OR id
 */
var isLoadAll = false;
function gotoAnchor(anchorNameOrId) {
	var anchorHrefName = anchorNameOrId;
	var isExist = false;
	if ($('a[name=' + anchorHrefName + ']').length) {
		isExist = true;
		setTimeout(function () {
			$('html, body').animate({scrollTop:($('a[name=' + anchorHrefName + ']').position().top + 20)}, 'slow');
		}, 300);

	} else if ($('#' + anchorHrefName).length) {
		isExist = true;
		setTimeout(function () {
			$('html, body').animate({scrollTop:($('#' + anchorHrefName).position().top + 20)}, 'slow');
		}, 300);
	}

	if (!isExist && !isLoadAll) {
		var showLoader = function () {
			$('.loader').show();
		}
		var hideLoader = function () {
			$('.loader').hide();
		}

		// page NOT exist, load all page.
		var queryStr
		try {
			queryStr = $.url().attr('query');
		} catch (e) {
		}
		try {
			$('.word-content').infinitescroll('destroy');
		} catch (e) {
		}

		showLoader();
		$.ajax({
			type: "GET",
			url: '/view/' + (!!id ? id : uuid) + '.json?start=1&size=0&' + queryStr,
			data: {},
			async: true,
			dataType: "json"
		}).done(function( data ) {
			var code = data.code;
			if (1 == code) {
				var rid = data.rid;
				var pages = data.data;

				// pages
				// $('.span12 .word-page .word-content').html();
				for (i = 0; i < pages.length; i++) {
					var page = pages[i];
					if (i == 0) {
						$('.span12 .word-page .word-content').html(page.content);
					} else {
						$('.span12 .word-page .word-content').append(page.content);
					}
				}

				isLoadAll = true;

				// goto anchor OR id
				gotoAnchor(anchorNameOrId);
				hideLoader();

				// bind anchor scroll event
				bindAnchorScroll();

				// bind bottom paging progress event
				bindBottomPagingProgress();
			} else {
				$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
			}
		});
	}
}

/* ---------------------------------------------------------------------- */
/*	Load All pages of WORD || TXT
/* ---------------------------------------------------------------------- */
function loadAllPage(isAsync) {
	var queryStr = $.url().attr('query');
	$('.word-content').infinitescroll('destroy');
	$.ajax({
		type: "GET",
		url: '/view/' + (!!id ? id : uuid) + '.json?start=1&size=0&' + queryStr,
		data: {},
		async: !!isAsync,
		dataType: "json"
	}).done(function( data ) {
		var code = data.code;
		if (1 == code) {
			var rid = data.rid;
			var pages = data.data;

			// pages
			// $('.span12 .word-page .word-content').html();
			for (i = 0; i < pages.length; i++) {
				var page = pages[i];
				if (i == 0) {
					$('.span12 .word-page .word-content').html(page.content);
				} else {
					$('.span12 .word-page .word-content').append(page.content);
				}
			}

			// anchor scroll
			$('a[href^=#]').click(function(e){
				var anchorHrefName = $(this).attr('href').substring(1);
				if (! anchorHrefName) {
					return;
				}
				e.preventDefault();
				$('html, body').animate({scrollTop:($('a[name=' + anchorHrefName + ']').position().top + 20)}, 'slow');
			});

			bindBottomPagingProgress();
		} else {
			$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
		}
	});
}

/* ---------------------------------------------------------------------- */
/*	Customized settings
/* ---------------------------------------------------------------------- */
function afterLoad() {
	/**
	 * Hide title under Weixin browser
	 */
	var ua = navigator.userAgent.toLowerCase();
    var isWeixinBrowser = (/micromessenger/.test(ua)) ? true : false ;
    // if (document.documentElement.clientWidth < 768) { // 根据屏幕宽度控制顶部黑色标题
    if (isWeixinBrowser) {
    	$('.lnk-file-title').remove();
    	
    	if ($('.word-tab-title-li').length) {
    		// only hide title
    	} else {
    		$('.word-body .navbar').hide();
    		$('.word-body').css('padding-top', '0px');
    	}
    	
    	$('.ppt-body .navbar').hide();
    	$('.ppt-body').css('padding-top', '20px');
    	
    	$('.pdf-body .navbar').hide();
    	$('.pdf-body').css('padding-top', '20px');
    	
    	$('.img-body .navbar').hide();
    	$('.img-body').css('padding-top', '20px');
    	
    	$('.audio-body .navbar').hide();
    	$('.audio-body').css('padding-top', '20px');
    	
    	$('.zip-body .navbar').hide();
    	$('.zip-body').css('padding-top', '20px');
    }
	
	/**
	 * Remove footer
	 */
	// $('footer').remove();
	
	/**
	 * do NOT allow copy text
	 */
	// $('body').css('-webkit-user-select', 'none');
	// $('body').css('-moz-user-select', 'none');
	// $('body').css('-ms-user-select', 'none');
	
	/**
	 * do NOT allow download
	 */
	// var ref = $('.lnk-file-title').attr('href');
	// $('.lnk-file-title').removeAttr('href');

	// diable drag image to elsewhere
	// window.ondragstart = window.ondrop = document.ondragstart = document.ondrop = function() { return false; }
	
	/**
	 * set filename from URL param
	 */
	try {
		var fileName = $.url().param('name');
		if (!!fileName) {
			$('.lnk-file-title').text(fileName);
		}
	} catch (e) {
	}
	
	/**
	 * Set title
	 */
	var name = $('.lnk-file-title').text();
	if (!! name) {
		document.title = name;
		// $('title').text(name);	IE 8 does NOT support this
	}

	/**
	 * disable right click (contextmenu)
	 */
	// × document.addEventListener("contextmenu", function(e){ e.preventDefault(); }, false);	// IE8或以下版本不支持addEventListener()
	// document.oncontextmenu = document.body.oncontextmenu = function() {return false;}
	
	/**
	 * view checker
	 */
	try {
		// read
		/* put these code at the end of word.js file
		var readCheck = $.cookie('IDOCV_THD_VIEW_CHECK_READ_' + uuid);
		if ((totalSize > 5) && !!readCheck && (readCheck > 0)) {
			$('.word-content').infinitescroll('destroy');
			$('.word-content').append('<br /><br /><div class="alert alert-info">试读结束，支付后阅读全文！</div>');
			$('.btn-search-toggle').hide();
			$('.word-tab-title').hide();
			$('.paging-bottom-all').hide();
		}
		*/
		// download
		var downCheck = $.cookie('IDOCV_THD_VIEW_CHECK_DOWN_' + uuid);
		if (!!downCheck && '0' == downCheck) {
			$('.lnk-file-title').removeAttr('href');
		}
		// copy
		var copyCheck = $.cookie('IDOCV_THD_VIEW_CHECK_COPY_' + uuid);
		if (!!copyCheck && '0' == copyCheck) {
			$('body').css('-webkit-user-select', 'none');
			$('body').css('-khtml-user-select', 'none');
			$('body').css('-moz-user-select', 'none');
			$('body').css('-ms-user-select', 'none');
			$('body').css('user-select', 'none');
			$('body').on("selectstart", function(e) { e.preventDefault(); });
		}
	} catch (e) {
	}
	
	// watermark
	var isWatermark = false;
	try {
		var watermarkFunct = function() {
			if (!isWatermark) {
				return;
			}
			var watermarkImg = 'http://data.idocv.com/idocv_logo.png';
			var watermarkText = '绝密文件';
			
			// info
			var infoCheck = $.cookie('IDOCV_THD_VIEW_CHECK_INFO_' + uuid);
			if (!!infoCheck) {
				watermarkText = decodeURI(infoCheck);
			}
			
			var watermarkContainer = $('.span12:visible').length > 0 ? $('.span12') : $('body');
			var step = 200;
		    for (var i = 0; i < parseInt(watermarkContainer.height() / step); i++) {
		    	watermarkContainer.append('<div style="width:100%;text-align:center;opacity:0.2;color:#000;position:absolute;top:' + step * (i + 1) + 'px;font-size:30px;transform:rotate(-30deg)">' + watermarkText + '<br /><img src="' + watermarkImg + '" /></div>');
		    }
		}
		
		// after load
		if ($('img').length > 0) {
			// run afterload after first image loaded
			$('img:first').load(function() {
				// first image loaded.
				watermarkFunct();
			});
		} else {
			watermarkFunct();
		}
	} catch (e) {
	}
	
	$('body').append('<div class="loader">加载中……</div>');
}
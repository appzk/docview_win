/**
 * Copyright 2013 I Doc View
 * 
 * @author Godwin <godwin668@gmail.com>
 */

var uuid = $.url().segment(3);
var sessionId = $.url().param('session');
var stampImg;
var pageWidth = 795;	// 595
var pageHeight = 1124;	// 842
var stampShow = 0;
var stage;
$(document).ready(
	function() {
		$.get('/view/' + uuid + '.json?start=1&size=5', {
			session : sessionId
		}, function(data, status) {
			var code = data.code;
			if (1 == code) {
				var rid = data.rid;
				// var uuid = data.uuid;
				var pages = data.data;

				// title
				$('.navbar-inner .container-fluid .btn-navbar').after('<a class="brand" style="text-decoration: none;" href="/doc/download/' + uuid + '" title="' + data.name + '">' + data.name + '</a>');

				for (i = 0; i < pages.length; i++) {
					var page = pages[i];
					$('.span12').append('<div class="pdf-page"><div class="pdf-content"><div id="container"></div><img alt="第' + (i + 1) + '页" src="' + page.url + '"></div></div>');
				}
			} else {
				$('.span12').html('<div class="alert alert-error">' + data.desc + '</div>');
			}

			// clear progress bar
			clearProgress();

			$('.btn-stamp-pdf-generator').click(function() {
				var xx = stampImg.getX();
				var yy = pageHeight - stampImg.getY();
				// var xPercent = Math.round(xx * 100 / pageWidth);
				// var yPercent = Math.round(yy * 100 / pageHeight);
				window.location.href='/stamp/pdf/' + uuid + '/down?' + ((1 === stampShow) ? 'stamp=d:/idocv-stamp.png&' : '') + 'x=' + xx + '&y=' + yy;
			});
			
			$("img").load(function(){
				initKenetic();
			});
			
			$('.slct-stamp-page').change(function() {
				var sv = $('.slct-stamp-page option:selected').val();
				if ('no' === sv) {
					$('canvas').hide();
					stampShow = 0;
				} else {
					$('canvas').show();
					stampShow = 1;
				}
			});
		});
	});

function initKenetic() {
	function drawImage(imageObj) {
		stage = new Kinetic.Stage({
			container : "container"
		});
		// stage.setVisible(false);
		
		pageWidth = $('#container').width();
		pageHeight = $('#container').height();
		stage.setSize(pageWidth, pageHeight);
		var layer = new Kinetic.Layer();

		// darth vader
		stampImg = new Kinetic.Image({
			image : imageObj,
			x : stage.getWidth() / 2 - 200 / 2,
			y : stage.getHeight() / 4 - 200 / 2,
			width : 156,
			height : 156,
			draggable : true
		});

		// add cursor styling
		stampImg.on('mouseover', function() {
			document.body.style.cursor = 'pointer';
		});
		stampImg.on('mouseout', function() {
			document.body.style.cursor = 'default';
		});
		stampImg.on('mousemove', function() {
			console.log("x=" + stampImg.getX() + ", y=" + stampImg.getY());
		});

		layer.add(stampImg);
		stage.add(layer);
		$('canvas').hide();
	}
	var imageObj = new Image();
	imageObj.onload = function() {
		drawImage(this);
	};
	imageObj.src = 'http://www.idocv.com/idocv-stamp.png';

	if (window.File && window.FileList && window.FileReader) {
		$('#fileupload').change(function() {
			var input = document.getElementById("fileupload");
			var fReader = new FileReader();
			fReader.readAsDataURL(input.files[0]);
			fReader.onloadend = function(event) {
				var pth = event.target.result;
				imageObj.src = pth;
			}
		});
	} else {
		alert("您的浏览器暂不支持文件选择功能，推荐使用Chrome浏览器重试！");
	}

	$('.btn-stamp-remove').click(function() {

	});
}
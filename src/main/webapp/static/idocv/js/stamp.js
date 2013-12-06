/**
 * Copyright 2013 I Doc View
 * 
 * @author Godwin <godwin668@gmail.com>
 */

var uuid = $.url().segment(2);
var sessionId = $.url().param('session');
$(document).ready(
	function() {
		$.get('/view/' + uuid + '.json?start=1&size=5', {
			session : sessionId
		}, function(data, status) {
			var code = data.code;
			if (1 == code) {
				var rid = data.rid;
				var uuid = data.uuid;
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

			initKenetic();
		});
	});

function initKenetic() {
	function drawImage(imageObj) {
		var stage = new Kinetic.Stage({
			container : "container",
			width : 600,
			height : 400
		});
		var layer = new Kinetic.Layer();

		// darth vader
		var darthVaderImg = new Kinetic.Image({
			image : imageObj,
			x : stage.getWidth() / 2 - 200 / 2,
			y : stage.getHeight() / 2 - 200 / 2,
			width : 200,
			height : 200,
			draggable : true
		});

		// add cursor styling
		darthVaderImg.on('mouseover', function() {
			document.body.style.cursor = 'pointer';
		});
		darthVaderImg.on('mouseout', function() {
			document.body.style.cursor = 'default';
		});
		darthVaderImg.on('mousemove', function() {
			console.log("x=" + darthVaderImg.getX() + ", y="
					+ darthVaderImg.getY());
		});

		layer.add(darthVaderImg);
		stage.add(layer);
	}
	var imageObj = new Image();
	imageObj.onload = function() {
		drawImage(this);
	};
	imageObj.src = 'http://www.idocv.com/idocv-stamp.png';

	if (window.File && window.FileList && window.FileReader) {
		$("input:file").change(function() {
			var input = document.getElementById("inputFile");
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
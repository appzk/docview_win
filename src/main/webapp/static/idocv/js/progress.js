/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

// Progress bar
$('.loading-mask').css("display", "");
var startSeconds;
var percent;
var convertInterval = setInterval(updateProgress, 100);
var remainderInterval;
function updateProgress() {
	if (startSeconds == undefined) {
		startSeconds = new Date().getTime() / 1000;
	}
	curSeconds = new Date().getTime() / 1000;
	percent = Math.floor( (1 - Math.pow(0.75, (curSeconds - startSeconds))) * 100 );
	
	$('.loading-mask .loading-zone .text').text("正在载入..." + percent + "%");
	$('.progress:first .bar').css("width", percent + "%");
	$('.progress:first .progress-bar').css("width", percent + "%"); // Bootstrap 3
}

function updateRemainderProgress() {
	if (percent == undefined || percent < 0) {
		percent = 0;
	}
	if (100 < percent) {
		percent = 100;
	}
	if (100 == percent) {
		clearInterval(remainderInterval);
		$('.loading-mask').delay(500).fadeOut(500);
	} else {
		percent = percent + 4;
		if (100 < percent) {
			percent = 100;
		}
		$('.loading-mask .loading-zone .text').text("正在载入..." + percent + "%");
		$('.progress:first .bar').css("width", percent + "%");
		$('.progress:first .progress-bar').css("width", percent + "%"); // Bootstrap 3
	}
}
	
// Clear Progress Bar
function clearProgress() {
	clearInterval(convertInterval);
	remainderInterval = setInterval(updateRemainderProgress, 30);
}
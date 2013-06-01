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
	percent = Math.floor( (1 - Math.pow(0.9, (curSeconds - startSeconds))) * 100 );
	
	$('.loading-mask .loading-zone .text').text("正在载入..." + percent + "%");
	$('.progress .bar').css("width", percent + "%");
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
		$('.loading-mask').delay(1000).fadeOut(500);
	} else {
		percent = percent + 5;
		if (100 < percent) {
			percent = 100;
		}
		$('.loading-mask .loading-zone .text').text("正在载入..." + percent + "%");
		$('.progress .bar').css("width", percent + "%");
	}
}
	
// Clear Progress Bar
function clearProgress() {
	clearInterval(convertInterval);
	remainderInterval = setInterval(updateRemainderProgress, 100);
}
/**
 * Copyright 2013 I Doc View
 * 
 * @author Godwin <godwin668@gmail.com>
 */

$(document).ready(function() {

	/* ---------------------------------------------------------------------- */
	/*
	 * Sidebar list /*
	 * ----------------------------------------------------------------------
	 */
	if (uid === undefined) {
		window.location = '/user/login';
	}
	
	// link
	$('.nav-doc-list').click(function () {
		if (username !== undefined && username !== '') {
			window.location = '/user/' + username + '/all';
		}
	});

	getInfo();
	
	setInterval(function() {
		getInfo();
	}, 3000);

	function getInfo() {
		$.get('/system/info.json', function(data, status) {
			var os = data.os;
			var cpuCount = data.cpuCount;
			var queueSize = data.queueSize;
			var highLoad = data.highLoad;
			
			var osMemUsed = data.osMemUsed;
			var osMemFree = data.osMemFree;
			var osMemTotal = data.osMemTotal;
			
			var memInit = data.memInit;
			var memUsed = data.memUsed;
			var memMax = data.memMax;
			var memRate = data.memRate;
			var uploadAvg = data.uploadAvg;
			$('.system-info-os').text(os);
			$('.system-info-cpu-cuont').text(cpuCount);
			$('.system-info-queue-size').text(queueSize);
			$('.system-info-high-load').text(highLoad ? '高负载' : '低负载');
			$('.system-info-os-mem-total').text(osMemTotal / 1024 + 'k');
			$('.system-info-os-mem-used').text(osMemUsed / 1024 + 'k');
			$('.system-info-os-mem-free').text(osMemFree / 1024 + 'k');
			$('.system-info-mem-init').text(memInit / 1024 + 'k');
			$('.system-info-mem-used').text(memUsed / 1024 + 'k');
			$('.system-info-mem-max').text(memMax / 1024 + 'k');
			$('.system-info-mem-rate').text(memRate);
			$('.system-info-upload-avg').text(uploadAvg);
			if (highLoad == true) {
				$('.system-info-high-load').addClass('alert-danger');
			} else {
				$('.system-info-high-load').removeClass('alert-danger');
			}
			if (memRate >= 0.8) {
				$('.system-info-mem-rate').addClass('alert-danger');
			} else {
				$('.system-info-mem-rate').removeClass('alert-danger');
			}
		});
	}

});
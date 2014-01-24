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
	
	var dps_heap_second = []; // dataPoints
	var dps_heap_minute = [];

	var chart_heap_second = new CanvasJS.Chart("chartContainer-heap-second",{
		title :{
			text: "堆内存使用率（秒）"
		},
		axisX: {
			suffix: " s",
			minimum: -60,
			maximum: 0,
		},
		axisY2:{
			minimum: 0,
			maximum: 100,
		},
		data: [{
			type: "spline",
			axisYType: "secondary",
			dataPoints: dps_heap_second
		},
		]
	});

	var chart_heap_minute = new CanvasJS.Chart("chartContainer-heap-minute",{
		theme:"theme1",
		title :{
			text: "堆内存使用率（分钟）"
		},
		axisX: {
			suffix: " m",
			minimum: -120,
			maximum: 1,
		},
		axisY:{
			minimum: 0,
			maximum: 100,
		},
		data: [{
			type: "spline",
			dataPoints: dps_heap_minute
		},
		]
	});

	var updateInterval = 5000;
	var dataLength_heap_second = 13; // number of dataPoints visible at any point
	var dataLength_heap_minute = 300; // number of dataPoints visible at any point

	var heap_minute_data_array = [];

	var updateChart_heap_second = function () {
		var info = getSystemInfo();
		var cpuCount = info.cpuCount;
		var queueSize = info.queueSize;
		var highLoad = info.highLoad;
		var memInit = info.memInit;
		var memUsed = info.memUsed;
		var memMax = info.memMax;
		var memRate = info.memRate;
		var uploadAvg = info.uploadAvg;

		chart_heap_second.options.axisY2.title = Math.floor((memInit / 1000)) + ' k ～ ' + Math.floor((memMax / 1000)) + ' k';
		for (var i = 0; i < dps_heap_second.length; i++) {
			dps_heap_second[i].x = dps_heap_second[i].x - 5;
		}
		dps_heap_second.push({
			x: 0,
			y: memRate * 100
		});
		if (dps_heap_second.length > dataLength_heap_second)
		{
			dps_heap_second.shift();
		}
		chart_heap_second.render();

		// heap memory minute chart
		heap_minute_data_array.push(memRate * 100);
		if (heap_minute_data_array.length >= 12) {
			var sum = 0;
			for (var i = 0; i < heap_minute_data_array.length; i++) {
				sum += heap_minute_data_array[i];
			}
			var avg = sum / heap_minute_data_array.length;
			heap_minute_data_array = [];

			for (var i = 0; i < dps_heap_minute.length; i++) {
				dps_heap_minute[i].x = dps_heap_minute[i].x - 1;
			}
			dps_heap_minute.push({
				x: -1,
				y: avg
			});
			if (dps_heap_minute.length > dataLength_heap_minute)
			{
				dps_heap_minute.shift();
			}
			chart_heap_minute.render();
		}

	};

	// generates first set of dataPoints
	updateChart_heap_second(); 
	chart_heap_minute.render();

	// update chart after specified time. 
	setInterval(function(){updateChart_heap_second()}, updateInterval);

	function getSystemInfo() {
		var jsonString = $.ajax({
			url: '/system/info.json',
			async: false
		}).responseText;
		return JSON.parse(jsonString);
	}
});
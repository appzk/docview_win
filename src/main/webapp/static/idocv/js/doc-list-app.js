/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

$(document).ready(function() {
	
	var app = 'eco';
	var label = $.url().segment(2);
	
	/* ---------------------------------------------------------------------- */
	/*	Sidebar list
	/* ---------------------------------------------------------------------- */
	$(function () {
		// get append labels
		$.get('/label/' + app + '.json', function(data, status) {
			var list = $('.sidebar-nav .nav-list');
			list.append('<li ' + (('all' == label || label === undefined) ? ' class="active"' : '') + '><a href="/open/all">全部</a></li>');
			list.append('<li ' + (('bilingual-weekly' == label || label === undefined) ? ' class="active"' : '') + '><a href="/open/bilingual-weekly">双语周刊</a></li>');
			/*
			for (var i = 0; i < data.length; i++) {
				list.append('<li ' + ((data[i].name == label) ? ' class="active"' : '') + '><a href="/app/' + data[i].name + '">' + data[i].value + '</a></li>');
			}
			*/
		});
	});
	
	/* ---------------------------------------------------------------------- */
	/*	Documenet Table
	/* ---------------------------------------------------------------------- */
	$(function () {
		var oTable = $('#doctable').dataTable( {
			// "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
			"sPaginationType": "bootstrap",
			"oLanguage": {
				"sLengthMenu": "_MENU_ records per page",
				"sEmptyTable": "没有可显示的数据！"
			},
			"iDisplayLength": 10,
			"bServerSide": true,
			"sAjaxSource": "/doc/list.json",
			"aoColumns": [
				{ "mData": "name", "sClass": "center " },
				{ "mData": "ctime", "sClass": "center" },
				{ "mData": "size", "sClass": "center" },
				{ "mData": "uuid", "sClass": "center" },
				{ "mData": "viewCount", "sClass": "center", "bSortable": false },
				{
				"mData": null,
				"sClass": "center",
				"bSortable": false,
				}
			],
			"aaSorting": [[1,'desc']],
			"fnRowCallback": function( nRow, aData, iDisplayIndex ) {
				var uuid = aData.uuid;
				$('td:eq(0)', nRow).html( '<a href="/view/'+aData.uuid+'" target="_blank">'+aData.name+'</a>' );
				$('td:eq(3)', nRow).html( '<a href="http://wev.cc/'+aData.uuid+'" target="_blank">wev.cc/' + aData.uuid + '</a>' );
				$('td:eq(4)', nRow).html( '' + aData.viewCount + '/' + aData.downloadCount + '' );
				$('td:eq(5)', nRow).html( '<a href="/view/'+aData.uuid+'" target="_blank" >预览</a> | <a href="/doc/download/'+aData.uuid+'">下载</a>' );
			},
			"fnServerParams": function ( aoData ) {
				aoData.push(
					{"name": "app", "value": app},
					{"name": "label", "value": label}
				);
			}
		});
	});
} );
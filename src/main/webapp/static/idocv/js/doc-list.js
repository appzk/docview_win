
$(document).ready(function() {
	
	var uid;
	var username;
	var sid;
	
	/* ---------------------------------------------------------------------- */
	/*	Check Login
	/* ---------------------------------------------------------------------- */
	$.get("/user/checkLogin", function(data) {
		username = data.username;
		uid = data.uid;
		initLabels();
		initTable();
	}, "json");
	
	/* ---------------------------------------------------------------------- */
	/*	File Upload
	/* ---------------------------------------------------------------------- */
	$(function () {
		// Setup any needed variables.
		var $ret  = $('#doc-view-fileupload-result'),
			$loader = '<img src="/static/smart/img/loader.gif" height="11" width="16" alt="Loading..." />';
		$ret.append('<div id="response">');
		var $response = $('#response');
	    $('#fileupload').fileupload({
	        dataType: 'json',
	        done: function (e, data) {
	        	// Hide any previous response text and show loader
	        	$response.hide().html( $loader ).show();
	        	var result = data.result;
	        	var uuid = result.uuid;
	        	$('#upload-result').fadeIn();
	        	if (uuid !== undefined) {
	        		// window.location.reload();
	        		$("#upload-result").html('<div class="alert alert-success" style="margin-bottom: 0px;">上传成功！</div>');
	        		oTable.fnDraw();
	        	} else {
	        		$("#upload-result").html('<div class="alert alert-error" style="margin-bottom: 0px;">上传失败：' + result.error + '</div>');
	        	}
	        	setTimeout(function() {
	      			$('#upload-result').fadeOut();
	      		}, 10000);
	        }
	    });
	    
	    $('#fileupload').fileupload({
	        /* ... */
	        progressall: function (e, data) {
	        	$('.upload-progress').css(
	        		'display',
	        		''
	        	);
	            var progress = parseInt(data.loaded / data.total * 100, 10);
	            $('.progress .bar').css(
	                'width',
	                progress + '%'
	            );
	            if (100 == progress) {
	            	$('.upload-progress').fadeOut();
	            };
	        }
	    });
	});
	
	/* ---------------------------------------------------------------------- */
	/*	Sidebar list
	/* ---------------------------------------------------------------------- */
	var label = $.url().segment(3);
	function initLabels() {
		// get append labels
		$.get('/label/' + uid + '.json', function(data, status) {
			if (label === undefined) {
				window.location = '/doc/list/all';
			}
			var list = $('.sidebar-nav .nav-list');
			list.append('<li class="nav-header"><i class="icon-user"></i> 我的文档</li>');
			list.append('<li ' + (('all' == label) ? ' class="active"' : '') + '><a href="/doc/list/all">全部</a></li>')
			for (var i = 0; i < data.length; i++) {
				list.append('<li ' + ((data[i].name == label) ? ' class="active"' : '') + '><a href="/doc/list/' + data[i].name + '">' + data[i].value + '</a></li>');
			}
			list.append('<li><a href="#">+</a></li>')
				.append('<li class="divider"></li>')
				.append('<li class="nav-header"><i class="icon-share-alt"></i> 其他文档</li>')
				.append('<li ' + (('shared' == label) ? ' class="active"' : '') + '><a href="/doc/list/shared">好友分享</a></li>')
				.append('<li ' + (('recommend' == label) ? ' class="active"' : '') + '><a href="/doc/list/recommend">推荐文档</a></li>')
			;
		});
	}
	
	/* ---------------------------------------------------------------------- */
	/*	Documenet Table
	/* ---------------------------------------------------------------------- */
	function initTable() {
		var oTable = $('#doctable').dataTable( {
			// "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
			"sPaginationType": "bootstrap",
			"oLanguage": {
				"sLengthMenu": "_MENU_ records per page"
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
			            	  if (uuid.charAt(uuid.length-1) == "w") {
			            		  $('td:eq(5)', nRow).html( '<a href="/doc/download/'+aData.uuid+'">下载</a> | <a href="/edit/'+aData.uuid+'" target="_blank" >协作编辑</a> | <a href="/doc/delete/'+aData.uuid+'" onclick="return confirm(\'确定要删除吗？\');" >删除</a>' );
			            	  } else {
			            		  $('td:eq(5)', nRow).html( '<a href="/doc/download/'+aData.uuid+'">下载</a> | <a href="/doc/delete/'+aData.uuid+'" onclick="return confirm(\'确定要删除吗？\');" >删除</a>' );
			            	  }
			              },
			              "fnServerParams": function ( aoData ) {
			            	  aoData.push(
			            			  {"name": "label", "value": label},
			            			  {"name": "uid", "value": uid}
			            	  );
			              }
		} );
	}
} );
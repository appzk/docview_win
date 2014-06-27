<%@page import="com.idocv.docview.Version"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Document List</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2014">
    <meta name="author" content="godwin668@gmail.com">
	
	<!-- Le styles -->
    <link href="/static/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="/static/idocv/css/style.css" rel="stylesheet">
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
	
	<style type="text/css" title="currentStyle">
		@import "/static/datatable/css/demo_page.css";
		@import "/static/datatable/css/demo_table.css";
		.bar {
		    height: 18px;
		    background: green;
		}
	</style>
</head>
<body>
	<h1>Upload Document - doc, docx, xls, xlsx, ppt, pptx, odt, ods, odp, pdf and txt</h1>
	<input id="fileupload" type="file" name="file" data-url="/doc/upload" multiple>
	<div id="progress">
	    <div class="bar" style="width: 0%;"></div>
	</div>
	
	<h1>Document List</h1>
	<table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered" id="doctable">
		<thead>
			<tr>
				<th width="25%">name</th>
				<th width="25%">rid</th>
				<th width="8%">uuid</th>
				<th width="5%">size</th>
				<th width="15%">ctime</th>
				<th width="4%">d/v</th>
				<th width="18%">option</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td colspan="5" class="dataTables_empty">Loading data from server...</td>
			</tr>
		</tbody>
	</table>
	
	<br />
	<h1>Url Preview</h1>
	<form action="/view/url">
		http://host:port/view/url?url=
		<input name="url" size="80" />
		&name=
		<input name="name" size="10" />
		<input type="reset" value=" Reset ">
		<input type="submit" value=" Preview ">
		<br />
		Note: Url should be encoded.
	</form>
	
	<center>
	<br /><br /><br />
	<% out.println("All rights reserved, version: " + Version.getVersion()); %>
	<br /><br />
	</center>
	<!-- Le javascript
    ================================================== -->
	<script src="/static/jquery/js/jquery-1.11.1.min.js"></script>
	<script src="/static/bootstrap/js/bootstrap.min.js"></script>
	<script src="/static/js/jquery.js"></script>
	<!-- 
	<script src="/static/js/jquery.dataTables.js"></script>
	 -->
	<script class="jsbin" src="http://datatables.net/download/build/jquery.dataTables.nightly.js"></script>
	<script class="jsbin" src="http://datatables.net/download/build/Scroller.js"></script>
	<script src="/static/js/upload/jquery.ui.widget.js"></script>
	<script src="/static/js/upload/jquery.iframe-transport.js"></script>
	<script src="/static/js/upload/jquery.fileupload.js"></script>
	<script>
		$(function () {
		    $('#fileupload').fileupload({
		        dataType: 'json',
		        done: function (e, data) {
		        	var result = data.result;
		        	var uuid = result.uuid;
		        	if (uuid !== undefined) {
		        		// alert("Success. uuid=" + uuid);
		        	} else {
		        		alert("Error! " + result.desc);
		        	}
		        	location.reload(true);
		        }
		    });
		    
			$('#fileupload').fileupload({
			    /* ... */
			    progressall: function (e, data) {
			        var progress = parseInt(data.loaded / data.total * 100, 10);
			        $('#progress .bar').css(
			            'width',
			            progress + '%'
			        );
			    }
			});
		});
		 
		$(document).ready(function() {
			$('#doctable').dataTable( {
		        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
		        "sPaginationType": "full_numbers",
		        "iDisplayLength": 25,
		        "bProcessing": true,
		        "bServerSide": true,
		        "sAjaxSource": "/doc/list",
		        "aoColumns": [
		            { "mData": "name", "sClass": "center" },
		            { "mData": "rid", "sClass": "center" },
		            { "mData": "uuid", "sClass": "center" },
		            { "mData": "size", "sClass": "center" },
		            { "mData": "ctime", "sClass": "center" },
		            { "mData": "viewCount", "sClass": "center" },
		            {
		                "mData": null,
		                "sClass": "center",
	// 	                "sDefaultContent": '<a href="/doc/delete">Delete</a> / <a href="/doc/view?id=">Preview</a>'
		            }
		        ],
				"fnRowCallback": function( nRow, aData, iDisplayIndex ) {
					$('td:eq(0)', nRow).html( '<a href="/view/'+aData.uuid+'" target="_blank">'+aData.name+'</a>' );
					$('td:eq(4)', nRow).html( '' + new Date(aData.ctime).getFullYear() + '-' + (new Date(aData.ctime).getMonth() + 1) + "-" + new Date(aData.ctime).getDate() + ' ' + new Date(aData.ctime).getHours() + ':' + new Date(aData.ctime).getMinutes() + ':' + new Date(aData.ctime).getSeconds() );
					$('td:eq(5)', nRow).html( '' + aData.downloadCount + '/' + aData.viewCount + '' );
					$('td:eq(6)', nRow).html( '<a href="/doc/download/'+aData.uuid+'">Download</a> | <a href="/doc/delete/'+aData.uuid+'" onclick="return confirm(\'Are you sure you want to delete?\');" >Delete</a> | <a href="/view/'+aData.uuid+'" target="_blank">View</a>' );
	            }
		    } );
			$.extend( $.fn.dataTableExt.oStdClasses, {
			    "sWrapper": "dataTables_wrapper form-inline"
			} );
		});
	</script>
</body>
</html>
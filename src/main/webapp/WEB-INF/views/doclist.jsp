<%@page import="com.idocv.docview.Version"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Document List</title>
<style type="text/css" title="currentStyle">
	@import "/static/datatable/css/demo_page.css";
	@import "/static/datatable/css/demo_table.css";
</style>
<script type="text/javascript" language="javascript" src="/static/js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="/static/js/jquery.dataTables.js"></script>
<script type="text/javascript" language="javascript" src="/static/js/upload/jquery.ui.widget.js"></script>
<script type="text/javascript" language="javascript" src="/static/js/upload/jquery.iframe-transport.js"></script>
<script type="text/javascript" language="javascript" src="/static/js/upload/jquery.fileupload.js"></script>
<script type="text/javascript" charset="utf-8">
	$(function () {
	    $('#fileupload').fileupload({
	        dataType: 'json',
	        done: function (e, data) {
	        	var result = data.result;
	        	var uuid = result.uuid;
	        	console.log(uuid);
	        	if (uuid !== undefined) {
	        		alert("Success. uuid=" + uuid);
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

	/* Time between each scrolling frame */
	$.fn.dataTableExt.oPagination.iTweenTime = 100;
	 
	$.fn.dataTableExt.oPagination.scrolling = {
	    "fnInit": function ( oSettings, nPaging, fnCallbackDraw )
	    {
	        /* Store the next and previous elements in the oSettings object as they can be very
	         * usful for automation - particularly testing
	         */
	        var nPrevious = document.createElement( 'div' );
	        var nNext = document.createElement( 'div' );
	         
	        if ( oSettings.sTableId !== '' )
	        {
	            nPaging.setAttribute( 'id', oSettings.sTableId+'_paginate' );
	            nPrevious.setAttribute( 'id', oSettings.sTableId+'_previous' );
	            nNext.setAttribute( 'id', oSettings.sTableId+'_next' );
	        }
	         
	        nPrevious.className = "paginate_disabled_previous";
	        nNext.className = "paginate_disabled_next";
	         
	        nPrevious.title = oSettings.oLanguage.oPaginate.sPrevious;
	        nNext.title = oSettings.oLanguage.oPaginate.sNext;
	         
	        nPaging.appendChild( nPrevious );
	        nPaging.appendChild( nNext );
	         
	        $(nPrevious).click( function() {
	            /* Disallow paging event during a current paging event */
	            if ( typeof oSettings.iPagingLoopStart != 'undefined' && oSettings.iPagingLoopStart != -1 )
	            {
	                return;
	            }
	             
	            oSettings.iPagingLoopStart = oSettings._iDisplayStart;
	            oSettings.iPagingEnd = oSettings._iDisplayStart - oSettings._iDisplayLength;
	             
	            /* Correct for underrun */
	            if ( oSettings.iPagingEnd < 0 )
	            {
	              oSettings.iPagingEnd = 0;
	            }
	             
	            var iTween = $.fn.dataTableExt.oPagination.iTweenTime;
	            var innerLoop = function () {
	                if ( oSettings.iPagingLoopStart > oSettings.iPagingEnd ) {
	                    oSettings.iPagingLoopStart--;
	                    oSettings._iDisplayStart = oSettings.iPagingLoopStart;
	                    fnCallbackDraw( oSettings );
	                    setTimeout( function() { innerLoop(); }, iTween );
	                } else {
	                    oSettings.iPagingLoopStart = -1;
	                }
	            };
	            innerLoop();
	        } );
	         
	        $(nNext).click( function() {
	            /* Disallow paging event during a current paging event */
	            if ( typeof oSettings.iPagingLoopStart != 'undefined' && oSettings.iPagingLoopStart != -1 )
	            {
	                return;
	            }
	             
	            oSettings.iPagingLoopStart = oSettings._iDisplayStart;
	             
	            /* Make sure we are not over running the display array */
	            if ( oSettings._iDisplayStart + oSettings._iDisplayLength < oSettings.fnRecordsDisplay() )
	            {
	                oSettings.iPagingEnd = oSettings._iDisplayStart + oSettings._iDisplayLength;
	            }
	             
	            var iTween = $.fn.dataTableExt.oPagination.iTweenTime;
	            var innerLoop = function () {
	                if ( oSettings.iPagingLoopStart < oSettings.iPagingEnd ) {
	                    oSettings.iPagingLoopStart++;
	                    oSettings._iDisplayStart = oSettings.iPagingLoopStart;
	                    fnCallbackDraw( oSettings );
	                    setTimeout( function() { innerLoop(); }, iTween );
	                } else {
	                    oSettings.iPagingLoopStart = -1;
	                }
	            };
	            innerLoop();
	        } );
	         
	        /* Take the brutal approach to cancelling text selection */
	        $(nPrevious).bind( 'selectstart', function () { return false; } );
	        $(nNext).bind( 'selectstart', function () { return false; } );
	    },
	     
	    "fnUpdate": function ( oSettings, fnCallbackDraw )
	    {
	        if ( !oSettings.aanFeatures.p )
	        {
	            return;
	        }
	         
	        /* Loop over each instance of the pager */
	        var an = oSettings.aanFeatures.p;
	        for ( var i=0, iLen=an.length ; i<iLen ; i++ )
	        {
	            if ( an[i].childNodes.length !== 0 )
	            {
	                an[i].childNodes[0].className =
	                    ( oSettings._iDisplayStart === 0 ) ?
	                    oSettings.oClasses.sPagePrevDisabled : oSettings.oClasses.sPagePrevEnabled;
	                 
	                an[i].childNodes[1].className =
	                    ( oSettings.fnDisplayEnd() == oSettings.fnRecordsDisplay() ) ?
	                    oSettings.oClasses.sPageNextDisabled : oSettings.oClasses.sPageNextEnabled;
	            }
	        }
	    }
	}
	 
	$(document).ready(function() {
		$('#doctable').dataTable( {
			"sPaginationType": "scrolling",
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
	            {
	                "mData": null,
	                "sClass": "center",
// 	                "sDefaultContent": '<a href="/doc/delete">Delete</a> / <a href="/doc/view?id=">Preview</a>'
	            }
	        ],
			"fnRowCallback": function( nRow, aData, iDisplayIndex ) {
				$('td:eq(0)', nRow).html( '<a href="/v/'+aData.uuid+'" target="_blank">'+aData.name+'</a>' );
				$('td:eq(4)', nRow).html( '' + new Date(aData.ctime * 1000).getFullYear() + '-' + (new Date(aData.ctime * 1000).getMonth() + 1) + "-" + new Date(aData.ctime * 1000).getDate() + ' ' + new Date(aData.ctime * 1000).getHours() + ':' + new Date(aData.ctime * 1000).getMinutes() + ':' + new Date(aData.ctime * 1000).getSeconds() );
				$('td:eq(5)', nRow).html( '<a href="/doc/download?id='+aData.uuid+'">Download</a> | <a href="/doc/delete?id='+aData.uuid+'" onclick="return confirm(\'Are you sure you want to delete?\');" >Delete</a> | <a href="/v/'+aData.uuid+'" target="_blank">View</a> | <a href="/view/sync/'+aData.rid+'.html" target="_blank">SyncView</a>' );
            }
	    } );
	});
</script>
<style type="text/css">
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
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="doctable" width="98%">
		<thead>
			<tr>
				<th width="25%">name</th>
				<th width="25%">rid</th>
				<th width="8%">uuid</th>
				<th width="5%">size</th>
				<th width="15%">ctime</th>
				<th width="22%">option</th>
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
</body>
</html>
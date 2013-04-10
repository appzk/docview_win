/* Table initialisation */
$(document).ready(function() {
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
	        	if (uuid !== undefined) {
	        		// window.location.reload();
	        		$("#upload-result").html('<div class="alert alert-success">Upload success!</div>');
	        		oTable.fnDraw();
	        	} else {
	        		$("#upload-result").html('<div class="alert alert-error">Upload error, error=' + result.error + '!</div>');
	        	}
	        }
	    });
	});
	
	/* ---------------------------------------------------------------------- */
	/*	Documenet Table
	/* ---------------------------------------------------------------------- */
	var oTable = $('#doctable').dataTable( {
        // "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
		"sPaginationType": "bootstrap",
		"oLanguage": {
			"sLengthMenu": "_MENU_ records per page"
		},
        "iDisplayLength": 10,
        "bServerSide": true,
        "sAjaxSource": "/doc/list",
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
			$('td:eq(0)', nRow).html( '<a href="/view/'+aData.uuid+'" target="_blank">'+aData.name+'</a>' );
			$('td:eq(3)', nRow).html( '<a href="http://wev.cc/'+aData.uuid+'" target="_blank">wev.cc/' + aData.uuid + '</a>' );
			$('td:eq(4)', nRow).html( '' + aData.viewCount + '/' + aData.downloadCount + '' );
			$('td:eq(5)', nRow).html( '<a href="/doc/download/'+aData.uuid+'">Download</a> | <a href="/doc/delete/'+aData.uuid+'" onclick="return confirm(\'Are you sure you want to delete?\');" >Delete</a>' );
        }
    } );
} );

/* Table initialisation */
/*
$(document).ready(function() {
	$('#doctable').dataTable( {
		"sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
		"sPaginationType": "bootstrap",
		"oLanguage": {
			"sLengthMenu": "_MENU_ records per page"
		}
	} );
} );
*/
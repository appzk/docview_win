/* Table initialisation */
$(document).ready(function() {
	$('#doctable').dataTable( {
        "sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
		"sPaginationType": "bootstrap",
		"oLanguage": {
			"sLengthMenu": "_MENU_ records per page"
		},
        "iDisplayLength": 25,
        "bProcessing": true,
        "bServerSide": true,
        "sAjaxSource": "/doc/list",
        "aoColumns": [
            { "mData": "name", "sClass": "center " },
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
			$('td:eq(3)', nRow).html( '' + new Date(aData.ctime).getFullYear() + '-' + (new Date(aData.ctime).getMonth() + 1) + "-" + new Date(aData.ctime).getDate() + ' ' + new Date(aData.ctime).getHours() + ':' + new Date(aData.ctime).getMinutes() + ':' + new Date(aData.ctime).getSeconds() );
			$('td:eq(4)', nRow).html( '' + aData.viewCount + '/' + aData.downloadCount + '' );
			$('td:eq(5)', nRow).html( '<a href="/doc/download/'+aData.uuid+'">Download</a> | <a href="/doc/delete/'+aData.uuid+'" onclick="return confirm(\'Are you sure you want to delete?\');" >Delete</a>' );
        }
    } );
	$.extend( $.fn.dataTableExt.oStdClasses, {
	    "sWrapper": "dataTables_wrapper form-inline"
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
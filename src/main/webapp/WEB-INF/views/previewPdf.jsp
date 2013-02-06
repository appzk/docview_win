<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<title>PDF Preview</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style type="text/css" media="screen">
		html, body	{ height:100%; }
		body { margin:0; padding:0; overflow:auto; }
		#flashContent { display:none; }
	</style>
	<script type="text/javascript" src="/static/flex/flexpaper_flash.js"></script>
	<link rel="stylesheet" rev="stylesheet" type="text/css" media="all" href="/static/preview/css/base/doc-preview.css" />
	</head>
<body>
	<div id="viewerPlaceHolder" style="width:100%;height:100%;"></div>
	<script type="text/javascript">
		var fp = new FlexPaperViewer(
		'/static/flex/FlexPaperViewer',
		'viewerPlaceHolder', { config : {
		SwfFile : escape('${url}'),
		Scale : 0.6, ZoomTransition : 'easeOut', ZoomTime : 0.5, ZoomInterval : 0.2, FitPageOnLoad : false, FitWidthOnLoad : true, PrintEnabled : true, FullScreenAsMaxWindow : false, ProgressiveLoading : false, MinZoomSize : 0.2, MaxZoomSize : 5, SearchMatchAll : false, InitViewMode : 'Portrait', ViewModeToolsVisible : true, ZoomToolsVisible : true, NavToolsVisible : true, CursorToolsVisible : true, SearchToolsVisible : true, localeChain: 'en_US'
		}});
	</script>
</body>
</html>
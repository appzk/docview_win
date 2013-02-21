/*
 * jQuery Simple Loading Modal Plugin
 * Version: 1.0
 *
 * Author: Chris Rivers
 * http://chrisriversdesign.com
 *
 *
 * Changelog: 
 * Version: 1.0
 *
 */

jQuery.fn.simpleLoadingModal = function ( options ) {
	
	var settings = { // Defaults
      'delay': 1000,
      'background' : '#FFFFFF',
	  'image' : '/static/loading/img/ajax-loader.gif',
	  'zIndex' : 99
    };
	
	var thisOb = this;
	
	return this.each(function() {        
		// If options exist, lets merge them with our default settings
		if ( options ) { 
			$.extend( settings, options );
		}
		
		function centerToBox( cur, box ) {
				
			// Simply Pass in the box that you would like your item centered too
			var position = $(box).position();
			
			var posTopOffset = ( $(box).height() ) /2;
			var posLeftOffset = ( $(box).width() ) /2;
			
			position.left = position.left + posLeftOffset;
			position.top = position.top + posTopOffset;
			
			$(cur).css({'position': 'absolute','left' : position.left, 'top' : position.top, "z-index" : 100 });
			
			return this;
		}
	
		function ajaxLoadList(){
			var a=thisOb.height();
			
			var b=thisOb.width();
			
			var position = thisOb.position();
			
			$("body").append("<div id=\"dvGlobalMask\"></div><div id=\"loader\"><img src=\"" + settings.image + "\"></div>");
			
			$("#dvGlobalMask").css({
				width:b,height:a,
				'background-color': settings.background,
				'position': 'absolute',
				'left' : position.left, 
				'top' : position.top, 
				'display' : 'inline-block',
				"z-index" : settings.zIndex
			}).fadeTo("fast",0.7);
			
			centerToBox($("#loader"), thisOb);
			$("#loader").show().delay(settings.delay).fadeOut();
			$("#dvGlobalMask").delay(settings.delay).fadeOut();
		}
		
		$(function(){
			ajaxLoadList();
		});
		
		return this;
		
	});
}
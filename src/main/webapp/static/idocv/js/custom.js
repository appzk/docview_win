// If JavaScript is enabled remove 'no-js' class and give 'js' class
jQuery('html').removeClass('no-js').addClass('js');

// When DOM is fully loaded
jQuery(document).ready(function($) {

	/* ---------------------------------------------------------------------- */
	/*	Custom Functions
	/* ---------------------------------------------------------------------- */

	
	/* ---------------------------------------------------------------------- */
	/*	UItoTop (Back to Top)
	/* ---------------------------------------------------------------------- */

	(function() {

		var settings = {
				button      : '#back-to-top',
				text        : '返回顶部',
				min         : 200,
				fadeIn      : 400,
				fadeOut     : 400,
				scrollSpeed : 800,
				easingType  : 'easeInOutExpo'
			},
			oldiOS     = false,
			oldAndroid = false;

		// Detect if older iOS device, which doesn't support fixed position
		if( /(iPhone|iPod|iPad)\sOS\s[0-4][_\d]+/i.test(navigator.userAgent) )
			oldiOS = true;

		// Detect if older Android device, which doesn't support fixed position
		if( /Android\s+([0-2][\.\d]+)/i.test(navigator.userAgent) )
			oldAndroid = true;
	
		$('body').append('<a href="#" id="' + settings.button.substring(1) + '" title="' + settings.text + '">' + settings.text + '</a>');

		$( settings.button ).click(function( e ){
				$('html, body').animate({ scrollTop : 0 }, settings.scrollSpeed, settings.easingType );

				e.preventDefault();
			});

		$(window).scroll(function() {
			var position = $(window).scrollTop();

			if( oldiOS || oldAndroid ) {
				$( settings.button ).css({
					'position' : 'absolute',
					'top'      : position + $(window).height()
				});
			}

			if ( position > settings.min ) 
				$( settings.button ).fadeIn( settings.fadeIn );
			else 
				$( settings.button ).fadeOut( settings.fadeOut );
		});

	})();

	/* end UItoTop (Back to Top) */

});

function bindBottomPagingProgress() {
	$('.scroll-page').each(function(i) {
		var position = $(this).position();
		console.log(position);
		console.log('min: ' + position.top + ' / max: ' + parseInt(position.top + $(this).height()));
		$(this).scrollspy({
			min: position.top,
			max: position.top + $(this).height(),
			onEnter: function(element, position) {
				if(console) console.log('entering ' +  element.id);
				// $("body").css('background-color', element.id);
				var percent = Math.round(element.id / totalSize * 100);
				// alert('percent: ' + percent);
				$('.bottom-paging-progress .bar').width('' + percent + '%');
			},
			onLeave: function(element, position) {
				if(console) console.log('leaving ' +  element.id);
				//	$('body').css('background-color','#eee');
			}
		});
	});
}
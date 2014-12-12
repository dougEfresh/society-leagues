/*
Bones Scripts File
Author: Jared Pereira

This file should contain any js scripts you want to add to the site.
Instead of calling it in the header or throwing it inside wp_head()
this file will be called automatically in the footer so as not to
slow the page load.

*/

// IE8 ployfill for GetComputed Style (for Responsive Script below)
if (!window.getComputedStyle) {
	window.getComputedStyle = function(el, pseudo) {
		this.el = el;
		this.getPropertyValue = function(prop) {
			var re = /(\-([a-z]){1})/g;
			if (prop == 'float') prop = 'styleFloat';
			if (re.test(prop)) {
				prop = prop.replace(re, function () {
					return arguments[2].toUpperCase();
				});
			}
			return el.currentStyle[prop] ? el.currentStyle[prop] : null;
		}
		return this;
	}
}

// as the page loads, call these scripts
 
jQuery(document).ready(function($) {
	
	/*
	Responsive jQuery is a tricky thing.
	There's a bunch of different ways to handle
	it, so be sure to research and find the one
	that works for you best.
	*/
	
	/* getting viewport width */
	var responsive_viewport = $(window).width();
	
	/* if is below 481px */
	if (responsive_viewport < 481) {
	
	} /* end smallest screen */
	
	/* if is larger than 481px */
	if (responsive_viewport > 481) {
	
	} /* end larger than 481px */
	
	/* if is above or equal to 768px */
	if (responsive_viewport >= 768) {
	
		/* load gravatars */
		$('.comment img[data-gravatar]').each(function(){
			$(this).attr('src',$(this).attr('data-gravatar'));
		});
		
		$('.showhide').css('display', 'block');
		
		
	}
	
	/* off the bat large screen actions */
	if (responsive_viewport > 1030) {
	
	}
var waitForFinalEvent = (function () {
  var timers = {};
  return function (callback, ms, uniqueId) {
    if (!uniqueId) {
      uniqueId = "Don't call this twice without a uniqueId";
    }
    if (timers[uniqueId]) {
      clearTimeout (timers[uniqueId]);
    }
    timers[uniqueId] = setTimeout(callback, ms);
  };
})();


$(window).resize(function () {
    waitForFinalEvent(function(){
    if ($( window ).width() >= 768) {
	  var hidden = $(".showhide").is(":hidden");
		if (hidden) {
			$('.showhide').css("display","block");
			document.getElementById('menuexpand').style.width = '100%';
			document.getElementById('menuexpand').style.borderLeft = '0px #fff solid';
			document.getElementById('menuexpand').style.borderRight = '0px #fff solid';
			document.getElementById('menuexpand').style.borderTop = '0px #fff solid'
			document.getElementById('menuexpand').style.margin = '5px 0px 0px 0px';
			document.getElementById('menu-main-navigation').style.borderBottom = 'none';
			$('#menuexpand a').text('SHOW MENU');
			};
      };
	   if ($( window ).width() < 750) {
		 var hidden = $(".showhide").is(":hidden");
			
			if (!hidden) {
		   		$('.showhide').css("display","none");
				
				document.getElementById('menuexpand').style.width = '100%';
				document.getElementById('menuexpand').style.borderLeft = '0px #fff solid';
				document.getElementById('menuexpand').style.borderRight = '0px #fff solid';
				$('#menuexpand a').text('SHOW MENU');
		  	 };
		   
		   };
    }, 0, "");
});
	
// add all your scripts here
	
	

//Set Menu Show/Hide Toggle
$( "#menuexpand" ).click(function() {
	
  $( ".showhide" ).toggle();
  
  var hidden = $(".showhide").is(":hidden");
		if (hidden) {
		document.getElementById('menuexpand').style.width = '100%';
		document.getElementById('menuexpand').style.borderLeft = '0px #fff solid';
		document.getElementById('menuexpand').style.borderRight = '0px #fff solid';
		document.getElementById('menuexpand').style.borderTop = '0px #fff solid'
		document.getElementById('menuexpand').style.margin = '5px 0px 0px 0px';
		document.getElementById('menu-main-navigation').style.borderBottom = '0px #fff solid';
		 $('#menuexpand a').text('SHOW MENU');	
			
		} else {
		document.getElementById('menuexpand').style.width = '25%';
		document.getElementById('menuexpand').style.borderLeft = '2px #fff solid';
		document.getElementById('menuexpand').style.borderRight = '2px #fff solid';
		document.getElementById('menuexpand').style.borderTop = '2px #fff solid';
		document.getElementById('menuexpand').style.margin = '0px 0px 0px 0px';
		document.getElementById('menu-main-navigation').style.borderBottom = '2px #fff solid';
		$('#menuexpand a').text('X');
		};
});
	
}); /* end of as page load scripts */


/*! A fix for the iOS orientationchange zoom bug.
 Script by @scottjehl, rebound by @wilto.
 MIT License.
*/
(function(w){
	// This fix addresses an iOS bug, so return early if the UA claims it's something else.
	if( !( /iPhone|iPad|iPod/.test( navigator.platform ) && navigator.userAgent.indexOf( "AppleWebKit" ) > -1 ) ){ return; }
	var doc = w.document;
	if( !doc.querySelector ){ return; }
	var meta = doc.querySelector( "meta[name=viewport]" ),
		initialContent = meta && meta.getAttribute( "content" ),
		disabledZoom = initialContent + ",maximum-scale=1",
		enabledZoom = initialContent + ",maximum-scale=10",
		enabled = true,
		x, y, z, aig;
	if( !meta ){ return; }
	function restoreZoom(){
		meta.setAttribute( "content", enabledZoom );
		enabled = true; }
	function disableZoom(){
		meta.setAttribute( "content", disabledZoom );
		enabled = false; }
	function checkTilt( e ){
		aig = e.accelerationIncludingGravity;
		x = Math.abs( aig.x );
		y = Math.abs( aig.y );
		z = Math.abs( aig.z );
		// If portrait orientation and in one of the danger zones
		if( !w.orientation && ( x > 7 || ( ( z > 6 && y < 8 || z < 8 && y > 6 ) && x > 5 ) ) ){
			if( enabled ){ disableZoom(); } }
		else if( !enabled ){ restoreZoom(); } }
	w.addEventListener( "orientationchange", restoreZoom, false );
	w.addEventListener( "devicemotion", checkTilt, false );
})( this );
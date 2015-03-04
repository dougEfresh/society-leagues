function resize_windows()
{
	if(navigator.userAgent.match(/iPad/i) != null)
	{
		$("#logo_container").css('display','none');
		$(".menu_item").css({'margin-left':"8px",'font-size':"18px"});
		$(".nav_item").css({'margin-left':"8px",'font-size':"16px"});
	}
	
	var wh = $(window).height();
	var fh = $("#footer").height();
	var hh = $("#header").height();
	
	$("#container").css('height', wh);
	
	$("#content_container").css('height', wh - fh - hh -1);
	$("#content_scroller").css('height', wh - fh - hh -1);	
	
	$("#nav_container").css('height', wh - fh - hh -1);
	$("#nav_scroller").css('height', wh - fh - hh -1);	
}

function email_is_valid(email)
{
	var emailRegex = new RegExp(/^([\w\.\-]+)@([\w\-]+)((\.(\w){2,3})+)$/i);
	var valid = emailRegex.test(email);
	
	if (!valid)
		return false;
	else
		return true;	
}

function hide_nav()
{
	$("#nav_container").css('display', 'none');
	$("#content_container").css('width', '980');
	$("#content_scroller").css('width', '996');
	$("#content").css('width', '980');
}

function show_nav()
{
	$("#nav_container").css('display', '');
	$("#content_container").css('width', '830');
	$("#content_scroller").css('width', '846');
	$("#content").css('width', '830');	
}

function quick_view(caller, ajax_call, vertical_pos, horizontal_pos)
{
	$.post(ajax_call, function(data)	
	{
		$("#quick_view_container").html(data);
		$("#quick_view").css('display', '');
		$("#quick_view").css('left', ($(caller).offset().left - $("#quick_view").width()));
		$("#quick_view").css('top', ($(caller).offset().top + 20));
	});
}
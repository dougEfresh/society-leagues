// JavaScript Document


function enter_division_results(match_id)
{
	$.post("admin/division/enter_results_dialog.php?match_id=" + match_id, function(data)	
	{
		$("#results_dialog").html(data); 
		$("#results_dialog").css('top', '100');
		$("#results_dialog").css('left', $("#main_container").offset().left + 15);
		$("#results_dialog").css('display', '');
	});	
}





function win_loss_toggle(the_select)
{
	if ($(the_select).val() == 'WIN')
	{
		$("#" + $(the_select).attr('other_select')).val('LOSS');
		$("#" + $(the_select).attr('this_score')).val('1');
		$("#" + $(the_select).attr('other_score')).val('0');
	}
	else
	{
		$("#" + $(the_select).attr('other_select')).val('WIN');
		$("#" + $(the_select).attr('this_score')).val('0');
		$("#" + $(the_select).attr('other_score')).val('1');		
	}
}





function delete_match(match_id, match_number)
{
	$.post("admin/division/delete_ind_result.php?match_id=" + match_id + "&match_number=" + match_number, function(data)	
	{
		console.log(data + 'matches left');
		$.post("admin/division/enter_results_dialog.php?match_id=" + match_id, function(data)	
		{
			$("#results_dialog").html(data); 
		});
	});
}




function modify_singles_game(the_obj)
{
	$("#hp1").val($(the_obj).attr('hp1'));
	$("#vp1").val($(the_obj).attr('vp1'));
	$("#hr1").val($(the_obj).attr('hr1'));
	$("#vr1").val($(the_obj).attr('vr1'));
	$("#hs1").val($(the_obj).attr('hs1'));
	$("#vs1").val($(the_obj).attr('vs1'));
	$("#rid").val($(the_obj).attr('rid'));
	$(".result_input").prop('disabled', false);
	$("#create_button").css('display', 'none');
	$("#update_button").css('display', '');
}





function modify_doubles_game(the_obj)
{
	$("#hp1").val($(the_obj).attr('hp1'));
	$("#hp2").val($(the_obj).attr('hp2'));
	$("#vp1").val($(the_obj).attr('vp1'));
	$("#vp2").val($(the_obj).attr('vp2'));
	$("#hr1").val($(the_obj).attr('hr1'));
	$("#vr1").val($(the_obj).attr('vr1'));
	$("#hs1").val($(the_obj).attr('hs1'));
	$("#vs1").val($(the_obj).attr('vs1'));
	$("#dbl").val($(the_obj).attr('dbl'));
	$("#rid").val($(the_obj).attr('rid'));
	$("#update_button").css('display', '');
	$(".result_input").prop('disabled', false);
	
	
	var mn = Number($(the_obj).attr('mnb'));
	
	if ( is_mixed_doubles(mn) )
		$(".can_hide").css('display', '');
	else
		$(".can_hide").css('display', 'none');
}




function is_mixed_doubles(mn)
{
	if ( (mn == 7) || (mn == 10) || (mn == 13) || (mn == 20) )
		return true;
	else
		return false;
}




function create_match(type, mn)
{
	$('.result_input').prop('disabled', false);
	
	if ((is_mixed_doubles(Number(mn) + 1)) && (type == 'MIXED'))
		$(".can_hide").css('display', '');
	else
		$(".can_hide").css('display', 'none');
		
	$(".insert_button").css('display', '');
	$(".update_button").css('display', 'none');
	$(".normal_button").css('display', 'none');
}




function cancel_create_match(type, mn)
{
	$('.result_input').prop('disabled', true);
	$(".can_hide").css('display', 'none');
	$(".insert_button").css('display', 'none');
	$(".update_button").css('display', 'none');
	$(".normal_button").css('display', '');
}




function update_singles_game(match_id)
{
	var arg = '';
	arg += "?hp1=" + $("#hp1").val();
	arg += "&vp1=" + $("#vp1").val();
	arg += "&hr1=" + $("#hr1").val();
	arg += "&vr1=" + $("#vr1").val();
	arg += "&dbl=" + $("#dbl").val();
	arg += "&rid=" + $("#rid").val();
	arg += "&hs1=" + $("#hs1").val();
	arg += "&vs1=" + $("#vs1").val();
	
	console.log(arg);	
	$.post("admin/division/update_ind_result.php" + arg, function(data)	
	{
		$.post("admin/division/enter_results_dialog.php?match_id=" + match_id, function(data)	
		{
			
			$("#results_dialog").html(data); 
		});	
	});
}




function update_doubles_game(match_id)
{
	var arg = '';
	arg += "?hp1=" + $("#hp1").val();
	arg += "&vp1=" + $("#vp1").val();
	arg += "&hr1=" + $("#hr1").val();
	arg += "&vr1=" + $("#vr1").val();
	arg += "&hp2=" + $("#hp2").val();
	arg += "&vp2=" + $("#vp2").val();
	arg += "&hs1=" + $("#hs1").val();
	arg += "&vs1=" + $("#vs1").val();
	
	arg += "&dbl=" + $("#dbl").val();
	arg += "&rid=" + $("#rid").val();
	
	$.post("admin/division/update_ind_result.php" + arg, function(data)	
	{
		$.post("admin/division/enter_results_dialog.php?match_id=" + match_id, function(data)	
		{
			$("#results_dialog").html(data); 
		});	
	});
}





function insert_game(match_id, mn, type)
{
	var arg = '';
	arg += "?hp1=" + $("#hp1").val();
	arg += "&vp1=" + $("#vp1").val();
	
	arg += "&hr1=" + $("#hr1").val();
	arg += "&vr1=" + $("#vr1").val();
	
	arg += "&hp2=" + $("#hp2").val();
	arg += "&vp2=" + $("#vp2").val();
	
	arg += "&hs1=" + $("#hs1").val();
	arg += "&vs1=" + $("#vs1").val();
	
	arg += "&htm=" + $("#htm").val();
	arg += "&vtm=" + $("#vtm").val();	
	
	arg += "&htr=" + $("#home_match_result").val();
	arg += "&vtr=" + $("#visit_match_result").val();	
	
	if ((is_mixed_doubles(Number(mn) + 1)) && (type == 'MIXED'))
		arg += "&dbl=" + "DOUBLES";
	else
		arg += "&dbl=" + "SINGLE";
		
	arg += "&match_id=" + match_id;
	arg += "&match_number=" + (Number(mn) + 1);
	
	console.log(arg);
	
	$.post("admin/division/insert_ind_result.php" + arg, function(data)	
	{
		console.log(data);
		$.post("admin/division/enter_results_dialog.php?match_id=" + match_id, function(data)	
		{
			$("#results_dialog").html(data); 
		});	
	});
}

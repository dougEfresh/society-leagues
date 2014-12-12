
function home_page()
{
	show_nav();
	
	$.post("home/home_nav.php", function(data)	
	{
		$("#nav").html(data);
		$("#nav_container").addClass("cloth");
		
		$.post("home/home.php", function(data)	
		{
			$("#content").html(data);
			resize_windows();
		});		
	});	
}

function home_load_team(team_id)
{
	if ($("#home_team_container_" + team_id).html() == '')
	{
		$.post("home/home_load_team.php?team_id=" + team_id, function(data)	
		{
			$("#home_team_container_" + team_id).html(data);
		});
	}
	else
	{
		$("#home_team_container_" + team_id).html('');
	}
}

function toggle_home_divisions()
{
	if($("#division_container").css('display') == 'none')
		$("#division_container").css('display', '');
	else
		$("#division_container").css('display', 'none');
}

function load_home_division_team_roster(team_id, division_id)
{
	$(".ts_selector").removeClass("tr_selected");
	$("#ts_" + team_id + division_id).addClass("tr_selected");
	
	$.post("home/team_roster.php?team_id=" + team_id + "&division_id=" + division_id, function(data)	
	{
		$("#team_roster").html(data);
		// WE DO THIS SINCE THE BUTTON NEEDS TO KNOW THE TEAM AND THE LIST IS
		// LOADED VIA AN AJAX CALL. THE TEAM IS KNOW WHEN CLICKED BUT THE PLAYER
		// BUTTON DOES NOT KNOW UNTIL WE WHISPER IN HIS EAR.
		$("#player_button").attr('team_id', team_id);
		$("#player_button").attr('division_id', division_id);
		// THERE! IT IS DONE
		$("#player_button").css('display', '');
	});	
}

function home_load_division(division_id)
{
	$(".ds_selector").removeClass("nav_active");
	$(".ds_selector").addClass("nav_inactive");
	
	$("#ds_" + division_id).removeClass("nav_inactive");
	$("#ds_" + division_id).addClass("nav_active");
	
	$.post("home/division.php?division_id=" + division_id, function(data)	
	{
		$("#content").html(data);
		load_home_schedule(division_id);
	});
}

function load_home_schedule(division_id)
{
	$.post("home/load_schedule.php?division_id=" + division_id, function(data)	
	{
		$("#schedule_container").html(data); 
		
		var week_count = $(".week_selector");
		
		for ( var week = 1; week <= week_count.length; week++ )
		{
			division_home_week(division_id, week);
		}
	});	
}

function division_home_week(division_id, week)
{
	$.post("home/division_week.php?division_id=" + division_id + "&week=" + week, function(data)	
	{
		$("#week_" + week).html(data); 
	});
}

function edit_account(player_id)
{
	$.post("home/account.php?player_id=" + player_id, function(data)	
	{
		$("#content").html(data); 
	});	
}

function toggle_match_detail(match_id)
{
	if ($("#match_d_" + match_id).css('display') == 'none')
		$("#match_d_" + match_id).css('display', '');
	else
		$("#match_d_" + match_id).css('display', 'none');
}

function history(player_id)
{
	$.post("home/history.php?player_id=" + player_id, function(data)	
	{
		$("#content").html(data); 
	});	
}

function load_player_division(player_id, division_id)
{
	$.post("admin/user/player_division_results.php?player_id=" + player_id + "&division_id=" + division_id, function(data)	
	{
		$("#my_team_container").html(data); 
	});	
}

function opponents(player_id)
{
	$.post("include/progress.php?message=Your opponents are being searched for.", function(data)	
	{
		$("#content").html(data); 
		
		$.post("home/opponents.php?player_id=" + player_id, function(data)	
		{
			$("#content").html(data); 
			history_tally();
		});
	});	
}

function my_team(team_id, division_id)
{
	$(".team_select").removeClass('nav_active').addClass('nav_inactive');
	$("#my_team_nav_" + team_id + '' + division_id).removeClass('nav_inactive').addClass('nav_active');
	
	$.post("home/my_team.php?team_id=" + team_id + "&division_id=" + division_id, function(data)	
	{
		$("#content").html(data); 
	});	
}

function update_account()
{
	var arg = "home/update_account.php?player_id=" + encodeURI($("#edit_player_id").val());
	
	arg += "&first_name="  + encodeURI($("#edit_first_name").val());
	arg += "&last_name="   + encodeURI($("#edit_last_name").val());
	arg += "&email="       + encodeURI($("#edit_email").val());
	arg += "&street1="     + encodeURI($("#edit_street1").val());
	arg += "&street2="     + encodeURI($("#edit_street2").val());
	arg += "&city="        + encodeURI($("#edit_city").val());
	arg += "&state="       + encodeURI($("#edit_state").val());
	arg += "&zip="         + encodeURI($("#edit_zip").val());
	arg += "&phone="       + encodeURI($("#edit_phone").val());
	
	player_id = $("#edit_player_id").val();
	
	if ($("#edit_password").val() != $("#edit_verify").val())
	{
		alert('The passwords do not match!');
		return;	
	}
	
	if ($("#edit_password").val() != $("#edit_password").attr('original'))
	{
		arg += "&password=" + encodeURI($("#edit_password").val());
	}
	
	$.post(arg, function(data)	
	{
		if (Number(data) > 0)
		{
			edit_account(player_id);		
		}
		else
			console.log(data);
	});
}


function history_tally()
{
	var matches = 0;
	var wins    = 0;
	var pct     = 0;
	var losses  = 0;
	
	$(".history_selector").each(
	function() 
	{ 
		if ($(this).css('display') != 'none')
		{
			matches++;		
			
			if($(this).attr('WIN') == 'WIN')
				wins++;
			else
				losses++;
		}
	});
	
	pct = wins / matches;
	pct = pct.toFixed(3);
		
	$("#history_matches").html(matches);
	$("#history_wins").html(wins);
	$("#history_losses").html(losses);
	$("#history_pct").html(pct);
}


function history_opp_opponent(opponent_id)
{
	reset_opponent_filter();
	
	$(".history_selector").each(
	function() 
	{ 
		if ($(this).attr('h_opponent') == opponent_id)
		{
			$("#history_opponent").html($(this).attr('h_opponent_name')); 
			$(this).css('display', '');
		}
		else
			$(this).css('display', 'none');	
	});
	
	history_tally();
	
	$("#history_reset_b").css('display', '');
	
	setTimeout('reset_opponent_filter();', 15000);
}

function reset_opponent_filter()
{
	$(".history_selector").css("display", ""); 
	
	$("#history_reset_b").css('display', 'none');
	
	$("#history_opponent").html("ALL"); 
	$("#history_team").html("ALL"); 
	$("#history_hcap").html("ALL");
	
	history_tally();
}

function history_opp_team(team_id)
{
	reset_opponent_filter();
	
	$(".history_selector").each(
	function() 
	{ 
		if ($(this).attr('h_team') == team_id)
		{
			$("#history_team").html($(this).attr('h_team_name')); 
			$(this).css('display', '');
		}
		else
			$(this).css('display', 'none');
	});
	
	history_tally();
	
	$("#history_reset_b").css('display', '');
	
	setTimeout('reset_opponent_filter();', 15000);
}

function history_opp_hcap(handicap)
{
	reset_opponent_filter();
	
	$(".history_selector").each(
	function() 
	{ 
		if ($(this).attr('hcap') == handicap)
			$(this).css('display', '');
		else
			$(this).css('display', 'none');
	});
	
	$("#history_hcap").html(handicap); 
	
	history_tally();
	
	$("#history_reset_b").css('display', '');
	
	setTimeout('reset_opponent_filter();', 15000);
}
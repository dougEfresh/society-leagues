// JavaScript Document
function admin_divisions()
{
	show_nav();
	
	$.post("admin/division/divisions.php", function(data)	
	{
		$("#nav").html(data);
		resize_windows();
		$(".nav_item")[1].click();
		//edit_division('58');
		//resize_windows();
		//$.post("admin/division/division_home.php", function(data)	
		//{
		//	$("#content").html(data);
		//	resize_windows();
		//});	
	});	
}




function add_playoffs(division_id)
{
	$.post("admin/division/add_playoffs.php?division_id=" + division_id, function(data)	
	{
		edit_division(division_id);
	});
}



function add_player_search(caller)
{
	$('input:radio[name=player_filter][value=all]').prop('checked', true);
	
	if ($(caller).val().length > 0)
	{
		var searcher = RegExp($(caller).val().toLowerCase());
		$('.player_select').css('display', 'none');
		
		$('.player_select').each(function()
		{
			if ( searcher.test( $(this).attr('player_name').toLowerCase() ) ) 
				$(this).css('display', '');
		});
	}
	else
	{
		$('.player_select').css('display', '');
	}
}

function substitute_week_matches(division_id, match_number)
{	
	$.post("admin/division/week_substitute.php?division_id=" + division_id + "&match_number=" + match_number, function(data)	
	{
		$("#week_substitute_dialog").html(data);
		$("#week_substitute_dialog").css('top', '100');
		$("#week_substitute_dialog").css('left', '100');
		$("#week_substitute_dialog").css('display', '');	
	});
}



function substitute_team(caller)
{
	$("[original_team=" + $(caller).val() + "]").each(function()
	{
		$(this).attr('original_team', $(this).val());
		$(this).val($(caller).attr('original_team'));
	});
	
	$(caller).attr('original_team', $(caller).val());
}



function apply_substitution(division_id, match_number)
{
	var matchups = Array();
	
	$(".substitute_h_team").each(function()
	{
		var other = $(".substitute_v_team[match_id=" + $(this).attr('match_id') + "]");
		var temp = $(this).attr('match_id') + ':' + $(this).val() + ':' + $(other[0]).val();
		matchups.push(temp);
	});
	var new_matchups = matchups.join('.');
	
	$.post("admin/division/modify_week.php?division_id=" + division_id + "&match_number=" + match_number + "&new_matchups=" + encodeURI(new_matchups), function(data)	
	{
		$("#week_substitute_dialog").css("display", "none");
		edit_division(division_id);
	});
}



function load_schedule(division_id)
{
	$.post("admin/division/load_schedule.php?division_id=" + division_id, function(data)	
	{
		$("#schedule_container").html(data); 
		
		var week_count = $(".week_selector");
		
		for ( var week = 1; week <= week_count.length; week++ )
		{
			division_week(division_id, week);
		}
	});	
}





function division_week(division_id, week)
{
	$.post("admin/division/division_week.php?division_id=" + division_id + "&week=" + week, function(data)	
	{
		$("#week_" + week).html(data); 
	});
}





function edit_division(division_id)
{
	$(".div_select").removeClass('nav_active').addClass('nav_inactive');
	
	$.post("admin/division/edit_division.php?division_id=" + division_id, function(data)	
	{
		$("#div_nav_" + division_id).removeClass('nav_inactive').addClass('nav_active');
		
		$("#content").html(data); 
		$("#edit_start_date").datepicker();
		$("#edit_end_date").datepicker();
		load_schedule(division_id);
	});	
}





function new_division()
{
	$.post("admin/division/new_division.php", function(data)	
	{
		$("#content").html(data);
	});	
}





function delete_division(division_id)
{
	$.post('admin/division/delete_division.php?division_id=' + division_id, function(data)	
	{
		if (Number(data) > 0)
		{
			$.post("admin/division/divisions.php", function(data)	
			{
				$("#nav").html(data); 
			});
		}
	});
}




function filter_divisions(the_obj)
{
	$.post("admin/division/division_filter.php?division_filter=" + encodeURI($(the_obj).val()), function(data)	
	{
		$.post("admin/division/divisions.php", function(data)	
		{
			$("#nav").html(data);
		});
	});	
}




function load_division_team_roster(team_id, division_id)
{
	$(".ts_selector").removeClass("tr_selected");
	$("#ts_" + team_id + division_id).addClass("tr_selected");
	
	$.post("admin/division/team_roster.php?team_id=" + team_id + "&division_id=" + division_id, function(data)	
	{
		$("#team_roster").html(data);
		// WE DO THIS SINCE THE BUTTON NEEDS TO KNOW THE TEAM AND THE LIST IS
		// LOADED VIA AN AJAX CALL. THE TEAM IS KNOW WHEN CLICKED BUT THE PLAYER
		// BUTTON DOES NOT KNOW UNTIL WE WHISPER IN HIS EAR.
		$("#player_button").attr('team_id', team_id);
		$("#player_button").attr('division_id', division_id);
		$("#captain_button").attr('team_id', team_id);
		$("#captain_button").attr('division_id', division_id);
		// THERE! IT IS DONE
		$("#player_button").css('display', '');
		$("#captain_button").css('display', '');

	});	
}




function new_division_dialog()
{
	$.post("admin/division/new_dialog.php", function(data)	
	{
		$("#new_division_dialog").html(data); 
		$("#new_start_date").datepicker();
		$("#new_end_date").datepicker();
		$("#new_division_dialog").css('top', '100');
		$("#new_division_dialog").css('left', '100');
		$("#new_division_dialog").css('display', '');	
	});	
}




function new_team_dialog(division_id, league_type)
{
	$.post("admin/division/team_dialog.php?division_id=" + division_id, function(data)	
	{
		$("#new_team_dialog").html(data); 
		$("#new_team_dialog").css('top', '100');
		$("#new_team_dialog").css('left', '100');
		$("#new_team_dialog").css('display', '');	
	});	
}




function new_team_toggle(the_obj)
{
	if ($(the_obj).parent().attr('id') == 'available_teams')
		$(the_obj).appendTo("#assigned_teams");
	else
		$(the_obj).appendTo("#available_teams");
}




function new_player_dialog(the_calling_button)
{
	// THIS WAS SET IN THE load_division_team_roster(team_id, division_id) FUNCTION
	var team_id     = $(the_calling_button).attr('team_id');
	var division_id = $(the_calling_button).attr('division_id');
	
	$.post("admin/division/player_dialog.php?team_id=" + team_id + "&division_id=" + division_id, function(data)	
	{
		$("#new_player_dialog").html(data); 
		$("#new_player_dialog").css('top', '100');
		$("#new_player_dialog").css('left', '100');
		$("#new_player_dialog").css('display', '');	
	});	
}

function new_captain_dialog(the_calling_button)
{
	// THIS WAS SET IN THE load_division_team_roster(team_id, division_id) FUNCTION
	var team_id     = $(the_calling_button).attr('team_id');
	var division_id = $(the_calling_button).attr('division_id');
	
	$.post("admin/division/captain_dialog.php?team_id=" + team_id + "&division_id=" + division_id, function(data)	
	{
		$("#new_captain_dialog").html(data); 
		$("#new_captain_dialog").css('top', '100');
		$("#new_captain_dialog").css('left', '100');
		$("#new_captain_dialog").css('display', '');	
	});	
}


function new_player_toggle(the_obj)
{
	if ($(the_obj).parent().attr('id') == 'available_players')
		$(the_obj).appendTo("#assigned_players");
	else
		$(the_obj).appendTo("#available_players");
}



function filter_new_players()
{
	var filter = $("input[name=player_filter]:checked").val();
	
	$("#player_search").val('');
	
	if (filter == 'all')
	{
		$("#available_players").children().each( function() { $(this).css('display', ''); });
	}
	else
	{
		$("#available_players").children().each( 
		function() 
		{ 
			if($(this).attr('player_status') == filter) 
				$(this).css('display', '');
			else
				$(this).css('display', 'none');
		});
	}
}



function add_players(team_id, division_id)
{	
	var players = Array();
	
	$("#assigned_players").children().each(function() 
	{ 
		players.push($(this).attr('player_id')); 
	});

	$.post("admin/division/add_players.php?players=" + encodeURI(players.join('.')) + "&team_id=" + team_id + "&division_id=" + division_id, function(data)	
	{
		$("#new_player_dialog").css("display", "none");
		load_division_team_roster(team_id, division_id);
	});	
}


function add_captain(team_id, division_id)
{
	var captain_id = $("#captain_id").val();

	$.post("admin/division/add_captain.php?team_id=" + team_id + '&captain_id=' + captain_id, function(data)	
	{
		$("#new_captain_dialog").css("display", "none");
		load_division_team_roster(team_id, division_id);
	});
}



function add_teams(division_id)
{
	var teams = Array();
	
	$("#assigned_teams").children().each(function() 
	{ 
		teams.push($(this).attr('team_id')); 
	});

	$.post("admin/division/add_teams.php?teams=" + encodeURI(teams.join('.')) + "&division_id=" + division_id, function(data)	
	{
		$("#new_team_dialog").css("display", "none");
		
		$.post("admin/division/generate_schedule.php?division_id=" + division_id, function(data)	
		{
			var result = data.split('*');
			
			if (result[0] == 'ERROR')
			{
				alert(result[1]);	
			}
			edit_division(division_id);
		});	
	});	
}



function add_individuals(division_id)
{
	var individuals = Array();
	
	$("#assigned_teams").children().each(function() 
	{ 
		individuals.push($(this).attr('player_id')); 
	});
	
	$.post("admin/division/add_individuals.php?individuals=" + encodeURI(individuals.join('.')) + "&division_id=" + division_id, function(data)	
	{
		$("#new_team_dialog").css("display", "none");
		
		$.post("admin/division/generate_schedule.php?division_id=" + division_id, function(data)	
		{
			var result = data.split('*');
			
			if (result[0] == 'ERROR')
			{
				alert(result[1]);	
			}
			
			edit_division(division_id);
		});			
	});	
}




function delete_division(division_id)
{
	$.post("admin/division/delete_division.php?division_id=" + division_id, function(data)	
	{
		if (Number(data) > 0)
		{
			admin_divisions();
		}
		else
			console.log(data);
	});
}



function update_division(division_id)
{
	if (!confirm("Updating this division will delete the current schedule but all teams and rosters will remain.\n\nDo you wish to proceed?"))
		return;
		
	var sd  = $.datepicker.formatDate('DD', $("#edit_start_date").datepicker('getDate'));
	var ed  = $.datepicker.formatDate('DD', $("#edit_end_date").datepicker('getDate'));
	var er  = 'The start & and dates have different week days\n';
	var arg = "admin/division/update_division.php";
	
	if (sd != ed)
	{
		alert(er + sd + ' does not match ' + ed);
		return;
	}
	
	arg += "?division_id=" + division_id;
	arg += "&start_date="  + encodeURI($("#edit_start_date").val());
	arg += "&end_date="    + encodeURI($("#edit_end_date").val());
	
	$.post(arg, function(data)	
	{
		if (Number(data) > 0)
		{
			edit_division(division_id);
		}
		else
			alert(data);
	});
}

function create_division()
{
	var sd  = $.datepicker.formatDate('DD', $("#new_start_date").datepicker('getDate'));
	var ed  = $.datepicker.formatDate('DD', $("#new_end_date").datepicker('getDate'));
	var er  = 'The start & and dates have different week days\n';
	var arg = "admin/division/create_division.php";
	
	if (sd != ed)
	{
		alert(er + sd + ' does not match ' + ed);
		return;
	}
	
	arg += "?league_id="  + encodeURI($("#new_league_id").val());
	arg += "&start_date=" + encodeURI($("#new_start_date").val());
	arg += "&end_date="   + encodeURI($("#new_end_date").val());
	arg += "&time_slot="  + encodeURI($("#new_time_slot").val());
	
	$.post(arg, function(data)	
	{
		if (Number(data) > 0)
		{
			var division_id = data;
			
			$("#new_division_dialog").css("display", "none");
			
			$.post("admin/division/divisions.php", function(data)	
			{
				$("#nav").html(data);
				edit_division(division_id);
			});				
		}
		else
			console.log(data);
	});	
}



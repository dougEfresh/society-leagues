// JavaScript Document
function admin_teams()
{
	show_nav();
	
	$.post("admin/team/teams.php", function(data)	
	{
		$("#nav").html(data);
		
		$.post("admin/team/team_home.php", function(data)	
		{
			$("#content").html(data);
			resize_windows()
		});	
	});	
}



function edit_team(team_id)
{
	$(".div_select").removeClass('nav_active').addClass('nav_inactive');
	
	$.post("admin/team/edit_team.php?team_id=" + team_id, function(data)	
	{
		$("#team_nav_" + team_id).removeClass('nav_inactive').addClass('nav_active');
		
		$("#content").html(data); 
		
		$.post("admin/team/previous_team_division.php?team_id=" + team_id, function(data)	
		{
			load_team_players(team_id, data);
		});	
	});	
}


function load_team_players(team_id, division_id)
{
	$(".tp_selector").removeClass("tr_selected");
	$("#tp_" + division_id).addClass("tr_selected");
	
	$.post("admin/team/team_players.php?team_id=" + team_id + "&division_id=" + division_id, function(data)	
	{
		$("#team_players").html(data); 
		load_team_history(team_id, division_id);
	});		
}



function load_player_history(player_id, team_id, division_id)
{
	$(".ph_selector").removeClass("tr_selected");
	$("#ph_" + player_id + division_id).addClass("tr_selected");
	
	$.post("admin/team/team_player_results.php?team_id=" + team_id + "&division_id=" + division_id + "&player_id=" + player_id, function(data)	
	{
		$("#team_results").html(data); 
	});	
}


function load_team_history(team_id, division_id)
{
	$.post("admin/team/team_division_results.php?team_id=" + team_id + "&division_id=" + division_id, function(data)	
	{
		$("#team_results").html(data); 
	});	
}


function update_team()
{
	var arg = "admin/team/update_team.php?team_id=" + encodeURI($("#edit_team_id").val());
	
	arg += "&league_id=" + encodeURI($("#edit_league_id").val());
	arg += "&status="    + encodeURI($("#edit_status").val());
	arg += "&name="      + encodeURI($("#edit_name").val());
	
	team_id = $("#edit_team_id").val();
	
	$.post(arg, function(data)	
	{
		if (Number(data) > 0)
		{
			$.post("admin/team/teams.php", function(data)	
			{
				$("#nav").html(data);
				edit_team(team_id);
			});			
		}
	});
}

function new_team()
{
	$.post("admin/team/new_team.php", function(data)	
	{
		$("#content").html(data);
	});	
}


function create_team()
{
	var arg = "admin/team/create_team.php";
	
	arg += "?name="      + encodeURI($("#name").val());
	arg += "&league_id=" + encodeURI($("#league_id").val());
	
	$.post(arg, function(data)	
	{
		if (Number(data) > 0)
		{
			var team_id = data;
			
			$.post("admin/team/teams.php", function(data)	
			{
				$("#nav").html(data);
				edit_team(teamp_id);
			});	
		}
		else
		{
			alert('The team could not be created.');
		}
	});
}

function delete_team(team_id)
{
	$.post('admin/team/delete_team.php?team_id=' + team_id, function(data)	
	{
		if (Number(data) > 0)
		{
			$.post("admin/team/teams.php", function(data)	
			{
				$("#nav").html(data); 
				$("#content").html("");
			});
		}
	});
}


function filter_teams(the_obj)
{
	$.post("admin/team/team_filter.php?team_filter=" + encodeURI($(the_obj).val()), function(data)	
	{
		$.post("admin/team/teams.php", function(data)	
		{
			$("#nav").html(data);
		});
	});	
}
// JavaScript Document

function admin_bca()
{
	show_nav();
	
	$.post("admin/bca_stats/divisions.php", function(data)	
	{
		$("#nav").html(data);
	
		$.post("admin/bca_stats/bca_stats_home.php", function(data)	
		{
			$("#content").html(data);
		
			resize_windows();
		});
	});	
}

function bca_stats(division_id)
{
	$.post("admin/bca_stats/bca_stats.php?division_id=" + division_id, function(data)	
	{
		$("#content").html(data);
	});
}
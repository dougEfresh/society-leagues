// JavaScript Document

function admin_database()
{
	show_nav();
	
	$.post("admin/database/database.php", function(data)	
	{
		$("#nav").html(data);
		
		$.post("admin/database/database_home.php", function(data)	
		{
			$("#content").html(data);
			resize_windows();
		});	
	});	
}


function database_user()
{
	$.post("admin/database/db_user.php", function(data)	
	{
		$("#content").html(data);
		
	});	
		
}

function database_season()
{
	$.post("admin/database/db_season.php", function(data)	
	{
		$("#content").html(data);
		
	});	
		
}
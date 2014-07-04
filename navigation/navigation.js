
function load_navigation()
{
	$.post("navigation/navigation.php", function(data)	
	{
		$("#menu").html(data);	
	});	
}

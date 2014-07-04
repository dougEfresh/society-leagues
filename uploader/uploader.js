function upload()
{
	$.post("uploader/uploader.php", function(data)	
	{
		hide_nav();
		$("#content").html(data);	
	});	
}	

<?php

include '../include/mysql.php';
include '../include/xtemplate.class.php';

$view = new xtemplate('ingest.html');

$unique = urldecode($_GET['unique']);
$filename = urldecode($_GET['file']);
$file = "../temp/" . $filename;

$view->assign('filename', $filename);
$view->assign('unique', $unique);

if (file_exists($file))
{
	$gallery_location = date('Y.m.d.h.m.') . rand(100,999);
	mkdir("../gallery/{$gallery_location}", 0777, true);
	mkdir("../gallery/{$gallery_location}/full", 0777, true);
	mkdir("../gallery/{$gallery_location}/thumb", 0777, true);
	
	rename("{$file}", "../gallery/{$gallery_location}/{$filename}");
	
	if (file_exists("../gallery/{$gallery_location}/{$filename}"))
	{
		// ---------------------------------------------------------------------
		// ---------------------------------------------------------------------
		$temp = explode('.', $filename);
		unset($temp[count($temp) - 1]);
		
		$original = "../gallery/{$gallery_location}/{$filename}";
		$preview_name = implode('.', $temp).'.jpg';
		$thumb = "../gallery/{$gallery_location}/thumb/".$preview_name;
		$preview = "../gallery/{$gallery_location}/full/".$preview_name;
		
		exec("convert -resize 128x128 \"{$original}\" \"{$thumb}\"");
		exec("convert -resize 512x512 \"{$original}\" \"{$preview}\"");
		// ---------------------------------------------------------------------
		// ---------------------------------------------------------------------
		
		mysql_query("INSERT INTO item SET i_path='{$gallery_location}', i_name='{$filename}', i_preview='{$preview_name}'");
		
		if (mysql_affected_rows() > 0)
		{
			$view->parse('main.ok');
		}
		else
		{
			unlink("../gallery/{$gallery_location}/{$filename}");
			rmdir("../gallery/{$gallery_location}");
			$view->parse('main.db_error');	
		}
	}
	else
	{
		rmdir("../gallery/{$gallery_location}");
		$view->parse('main.db_error');	
	}
}
else
{
	$view->parse('main.fail');
}

$view->parse('main');
$view->out('main');
die();
?>
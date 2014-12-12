<?php include 'system/login_status.php' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<meta name="apple-mobile-web-app-capable" content="yes">

<link rel="apple-touch-icon" sizes="152x152" href="images/touch-icon-ipad-retina.png">
<link rel="apple-touch-icon" sizes="76x76" href="/images/touch-icon-ipad.png">

<script language="javascript" type="text/javascript" src="include/jquery-1.9.0.js?v=1"></script>
<script language="javascript" type="text/javascript" src="include/jquery-ui.min.js?v=1"></script>
<script language="javascript" type="text/javascript" src="include/jquery-migrate-1.2.1.min.js?v=1"></script>

<!-- we need to keep this file remote to get the button thumbs -->
<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.9/themes/base/jquery-ui.css" type="text/css" />
<link rel="stylesheet" href="include/plupload-2.1/jquery.ui.plupload/css/jquery.ui.plupload.css" type="text/css" />
<script type="text/javascript" src="include/plupload-2.1/plupload.full.min.js?v=1"></script>
<script type="text/javascript" src="include/plupload-2.1/jquery.ui.plupload/jquery.ui.plupload.js?v=1"></script>

<script language="javascript" type="text/javascript" src="include/functions.js?v=1"></script>
<script language="javascript" type="text/javascript" src="admin/user/user.js?v=1"></script>
<script language="javascript" type="text/javascript" src="admin/team/team.js?v=1"></script>
<script language="javascript" type="text/javascript" src="admin/division/division.js?v=1"></script>
<script language="javascript" type="text/javascript" src="admin/database/database.js?v=1"></script>
<script language="javascript" type="text/javascript" src="admin/division/result.js?v=1"></script>
<script language="javascript" type="text/javascript" src="uploader/uploader.js?v=1"></script>
<script language="javascript" type="text/javascript" src="navigation/navigation.js?v=1"></script>
<script language="javascript" type="text/javascript" src="home/home.js?v=1"></script>
<script language="javascript" type="text/javascript" src="admin/bca_stats/bca_stats.js?v=1"></script>

<link href="index.css?v=1" rel="stylesheet" type="text/css">

<title>League System</title>

</head>

<body>

 <div id="main_container" class="container">
  
  <div class="header" id="header">
   <div id="logo_container">
    <img src="../images/header_logo.jpg?v=1" class="header_logo" onclick="home_page();" />
   </div>
   <div class="menu" id="menu"></div>
  </div>
    
  <div style="clear:both; height:0"></div>
  
  <div>
   <div class="nav_container" id="nav_container" style="display:none">
    <div class="nav_scroller" id="nav_scroller">
     <div class="nav" id="nav"></div>
    </div>
   </div>   
   
   <div class="content_container" id="content_container">
    <div class="content_scroller" id="content_scroller">
     <div id="content" class="content"></div>
    </div>
   </div>
  </div>
   
  <div class="footer" id="footer">
   <span style="position:relative; top:2px; float:left" onclick="location.reload();">www.societybilliards.com</span>
   <span style="position:relative; top:2px; float:left" id="browser_version"></span>
  </div>
 
 </div>

 <div id="quick_view" class="quick_view" style="display:none">
  <img src="images/x.png" class="button_16" height="24" 
  style="float:right;margin:2px" onclick='$("#quick_view").css("display","none");' />
  <div id="quick_view_container" style="padding:10px;"></div>
 </div>
</body>

<script language="javascript" type="text/javascript">
load_navigation();
home_page();
$(window).resize(function() { resize_windows(); });
resize_windows();
</script>
</html>

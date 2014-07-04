// JavaScript Document

function admin_users()
{
	show_nav();
	
	$.post("admin/user/users.php", function(data)	
	{
		$("#nav").html(data);
		
		$.post("admin/user/user_home.php", function(data)	
		{
			$("#content").html(data);
			resize_windows();
		});	
	});	
}

function edit_user(player_id)
{
	$(".div_select").removeClass('nav_active').addClass('nav_inactive');
	
	$.post("admin/user/edit_user.php?player_id=" + player_id, function(data)	
	{
		$("#player_nav_" + player_id).removeClass('nav_inactive').addClass('nav_active');
		
		$("#content").html(data); 
	});	
}

function update_user()
{
	var arg = "admin/user/update_user.php?player_id=" + encodeURI($("#edit_player_id").val());
	arg += "&player_login="            + encodeURI($("#edit_player_login").val());
	arg += "&first_name="              + encodeURI($("#edit_first_name").val());
	arg += "&last_name="               + encodeURI($("#edit_last_name").val());
	arg += "&email="                   + encodeURI($("#edit_email").val());
	arg += "&player_group="            + encodeURI($('input:radio[name=group]:checked').val());
	arg += "&street1="                 + encodeURI($("#edit_street1").val());
	arg += "&street2="                 + encodeURI($("#edit_street2").val());
	arg += "&city="                    + encodeURI($("#edit_city").val());
	arg += "&state="                   + encodeURI($("#edit_state").val());
	arg += "&zip="                     + encodeURI($("#edit_zip").val());
	arg += "&phone="                   + encodeURI($("#edit_phone").val());
	arg += "&handicap="                + encodeURI($("#edit_handicap").val());
	arg += "&handicap_eight="          + encodeURI($("#edit_handicap_eight").val());
	arg += "&handicap_straight="       + encodeURI($("#edit_handicap_straight").val());
	arg += "&handicap_mixed_8="        + encodeURI($("#edit_handicap_mixed_8").val());
	arg += "&handicap_mixed_9="        + encodeURI($("#edit_handicap_mixed_9").val());
	arg += "&handicap_eight_beginner=" + encodeURI($("#edit_handicap_eight_beginner").val());
	arg += "&handicap_10="             + encodeURI($("#edit_handicap_10").val());
	
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
		console.log(arg);
		if (Number(data) > 0)
		{
			$.post("admin/user/users.php", function(data)	
			{
				$("#nav").html(data);
				edit_user(player_id);
			});			
		}
		else
			console.log(data);
	});
}

function new_user()
{
	$.post("admin/user/new_user.php", function(data)	
	{
		$("#content").html(data);
	});	
}

function new_user_ok()
{
	var login = $("#new_player_login").val();
	var email = $("#new_email").val();
		
	var user_ok = 0;
	
	$.post("admin/user/new_user_unique.php?field=player_login&value=" + encodeURI(login), function(data)	
	{
		if(Number(data) == 0)
		{
			$("#new_login_status").attr('src', '../images/check.jpg');
			$('#new_login_text').html("OK");
			user_ok++;
		}
		else
		{
			$("#new_login_status").attr('src', '../images/x.jpg');
			
			if (Number(data) == 2)
				$('#new_login_text').html("This is a required field.");
			else
				$('#new_login_text').html("This login is already taken.");
		}
		
		$.post("admin/user/new_user_unique.php?field=email&value=" + encodeURI(email), function(data)	
		{
			var valid_email = email_is_valid(email);

			if(Number(data) == 0)
			{
				if (valid_email)
				{
					$("#new_email_status").attr('src', '../images/check.jpg');
					$('#new_email_text').html("OK");	
					user_ok++;
				}
				else
				{
					$("#new_email_status").attr('src', '../images/x.jpg');
					$('#new_email_text').html("This is not a properly formatted email address.");	
				}
			}
			else
			{
				$("#new_email_status").attr('src', '../images/x.jpg');
				
				if (Number(data) == 2)
					$('#new_email_text').html("This is a required field.");
				else
					$('#new_email_text').html("This email address is already taken.");
			}
					
			if ($("#new_password").val().length > 6)
			{
				user_ok++;
				$("#new_password_status").attr('src', '../images/check.jpg');
				$('#new_password_text').html("OK");	
			}
			else
			{
				$("#new_password_status").attr('src', '../images/x.jpg');
				$('#new_password_text').html("The password must be at least 6 characters in length.");
				$('#new_verify_text').html("");
			}
			
			if ( ($("#new_verify").val() == $("#new_password").val()) && ($("#new_password").val().length > 6) )
			{
				user_ok++;
				$("#new_verify_status").attr('src', '../images/check.jpg');
				$('#new_verify_text').html("OK");
			}
			else
			{
				$("#new_verify_status").attr('src', '../images/x.jpg');
				$('#new_verify_text').html("The passwords do not match.");	
			}
			
			console.log('ok=' + user_ok);
			
			if(user_ok >= 4)
				$("#new_user_b").css('display', '');
			else
				$("#new_user_b").css('display', 'none');	
		});
	});
}

function create_user()
{
	var arg = "admin/user/create_user.php";
	
	arg += "?player_login="  + encodeURI($("#new_player_login").val());
	arg += "&email="  + encodeURI($("#new_email").val());
	arg += "&first_name="  + encodeURI($("#new_first_name").val());
	arg += "&last_name="  + encodeURI($("#new_last_name").val());
	arg += "&password=" + encodeURI($("#new_password").val());
	
	$.post(arg, function(data)	
	{
		console.log('user:' + data);
		
		if (Number(data) > 0)
		{
			var player_id = data;
			
			$.post("admin/user/users.php", function(data)	
			{
				$("#nav").html(data);
				
				var arg = 'admin/user/user_email.php';
				
				arg += "?email="  + encodeURI($("#new_email").val());
				arg += "&player_login="  + encodeURI($("#new_player_login").val());
				arg += "&password=" + encodeURI($("#new_password").val());
				arg += "&function=welcome";
				
				$.post(arg, function(data) { });	
	
				edit_user(player_id);
			});	
		}
		else
		{
			alert('r=' + data);
			alert('The player could not be created.' + data);
		}
	});
}

function delete_user(player_id)
{
	$.post('admin/user/delete_user.php?player_id=' + player_id, function(data)	
	{
		if (Number(data) > 0)
		{
			admin_users();
		}
	});
}

function user_test_email(email)
{
	$.post("admin/user/user_email.php?email=" + encodeURI(email) + "&function=test", function(data)	
	{
		alert('A test email has been sent to ' + email);
	});	
}

function filter_users(the_obj)
{
	$.post("admin/user/user_filter.php?user_filter=" + encodeURI($(the_obj).val()), function(data)	
	{
		$.post("admin/user/users.php", function(data)	
		{
			$("#nav").html(data);
		});
	});	
}

function load_player_division_results(player_id, division_id)
{
	$(".uh_selector").removeClass("tr_selected");
	$("#uh_" + division_id).addClass("tr_selected");
	
	$.post("admin/user/player_division_results.php?player_id=" + player_id + "&division_id=" + division_id, function(data)	
	{
		$("#player_division_results").html(data);
	});		
}
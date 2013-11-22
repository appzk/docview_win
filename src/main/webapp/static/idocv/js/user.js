/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

var uid;
var username;
var sid;
var host = $.url().attr('host');

/* ---------------------------------------------------------------------- */
/*	Check Login
/* ---------------------------------------------------------------------- */
$.ajax({
	type: "GET",
	url: '/user/checkLogin.json',
	async: false,
	dataType: "json",
}).done(function( data ) {
	username = data.username;
	uid = data.uid;
	
	// Already logged in user, redirect to his(her) own document list.
	var label = $.url().segment(3);
	if (uid !== undefined) {
		// window.location = '/user/' + username + '/all';
	}
	if (username !== undefined) {
		// SUCCESS - is login
		var userHtml = 
		  '<div class="nav-collapse collapse">' +
            '<div class="pull-right">' +
              '<ul class="nav pull-right">' +
                '<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-user icon-white"></i> ' + username + ' <b class="caret"></b></a>' +
                  '<ul class="dropdown-menu">' +
                    '<li><a href="/user/preferences" style="text-decoration: none;"><i class="icon-cog"></i> Preferences</a></li>' +
                    '<li><a href="http://www.idocv.com" target="_blank" style="text-decoration: none;"><i class="icon-envelope"></i> Contact Support</a></li>' +
                    '<li class="divider"></li>' +
                    '<li><a id="button-logout" href="#" style="text-decoration: none;"><i class="icon-off"></i> Logout</a></li>' +
                  '</ul>' +
                '</li>' +
              '</ul>' +
            '</div>' +
         '</div><!-- user info end -->';
		$('.navbar-inner .container-fluid').append(userHtml);
	} else {
		// FAIL - NOT login
		var loginHtml = 
			'<!-- SIGN UP & SIGN IN -->' +
	          '<ul class="nav pull-right">' +
	          	'<li><a href="http://user.idocv.com/signup">注册</a></li>' +
	          	'<li class="divider-vertical"></li>' +
	            '<li class="dropdown">' +
	              '<a class="dropdown-toggle" href="#" data-toggle="dropdown">登录<strong class="caret"></strong></a>' +
	              '<div class="dropdown-menu" style="padding: 15px; padding-bottom: 0px;">' +
	                '<!-- Login form here -->' +
	                '<form id="form-signin" class="form-horizontal" action="" method="post" accept-charset="UTF-8">' +
	                  '<div id="sign-in-result"></div>' +
					  '<input id="login_username" placeholder="用户名" style="margin-bottom: 15px;" type="text" name="login_username" size="30" data-validation="validate_min_length length3" />' +
					  '<input id="login_password" placeholder="密码" style="margin-bottom: 15px;" type="password" name="login_password" size="30" data-validation="validate_min_length length4" />' +
					  '<input id="login_rememberme" style="float: left; margin-right: 10px;" type="checkbox" name="login_rememberme" value="1" />' +
					  '<label class="string optional" for="user_remember_me">记住我</label>' +
					  '<input class="btn btn-primary" style="clear: left; width: 100%; height: 32px; font-size: 13px;" type="submit" name="commit" value="登录" />' +
					'</form>' +
	              '</div>' +
	            '</li>' +
	          '</ul>';
		$('.navbar-inner .container-fluid').append(loginHtml);
	};
});

$(document).ready(function() {
	
	/* ---------------------------------------------------------------------- */
	/*	User Info - already login
	/* ---------------------------------------------------------------------- */
	$(function () {
		$("#button-logout").click(function() {
			$.get("/user/logout.json", function(data) {
				window.location.reload();
			}, "json");
		});
	});
	
	/* ---------------------------------------------------------------------- */
	/*	Login form - NOT login
	/* ---------------------------------------------------------------------- */
	$(function () {
		$("#user_username").focus();
		
		/* FORM VALIDATOR */
		var validationSettings = {
			errorMessagePosition : 'element',
		};
		
		$('#form-signin').submit(function() {
			if ($(this).validate(false, validationSettings)) {
				var $form = $( this ),
				username = $form.find('input[name="login_username"]').val(),
				password = $form.find('input[name="login_password"]').val();
				
				/* Send the data using post */
				$.post("/user/login.json",
					{
						user: username,
						password: password
					},
					function(data, status){
						var sid = data.sid;
						if (sid !== undefined) {
							// SUCCESS
							$.cookie('IDOCVSID', sid, { expires: 30, path: '/', domain: '.' + host + '' });
							window.location.reload();
						} else {
							// FAIL
							$('#sign-in-result').empty().append('<div class="alert alert-error">ERROR: ' + data.error + '</div>');
						}
					},
					"json"
				);
			}
			return false;
		})
		.validateOnBlur(false, validationSettings)
		.showHelpOnFocus();
	});
	
});
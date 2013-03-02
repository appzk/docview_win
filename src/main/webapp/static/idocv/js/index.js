/* ---------------------------------------------------------------------- */
/*	Check Login
/* ---------------------------------------------------------------------- */
	$.get("/user/checkLogin", function(data) {
		console.log(data);
		var username = data.username;
		if (username !== undefined) {
			// SUCCESS
			var loggedHtml = 
			  '<div class="nav-collapse collapse">' +
	            '<div class="pull-right">' +
	              '<ul class="nav pull-right">' +
	                '<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">Welcome, ' + username + ' <b class="caret"></b></a>' +
	                  '<ul class="dropdown-menu">' +
	                    '<li><a href="/user/preferences" style="text-decoration: none;"><i class="icon-cog"></i> Preferences</a></li>' +
	                    '<li><a href="http://www.idocv.com/en/contact.html" style="text-decoration: none;"><i class="icon-envelope"></i> Contact Support</a></li>' +
	                    '<li class="divider"></li>' +
	                    '<li><a href="/auth/logout" style="text-decoration: none;"><i class="icon-off"></i> Logout</a></li>' +
	                  '</ul>' +
	                '</li>' +
	              '</ul>' +
	            '</div>' +
	         '</div><!-- user info end -->';
			$('.navbar-inner .container-fluid').append(loggedHtml);
		} else {
			// FAIL
			window.location = "/signup.html";
		}
	}, "json");
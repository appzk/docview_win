var uid;
var username;
var sid;

/* ---------------------------------------------------------------------- */
/*	Check Login
/* ---------------------------------------------------------------------- */
$.ajax({
	type: "GET",
	url: '/user/checkLogin',
	async: false,
	dataType: "json",
}).done(function( data ) {
	username = data.username;
	uid = data.uid;
	
	// Already logged in user, redirect to his(her) own document list.
	var label = $.url().segment(2);
	if (uid !== undefined && label === undefined) {
		window.location = '/' + username + '/all';
	}
});

$(document).ready(function() {
	
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
							$.cookie('IDOCVSID', sid, { expires: 30, path: '/', domain: '.idocv.com' });
							window.location.reload();
						} else {
							// FAIL
							$('#sign-in-result').empty().append('<div class="alert alert-error">' + data.error + '</div>');
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
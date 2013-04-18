$(document).ready(function() {
	
	$("#username").focus();
	
	/* FORM VALIDATOR */
	var validationSettings = {
		errorMessagePosition : 'element',
	};
	
	$('#form-signup').submit(function() {
		if ($(this).validate(false, validationSettings)) {
			var $form = $( this ),
				username = $form.find('input[name="username"]').val(),
				email = $form.find('input[name="email"]').val(),
				password = $form.find('input[name="password"]').val();
			
			/* Send the data using post */
			$.post("/user/signup.json",
			{
				username: username,
				email: email,
				password: password
		    },
		    function(data, status){
		    	var sid = data.sid;
		    	if (sid !== undefined) {
		    		// SUCCESS
		    		$.cookie('IDOCVSID', sid);
		    		window.location = "/"
		    	} else {
		    		// FAIL
		    		$('#sign-up-result').empty().append('<div class="alert alert-error">ERROR: ' + data.error + '</div>');
		    	}
		    }, "json");
		}
		return false;
	})
	.validateOnBlur(false, validationSettings)
	.showHelpOnFocus();
});
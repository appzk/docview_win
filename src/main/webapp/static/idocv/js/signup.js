$(document).ready(function() {
	
	$("#username").focus();
	
	/* FORM VALIDATOR */
	var validationSettings = {
		errorMessagePosition : 'element',
	};
	
	$('#form-signup').submit(function() {
		if ($(this).validate(false, validationSettings)) {
			alert('Valid!');
			var $form = $( this ),
				username = $form.find('input[name="username"]').val(),
				email = $form.find('input[name="email"]').val(),
				password = $form.find('input[name="password"]').val();
			alert("username=" + username + ", email=" + email + ", password=" + password);
			
			/* Send the data using post */
			$.post("http://api.idocv.com/user/signup",
			{
				username: username,
				email: email,
				password: password
		    },
		    function(data, status){
		    	var uid = data.uid;
		    	if (uid !== undefined) {
		    		// SUCCESS
		    		alert("uid=" + uid);
		    	} else {
		    		// FAIL
		    		$('#sign-up-result').append('<div class="alert alert-error">ERROR: ' + data.error + '</div>');
		    	}
		    }, "json");
		}
		return false;
	})
	.validateOnBlur(false, validationSettings)
	.showHelpOnFocus();
});
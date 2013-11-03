/**
 * Copyright 2013 I Doc View
 * @author Godwin <godwin668@gmail.com>
 */

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
		    		// $.cookie('IDOCVSID', sid);
		    		// window.location = "/"
		    		$('#sign-up-result').empty().append('<div class="alert alert-success">感谢您的注册！已给您发了一封确认邮件，点击邮箱中的链接来激活您的账号，账号激活后就可以上传和预览文档了！</div>');
		    	} else {
		    		// FAIL
		    		$('#sign-up-result').empty().append('<div class="alert alert-error">' + data.error + '</div>');
		    	}
		    }, "json");
		}
		return false;
	})
	.validateOnBlur(false, validationSettings)
	.showHelpOnFocus();
});
$(document).ready(function() {
	
	$("#username").focus();
	
	/* FORM VALIDATOR */
	var validationSettings = {
		errorMessagePosition : 'element',
	};
	
	$('#form-signup').submit(function() {
		if ($(this).validate(false, validationSettings))
		alert('Valid!');
	
		return false;
	})
	.validateOnBlur(false, validationSettings)
	.showHelpOnFocus();
});
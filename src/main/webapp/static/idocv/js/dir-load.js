/**
 * Copyright 2013 I Doc View
 * 
 * @author Godwin <godwin668@gmail.com>
 */

$(document).ready(function() {
	
	$('.btn-cmd-commit').click(function(){
		var txt=$('.container input').val();
		$.post('/dir/load.json',{dir:txt},function(result){
			var code = result.code;
			if ('1' == code) {
				// console.log(result.data);
				$('.cmd-result pre').append(result.data);
			} else {
				$('.cmd-result').html('Error: ' + result.msg);
			}
		});
	});

});
$(document).ready(function() {
	
	/* ---------------------------------------------------------------------- */
	/*	Login form - NOT login
	/* ---------------------------------------------------------------------- */
	$(function () {
		var email = $.url().param('email');
		var key = $.url().param('key');
		$.get("/user/activate.json",
			{
				email: email,
				key: key
			},
			function(data, status){
				var error = data.error;
				if (error !== undefined) {
					// FAIL
					$('.activate-result').empty().html('<div class="alert alert-error">' + error + '</div><br />您可以直接<a href="/login">登录</a>，或<a href="signup">注册</a>一个新账号。');
				} else {
					// SUCCESS
					$('.activate-result').empty().html('<div class="alert alert-success">激活成功！您现在可以开始文档预览之旅了，如需帮助或有任何建议，请到我们的<a href="http://bbs.idocv.com" target="_blank">社区</a>，或现在<a href="/login">登录</a>...</div>');
				}
			},
			"json"
		);
	});
	
});
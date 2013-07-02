package com.idocv.docview.email;

import org.apache.commons.mail.HtmlEmail;

import com.idocv.docview.util.EmailUtil;

public class SendEmailTest {
	public static void main(String[] args) {
		try {
			EmailUtil.sendMail("GGG", "137123093@qq.com", "用户验证", "请点击以下链接以验证您的邮箱！");
			System.err.println("Done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMail(String username, String address, String title, String content) {
		try {
			// Create the email message
			HtmlEmail email = new HtmlEmail();
			email.setCharset("UTF-8");
			email.setHostName("pop.gmail.com");
			email.addTo(address, username);
			email.setFrom("support@idocv.com", "IDocV");
			email.setSubject(title);
			email.setAuthentication("support@idocv.com", "idocv.com88");
			email.setStartTLSRequired(true);

			// embed the image and get the content id
			String logoSrc = "http://www.idocv.com/img/logo.png";

			// set the html message
			email.setHtmlMsg("<html><h1>" + username + "，您好：</h1><p>" + content + "</p><hr /><a href=\"http://www.idocv.com\"><img src=\"" + logoSrc + "\"></a></html>");

			// set the alternative message
			email.setTextMsg("Your email client does not support HTML messages");

			// send the email
			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

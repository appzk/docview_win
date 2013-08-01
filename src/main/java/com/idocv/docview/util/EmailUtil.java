package com.idocv.docview.util;

import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtil implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

	private String username;
	private String address;
	private String title;
	private String content;

	public EmailUtil(String username, String address, String title, String content) {
		this.username = username;
		this.address = address;
		this.title = title;
		this.content = content;
	}

	public static void sendMail(String username, String address, String title, String content) {
		new Thread(new EmailUtil(username, address, title, content)).start();
	}
	
	@Override
	public void run() {
		try {
			// Create the email message
			logger.info("Sending email: username=" + username + ", email=" + address + ", title=" + title + ", content=" + content);
			HtmlEmail email = new HtmlEmail();
			email.setCharset("UTF-8");
			email.setHostName("smtp.exmail.qq.com");
			email.addTo(address, username);
			email.setFrom("support@idocv.com", "IDocV");
			email.setSubject(title + " - I Doc View");
			email.setAuthentication("support@idocv.com", "idocv.com88");
			// email.setStartTLSRequired(true);

			// embed the image and get the content id
			String logoSrc = "http://www.idocv.com/img/logo.png";

			// set the html message
			email.setHtmlMsg("<html><h1>" + username + "，您好：</h1><p>" + content + "</p><hr /><a href=\"http://www.idocv.com\"><img src=\"" + logoSrc + "\"></a></html>");

			// set the alternative message
			email.setTextMsg("Your email client does not support HTML messages");

			// send the email
			email.send();
			logger.info("Successfully send email: username=" + username + ", email=" + address + ", title=" + title + ", content=" + content);
		} catch (Exception e) {
			logger.error("Error sending email: username=" + username + ", email=" + address + ", title=" + title + ", content=" + content);
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		EmailUtil.sendMail("Godwin", "137123093@qq.com", "Test", "Tester");
	}
}

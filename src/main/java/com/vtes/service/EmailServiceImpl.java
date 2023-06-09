package com.vtes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vtes.entity.User;
import com.vtes.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailServiceImpl implements EmailService {
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private UserRepository userRepository;
	
	@Value("${vtes.app.frontend.uri}")
	private String frontEndURL;

	@Override
	@Async
	public void sendRegistrationUserConfirm(String email, String token) {

		SimpleMailMessage message = new SimpleMailMessage();
		User user = userRepository.findByEmail(email).get();
		String confirmationUrl = frontEndURL+"/verify/"+ token;
		message.setTo(email);
		message.setSubject("【重要】新規登録手続きのご案内");
		message.setText(user.getEmail() + "さん、、\r\n"
				+ "この度は、「VTES」をご利用いただきまして、\r\n"
				+ "誠にありがとうございます。\r\n"
				+ "\r\n"
				+ "Vtesでは安全にアカウントをご利用いただくために、\r\n"
				+ "ご登録いただいたメールアドレスの認証をお願いしております。\r\n"
				+ "\r\n"
				+ "このメールが到着してから３０分以内に下記URLをクリックしてください。\r\n"
				+ "\r\n"
				+ "認証URL：\r\n"
				+ confirmationUrl
				+ "\r\n"
				+ "認証が完了しない場合、ホームページなどがご利用いただけませんので、\r\n"
				+ "ご了承ください。\r\n"
				+ "\r\n"
				+ "＊メール到着後、３０分以上経過した場合は、もう一度最初から手続きをお願い致します。\r\n"
				+ "\r\n"
				+ "引き続き、よろしくお願いします。"
				+ "ーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーー\r\n"
				+ "ご不明点は下記連絡先までお問い合わせください。"
				+ "開発部"
				+ "新人2023"
				+ "Email: vti.fresher.042023@vti.com.vn");

		mailSender.send(message);
		log.info("Account activity email sent to: {}", email);

	}

	@Override
	@Async
	public void sendResetPasswordViaEmail(String email, String token) {
		SimpleMailMessage message = new SimpleMailMessage();
		User user = userRepository.findByEmail(email).get();
		String confirmationUrl = frontEndURL+"/confirmresetpassword/"+ token;
		message.setTo(email);
		message.setSubject("「VTES会員」パスワード再設定手順のご案内");
		message.setText(user.getFullName()+"さん、"
				+ "\r\n"
				+ "いつもVTESをご利用いただきまして、誠にありがとうございます。\r\n"
				+ "\r\n"
				+ "パスワード再設定手順をご案内いたします。\r\n"
				+ "\r\n"
				+ "このメールが到着してから３０分以内に下記URLをクリックしてください。\r\n"
				+ "\r\n"
				+ confirmationUrl
				+ "\r\n"
				+ "入力画面が表示されますので、新しいパスワードを入力後、「保存」ボタンを押してください。\r\n"
				+ "再設定の手続きが完了します。\r\n"
				+ "\r\n"
				+ "＊メール到着後、３０分以上経過した場合は、もう一度最初から手続きをお願い致します。\r\n"
				+ "ご不明な点がある場合は、お問い合わせください。\r\n"
				+ "\r\n"
				+ "引き続き、よろしくお願いします。"
				+ "ーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーーー\r\n"
				+ "ご不明点は下記連絡先までお問い合わせください。"
				+ "開発部"
				+ "新人2023"
				+ "Email: vti.fresher.042023@vti.com.vn");

		mailSender.send(message);
		log.info("Reset password email sent to: {}", email);

	}

}

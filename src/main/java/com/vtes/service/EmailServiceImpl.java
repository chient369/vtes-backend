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
		
		String confirmationUrl = frontEndURL+"/verify/"+ token;
		message.setTo(email);
		message.setSubject("【重要】アカウント登録の完了とアクティベーション手続きのご案内");
		message.setText("本メールは、アカウント登録の完了をお知らせするためにお送りしています。ご登録いただいた情報に基づき、アカウントを有効化していただく必要があります。\n\n"
				+ "以下のリンクをクリックして、アカウントを有効化してください：\n" + confirmationUrl + "\n\n"
				+ "有効化手続きの完了後、アカウントを正常にご利用いただけます。ご不明な点や質問がございましたら、お気軽にお問い合わせください。\n\n"
				+ "今後とも、当サービスをご利用いただきありがとうございます。\n\n" + "よろしくお願いいたします。\n\n");

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
		message.setSubject("パスワード再設定手続きのご案内");
		message.setText(user.getFullName() + "様、\n\n" + "パスワードをリセットするためのリクエストがありました。下のリンクをクリックしてパスワードをリセットしてください。\n\n"
				+ "下のリンクをクリックしてパスワードをリセットします:\n" + confirmationUrl + "\n\n" + "もしもこのリクエストを行っていない場合は、このメールを無視してください。\n\n"
				+ "ご不明な点がある場合は、お問い合わせください。\n\n" + "ありがとうございました。");

		mailSender.send(message);
		log.info("Reset password email sent to: {}", email);

	}

}

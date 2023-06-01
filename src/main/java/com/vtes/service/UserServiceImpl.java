package com.vtes.service;

import java.time.Instant;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vtes.entity.CommuterPass;
import com.vtes.entity.Department;
import com.vtes.entity.User;
import com.vtes.model.CommuterPassDTO;
import com.vtes.payload.request.PasswordResetEmailRequest;
import com.vtes.payload.request.PasswordResetRequest;
import com.vtes.payload.request.UpdateInfoRequest;
import com.vtes.payload.response.ResponseData;
import com.vtes.payload.response.ResponseData.ResponseType;
import com.vtes.repository.CommuterPassRepo;
import com.vtes.repository.DepartmentRepository;
import com.vtes.repository.UserRepository;
import com.vtes.security.jwt.JwtUtils;
import com.vtes.security.services.UserDetailsImpl;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private FareService fareService;

	@Autowired
	private CommuterPassRepo commuterPassRepo;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	EmailService emailService;

	@Autowired
	JwtUtils jwtUtils;

	@Override
	public ResponseEntity<?> activeUser(String token) {
	
		if (!isTokenActiveUserExists(token)) {
			return ResponseEntity.badRequest().body(ResponseData.builder().type(ResponseType.ERROR).code("API005_ER")
					.message("Verify code incorrect").build());
		}

		if (!jwtUtils.validateJwtToken(token)) {
			Optional<User> user = userRepository.findByVerifyCode(token);
			if(user.isPresent()) {
				userRepository.deleteById(user.get().getId());
			}
			
			log.info("Verify code has expired : {}", token);
			return ResponseEntity.badRequest().body(ResponseData.builder().type(ResponseType.ERROR).code("API_ER01")
					.message("Verify code has expired").build());
		}

		User user = userRepository.findByVerifyCode(token).get();
		user.setVerifyCode(null);
		user.setStatus((short) 1);
		userRepository.save(user);

		log.info("User {} of account is active", user.getFullName());

		return ResponseEntity.ok().body(ResponseData.builder().type(ResponseType.INFO).code("")
				.message("Account verify successfully").build());

	}

	@Override
	public ResponseEntity<?> updateUser(@Valid UpdateInfoRequest updateInfoRequest, UserDetailsImpl userDetailsImpl) {
		User user = getUserByEmail(userDetailsImpl.getEmail());
		if (!departmentExists(updateInfoRequest.getDepartmentId())) {
			log.debug("Bad request with department ID {}", updateInfoRequest.getDepartmentId());

			return ResponseEntity.badRequest().body(ResponseData.builder().type(ResponseType.ERROR).code("API_ER02")
					.message("Invalid parameter").build());
		}

		Department department = getDepartmentById(updateInfoRequest.getDepartmentId());

		if (updateInfoRequest.getNewPassword() != null) {
			if (!isPasswordValid(updateInfoRequest.getOldPassword(), user.getPassword()) && updateInfoRequest.getOldPassword() != null) {

				log.info("{} of entered password not match", user.getFullName());

				return ResponseEntity.ok().body(
						ResponseData.builder().type(ResponseType.ERROR).code("").message("Old password is not match").build());
			}

			updateUserPassword(user, updateInfoRequest.getNewPassword());
		}

		updateCommuterPass(user, userDetailsImpl.getId(), updateInfoRequest.getCommuterPass());

		user.setDepartment(department);
		user.setFullName(updateInfoRequest.getFullName());
		user.setUpdateDt(Instant.now());
		userRepository.save(user);

		return ResponseEntity.ok()
				.body(ResponseData.builder().type(ResponseType.INFO).code("200").message("Update successfull").build());
	}

	private boolean isTokenActiveUserExists(String token) {
		return !userRepository.findByVerifyCode(token).isEmpty();
	}

	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	private boolean departmentExists(Integer departmentId) {
		return departmentRepository.existsById(departmentId);
	}

	private Department getDepartmentById(Integer departmentId) {
		return departmentRepository.findById(departmentId)
				.orElseThrow(() -> new IllegalArgumentException("Department not found"));
	}

	private boolean isPasswordValid(String password, String encodedPassword) {
		return encoder.matches(password, encodedPassword);
	}

	private void updateUserPassword(User user, String password) {
		user.setPassword(encoder.encode(password));
	}

	private void updateCommuterPass(User user, Integer userId, CommuterPassDTO commuterPassDTO) {
		if (commuterPassDTO.getViaDetails() != null) {
			String viaDetail = commuterPassDTO.getViaDetails().toString();

			CommuterPass commuterPass = commuterPassRepo.findByUserId(userId).orElse(new CommuterPass());
			commuterPass.setDeparture(commuterPassDTO.getDeparture());
			commuterPass.setDestination(commuterPassDTO.getDestination());
			commuterPass.setViaDetail(viaDetail);
			commuterPass.setUser(new User(userId));
			user.setCommuterPass(commuterPass);
		}
	}

	@Override
	public User getUser(String email) {
		User user = userRepository.findByEmail(email).get();
		user.setFares(fareService.finByUserId(user.getId()));
		return user;
	}

	@Override
	public ResponseEntity<?> sendResetPasswordViaEmail(PasswordResetEmailRequest passwordResetEmailRequest) {

		if (!userRepository.existsByEmail(passwordResetEmailRequest.getEmail())) {
			log.info("Not found email : {}", passwordResetEmailRequest.getEmail());

			return ResponseEntity.badRequest().body(ResponseData.builder().type(ResponseType.ERROR).code("API003_ER")
					.message("This entered email does not exist").build());

		}

		User user = new User();
		user = userRepository.findByEmail(passwordResetEmailRequest.getEmail()).get();

		if (user.getStatus() == 0) {
			return ResponseEntity.badRequest().body(ResponseData.builder().type(ResponseType.ERROR).code("API001_ER02")
					.message("This account is not active yet").build());
		}
		String tokenToResetPassword = jwtUtils.generateTokenToResetPassword(user.getEmail());

		user.setVerifyCode(tokenToResetPassword);
		userRepository.save(user);

		emailService.sendResetPasswordViaEmail(passwordResetEmailRequest.getEmail(), user.getVerifyCode());

		return ResponseEntity.ok()
				.body(ResponseData.builder().type(ResponseType.INFO).code("").message("Verify mail has sent").build());
	}

	@Override
	public ResponseEntity<?> resetPassword(PasswordResetRequest passwordResetRequest) {
		// TODO Auto-generated method stub

		String tokenResetPassword = passwordResetRequest.getAuthToken();

		if (!isTokenResetPasswordExists(tokenResetPassword)) {
			log.info("Verify code does not exist : {}", passwordResetRequest.getAuthToken());

			return ResponseEntity.badRequest().body(ResponseData.builder().type(ResponseType.ERROR).code("API005_ER")
					.message("Verify code incorrect").build());
		}

		if (!jwtUtils.validateJwtToken(tokenResetPassword)) {
			log.info("Verify code has expired : {}", passwordResetRequest.getAuthToken());

			return ResponseEntity.badRequest().body(ResponseData.builder().type(ResponseType.ERROR).code("API_ER01")
					.message("Verify code has expired").build());
		}

		User user = new User();
		user = userRepository.findByVerifyCode(tokenResetPassword).get();

		if (user.getStatus() == 0) {
			return ResponseEntity.badRequest().body(ResponseData.builder().type(ResponseType.ERROR).code("API001_ER02")
					.message("This account is not active yet").build());
		}

		user.setPassword(encoder.encode(passwordResetRequest.getNewPassword()));
		user.setVerifyCode(null);
		userRepository.save(user);

		log.info("{} reset password successfully!", user.getFullName());
		return ResponseEntity.ok().body(ResponseData.builder().type(ResponseType.INFO).code("")
				.message("Reset password successfully!").build());

	}

	private boolean isTokenResetPasswordExists(String token) {
		return !userRepository.findByVerifyCode(token).isEmpty();

	}

}

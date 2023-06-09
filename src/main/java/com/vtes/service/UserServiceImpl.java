package com.vtes.service;

import java.time.Duration;
import java.time.Instant;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vtes.entity.CommuterPass;
import com.vtes.entity.Department;
import com.vtes.entity.User;
import com.vtes.exception.AuthenticationFailedException;
import com.vtes.exception.NotFoundException;
import com.vtes.exception.UserException;
import com.vtes.exception.VtesException;
import com.vtes.model.CommuterPassDTO;
import com.vtes.payload.EmailPayload;
import com.vtes.payload.RegisterPayload;
import com.vtes.payload.ResetPasswordPayload;
import com.vtes.payload.UpdateUserPayload;
import com.vtes.repository.CommuterPassRepo;
import com.vtes.repository.DepartmentRepository;
import com.vtes.repository.UserRepository;
import com.vtes.security.jwt.JwtUtils;
import com.vtes.security.service.UserDetailsImpl;

import lombok.extern.slf4j.Slf4j;

/*
 * @Author : cong.nguyenthanh
 * Date : 2023/05/21
 * */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
	private final Integer USER_CACHE_DURATION = 30;
	private final String RST_PWD_PREFIX = "RST_PWD:";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private FareService fareService;

	@Autowired
	private CommuterPassRepo commuterPassRepo;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private JwtUtils jwtUtils;

	public User saveUser(RegisterPayload payload) throws VtesException {
		if (isActiveUserAccount(payload.getEmail())) {
			log.info("{} is trying to register ", payload.getEmail());
			throw new UserException("API002_ER", "This email has already been used");
		}

		if (departmentRepository.findById(payload.getDepartmentId()).isEmpty()) {
			throw new VtesException("", "Department id " + payload.getDepartmentId() + " not found");
		}

		if (isExistsUserOnCache(payload.getEmail())) {
			log.info("{} is trying to register", payload.getEmail());
			throw new UserException("API002_ER2", "Active email already sent to " + payload.getEmail());
		}

		User preActiveUser = saveNewUser(payload);
		if (preActiveUser != null) {
			restoreRegisterUser(preActiveUser);
			emailService.sendRegistrationUserConfirm(payload.getEmail(), preActiveUser.getVerifyCode());
		}
		return preActiveUser;

	}

	private boolean isExistsUserOnCache(String email) {
		return redisTemplate.hasKey(email);
	}

	private User saveNewUser(RegisterPayload payload) {
		User user = new User(payload.getFullName(), payload.getEmail(), encoder.encode(payload.getPassword()),
				new Department(payload.getDepartmentId()));
		user.setStatus((short) 0);
		String tokenActive = jwtUtils.generateTokenToActiveUser(payload.getEmail());
		user.setVerifyCode(tokenActive);
		user.setDeleteFlag(false);

		log.info("New account {} has been registered", user.getEmail());
		log.info("{} of details has been restored to cache", user.getEmail());
		return user;
	}

	@Override
	public void activeUser(String token) throws AuthenticationFailedException {

		if (!redisTemplate.hasKey(token)) {
			throw new AuthenticationFailedException("API005_ER", "Verify code has expired");
		}
		User user = (User) redisTemplate.opsForValue().get(token);
		user.setCreateDt(Instant.now());
		user.setVerifyCode(null);
		user.setStatus((short) 1);
		userRepository.save(user); 
		clearCacheAfterActive(user, token);

		log.info("User {} of account is active", user.getFullName());

	}

	@Override
	public User updateUser(@Valid UpdateUserPayload updateInfoRequest, UserDetailsImpl userDetailsImpl)
			throws VtesException {
		User user = getUserByEmail(userDetailsImpl.getEmail());
		if (!departmentExists(updateInfoRequest.getDepartmentId())) {
			log.info("Bad request with department ID {}", updateInfoRequest.getDepartmentId());
			throw new VtesException("API_ER02", "Invalid parameter");
		}

		Department department = getDepartmentById(updateInfoRequest.getDepartmentId());

		if (updateInfoRequest.getNewPassword() != null) {
			if (!isPasswordValid(updateInfoRequest.getOldPassword(), user.getPassword())
					&& updateInfoRequest.getOldPassword() != null) {

				log.info("{} of entered password not match", user.getFullName());
				throw new VtesException("API004_ER", "Old password is not match");
			}

			updateUserPassword(user, updateInfoRequest.getNewPassword());
		}

		updateCommuterPass(user, userDetailsImpl.getId(), updateInfoRequest.getCommuterPass());

		user.setDepartment(department);
		user.setFullName(updateInfoRequest.getFullName());
		user.setUpdateDt(Instant.now());
		User updatedUser = userRepository.save(user);

		log.info("{} update successfully", user.getFullName());
		return updatedUser;

	}

	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
	}

	private boolean departmentExists(Integer departmentId) {
		return departmentRepository.existsById(departmentId);
	}

	private Department getDepartmentById(Integer departmentId) throws NotFoundException {
		return departmentRepository.findById(departmentId)
				.orElseThrow(() -> new NotFoundException("Department ID"+departmentId+" not found"));
	}

	private boolean isPasswordValid(String password, String encodedPassword) {
		return encoder.matches(password, encodedPassword);
	}

	private void updateUserPassword(User user, String password) {
		user.setPassword(encoder.encode(password));
	}

	private void updateCommuterPass(User user, Integer userId, CommuterPassDTO commuterPassDTO) {
		if (commuterPassDTO != null && commuterPassDTO.getViaDetails() != null) {
			String viaDetail = commuterPassDTO.getViaDetails().toString();

			CommuterPass commuterPass = commuterPassRepo.findByUserId(userId).orElse(new CommuterPass());
			commuterPass.setDeparture(commuterPassDTO.getDeparture());
			commuterPass.setDestination(commuterPassDTO.getDestination());
			commuterPass.setViaDetail(viaDetail);
			commuterPass.setUser(new User(userId));
			user.setCommuterPass(commuterPass);
			log.info("{} of commuter pass has been updated", user.getEmail());
		}
	}

	@Override
	public User getUser(String email) {
		User user = userRepository.findByEmail(email).get();
		user.setFares(fareService.finByUserId(user.getId()));
		return user;
	}

	@Override
	public void sendResetPasswordViaEmail(EmailPayload payload) throws VtesException {

		if (!userRepository.existsByEmail(payload.getEmail())) {
			throw new NotFoundException("API003_ER01", "Email: " + payload.getEmail() + " does not exist");

		}

		User user = userRepository.findByEmail(payload.getEmail()).get();

		if (user.getStatus() == 0) {
			throw new UserException("API001_ER02", "This account is not active yet");
		}
		if (isSentResetPasswordMail(payload.getEmail())) {
			throw new AuthenticationFailedException("API003_ER02", "Reset password link has been sent");
		}
		String tokenToResetPassword = jwtUtils.generateTokenToResetPassword(user.getEmail());
		user.setVerifyCode(tokenToResetPassword);
		userRepository.save(user);
		restoreResetPassWait(payload.getEmail());

		emailService.sendResetPasswordViaEmail(payload.getEmail(), user.getVerifyCode());

	}

	@Override
	public void resetPassword(ResetPasswordPayload passwordResetRequest) throws VtesException {

		String tokenResetPassword = passwordResetRequest.getAuthToken();

		if (!jwtUtils.validateJwtToken(tokenResetPassword)) {
			log.info("Verify code has expired : {}", tokenResetPassword);
			throw new AuthenticationFailedException("API005_ER", "Verify code has expired");
		}

		User user = new User();
		user = userRepository.findByVerifyCode(tokenResetPassword).get();

		if(isUsedPassword(passwordResetRequest.getNewPassword(),user)) {
			throw new UserException("API003_ER03", "New password matches old password");
		}

		user.setPassword(encoder.encode(passwordResetRequest.getNewPassword()));
		user.setVerifyCode(null);
		redisTemplate.delete(RST_PWD_PREFIX + user.getEmail());
		
		userRepository.save(user);

		log.info("{} reset password successfully!", user.getFullName());

	}

	public boolean isActiveUserAccount(String email) {
		return userRepository.findActiveUserByEmail(email) > 0;

	}

	public User findUserById(Integer userId) throws NotFoundException {
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
	}
	private boolean isUsedPassword(String newPass,User user) {
		return encoder.matches(newPass, user.getPassword());
	}

	private void restoreRegisterUser(User user) {
		redisTemplate.opsForValue().set(user.getEmail(), null, Duration.ofMinutes(USER_CACHE_DURATION));
		redisTemplate.opsForValue().set(user.getVerifyCode(), user, Duration.ofMinutes(USER_CACHE_DURATION));
	}
	
	private void restoreResetPassWait(String email) {
		redisTemplate.opsForValue().set(RST_PWD_PREFIX + email, null, Duration.ofMinutes(USER_CACHE_DURATION));
	}

	private boolean isSentResetPasswordMail(String email) {
	 return	redisTemplate.hasKey(RST_PWD_PREFIX + email);
	}
	
	private void clearCacheAfterActive(User user, String token) {
		redisTemplate.delete(user.getEmail());
		redisTemplate.delete(token);
	}
	

}

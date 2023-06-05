package com.vtes.service;

import java.time.Instant;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private FareService fareService;

	@Autowired
	private CommuterPassRepo commuterPassRepo;

	@Autowired
	private  PasswordEncoder encoder;

	@Autowired
	private  EmailService emailService;

	@Autowired
	private JwtUtils jwtUtils;

	
	public User saveUser(RegisterPayload payload) throws VtesException {
		if (isActiveUserAccount(payload.getEmail())) {
			log.info("{} is trying to register ",payload.getEmail());
			throw new UserException("API002_ER", "This email has already been used");
		}

		if (departmentRepository.findById(payload.getDepartmentId()).isEmpty()) {
			throw new VtesException("", "Department id "+payload.getDepartmentId()+" not found");
		}
		
		User savedUser ;

		if(userRepository.existsByEmail(payload.getEmail())) {
			savedUser = saveUserWhenNotActive(payload);
		}else {
			savedUser = saveNewUser(payload);
		}
		
		if(savedUser != null) {
			emailService.sendRegistrationUserConfirm(payload.getEmail(), savedUser.getVerifyCode());
		}
		return savedUser;
		
		

	}
	
	private User saveUserWhenNotActive(RegisterPayload payload) {
		Department department = new Department();
		department = departmentRepository.findById(payload.getDepartmentId()).get();

		User userDb = userRepository.findByEmail(payload.getEmail()).get();
		userDb.setStatus((short) 0);
		String tokenActive = jwtUtils.generateTokenToActiveUser(payload.getEmail());
		userDb.setVerifyCode(tokenActive);
		userDb.setCreateDt(Instant.now());
		userDb.setDeleteFlag(false);
		userDb.setDepartment(department);
		User savedUser = userRepository.save(userDb);
		log.info("Not active account {} has beean re-registered",savedUser.getEmail());
		
		return savedUser;
	}
	private User saveNewUser(RegisterPayload payload) {
		Department department = new Department();
		department = departmentRepository.findById(payload.getDepartmentId()).get();

		User user = new User(payload.getFullName(), payload.getEmail(),
				encoder.encode(payload.getPassword()), department);
		user.setStatus((short) 0);
		String tokenActive = jwtUtils.generateTokenToActiveUser(payload.getEmail());
		user.setVerifyCode(tokenActive);
		user.setCreateDt(Instant.now());
		user.setDeleteFlag(false);
		User savedUser = userRepository.save(user);

		log.info("New account {} has been registered",savedUser.getEmail());
		return savedUser;
	}
	
	@Override
	public void activeUser(String token) throws AuthenticationFailedException {
	
		if (!isTokenActiveUserExists(token)) {
			log.info("Verify code incorrect : {}",token);
			throw new AuthenticationFailedException("API005_ER","Verify code incorrect");
		}

		if (!jwtUtils.validateJwtToken(token)) {
			Optional<User> user = userRepository.findByVerifyCode(token);
			if(user.isPresent()) {
				log.info("Active failed user id {} has deleted.",user.get().getId());
				userRepository.deleteById(user.get().getId());
			}
			
			log.info("Verify code has expired : {}", token);
			throw new AuthenticationFailedException("API_ER01","Verify code has expired");
		}

		User user = userRepository.findByVerifyCode(token).get();
		user.setVerifyCode(null);
		user.setStatus((short) 1);
		userRepository.save(user);

		log.info("User {} of account is active", user.getFullName());

	}

	@Override
	public User updateUser(@Valid UpdateUserPayload updateInfoRequest, UserDetailsImpl userDetailsImpl) throws VtesException {
		User user = getUserByEmail(userDetailsImpl.getEmail());
		if (!departmentExists(updateInfoRequest.getDepartmentId())) {
			log.info("Bad request with department ID {}", updateInfoRequest.getDepartmentId());
			throw new VtesException("API_ER02","Invalid parameter");
		}

		Department department = getDepartmentById(updateInfoRequest.getDepartmentId());

		if (updateInfoRequest.getNewPassword() != null) {
			if (!isPasswordValid(updateInfoRequest.getOldPassword(), user.getPassword()) && updateInfoRequest.getOldPassword() != null) {

				log.info("{} of entered password not match", user.getFullName());
				throw new VtesException("API004_ER","Old password is not match");
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
			log.info("{} of commuter pass has been updated",user.getEmail());
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
			throw new NotFoundException("API003_ER","Email"+payload.getEmail()+" does not exist");			

		}
 
		User user = userRepository.findByEmail(payload.getEmail()).get();

		if (user.getStatus() == 0) {
			throw new UserException("API001_ER02","This account is not active yet");
		}
		String tokenToResetPassword = jwtUtils.generateTokenToResetPassword(user.getEmail());

		user.setVerifyCode(tokenToResetPassword);
		userRepository.save(user);

		emailService.sendResetPasswordViaEmail(payload.getEmail(), user.getVerifyCode());

	}

	@Override
	public void resetPassword(ResetPasswordPayload passwordResetRequest) throws VtesException {

		String tokenResetPassword = passwordResetRequest.getAuthToken();

		if (!isTokenResetPasswordExists(tokenResetPassword)) {
			log.info("Verify code does not exist : {}", passwordResetRequest.getAuthToken());
			throw new AuthenticationFailedException("API005_ER","Verify code incorrect");

		}

		if (!jwtUtils.validateJwtToken(tokenResetPassword)) {
			log.info("Verify code has expired : {}", passwordResetRequest.getAuthToken());
			throw new AuthenticationFailedException("API_ER01","Verify code has expired");
		}

		User user = new User();
		user = userRepository.findByVerifyCode(tokenResetPassword).get();

		if (user.getStatus() == 0) {
			throw new UserException("API001_ER02","This account is not active yet");
		}

		user.setPassword(encoder.encode(passwordResetRequest.getNewPassword()));
		user.setVerifyCode(null);
		userRepository.save(user);

		log.info("{} reset password successfully!", user.getFullName());


	}

	private boolean isTokenResetPasswordExists(String token) {
		return !userRepository.findByVerifyCode(token).isEmpty();

	}
	public boolean isActiveUserAccount(String email) {
		return userRepository.findActiveUserByEmail(email) > 0;

	}

	public User findUserById(Integer userId) throws NotFoundException {
	    return userRepository.findById(userId)
	            .orElseThrow(() -> new NotFoundException("User not found: " + userId));
	}

}

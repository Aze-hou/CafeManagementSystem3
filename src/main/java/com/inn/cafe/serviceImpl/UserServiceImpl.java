package com.inn.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.CustomerUsersDetailsService;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtil;
import com.inn.cafe.POJO.User;
import com.inn.cafe.constents.CafeConstant;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private CustomerUsersDetailsService customerUsersDetailsService;

	@Autowired
	private JwtFilter jwtFilter;
	@Autowired
	private EmailUtils emailUtils;

	// signUp
	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {

		try {
			log.info("inside signUp .." + requestMap);
			if (ValidateSignUp(requestMap)) {
				User user = userDao.findByEmailId(requestMap.get("email"));
				if (Objects.isNull(user)) {
					userDao.save(getUserFromMap(requestMap));
					return CafeUtils.getResponseEntity("Successfully Registered.", HttpStatus.OK);
				} else {
					return CafeUtils.getResponseEntity(CafeConstant.EMAIL_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
				}

			} else {
				return CafeUtils.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	private boolean ValidateSignUp(Map<String, String> requestMap) {
		return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
				&& requestMap.containsKey("email") && requestMap.containsKey("password");
	}

	public User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");

		return user;

	}

//	login
	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		try {
			log.info("inside login...");

			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));

			if (auth.isAuthenticated()) {
				if (customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
					return new ResponseEntity<String>(
							"{\"token\":\""
									+ jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
											customerUsersDetailsService.getUserDetail().getRole())
									+ "\"}",
							HttpStatus.OK);

				} else {
					return new ResponseEntity<String>("{\"message\":\"" + "Wait for Admin approval." + "\"}",
							HttpStatus.BAD_REQUEST);
				}

			}
		} catch (Exception ex) {
			log.error("{}", ex);
		}

		return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentiels.." + "\"}", HttpStatus.BAD_REQUEST);
	}

//	getAllUser
	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if (jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);

			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// update status
	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
				if (!optional.isEmpty()) {
					userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
					sendEmailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
					return new ResponseEntity<String>("User updated succesuly", HttpStatus.OK);
				} else {
					return new ResponseEntity<String>("User id donest exist", HttpStatus.OK);
				}

			} else {
				return CafeUtils.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// send Email To All Admin :
	private void sendEmailToAllAdmin(String status, String user, List<String> allAdmin) {
		allAdmin.remove(jwtFilter.getCurrentUser());
		if (status != null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved",
					"USER:- " + user + " \n is approved by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);

		} else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled",
					"USER:- " + user + " \n is disabled by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		}

	}

//	checkToken
	@Override
	public ResponseEntity<String> checkToken() {
		return CafeUtils.getResponseEntity("true", HttpStatus.OK);
	}

//	changePassword
	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
			if (!userObj.equals(null)) {
				if (userObj.getPassword().equals(requestMap.get("oldPassword"))) {
					userObj.setPassword(requestMap.get("newPassword"));
					userDao.save(userObj);
					return CafeUtils.getResponseEntity("Password Updated succuffully", HttpStatus.OK);

				}
				return CafeUtils.getResponseEntity("Incorrect old Password", HttpStatus.BAD_REQUEST);

			}
			return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	
//	forgotPassword
	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
		try {

			User user = userDao.findByEmail(requestMap.get("email"));
			if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
				emailUtils.forgotMail(user.getEmail(), "credentizl by cafe ùanagement", user.getPassword());
				return CafeUtils.getResponseEntity("Check your credentiels", HttpStatus.OK);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

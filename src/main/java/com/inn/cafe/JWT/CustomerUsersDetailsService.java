package com.inn.cafe.JWT;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.inn.cafe.dao.UserDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerUsersDetailsService implements UserDetailsService {

	@Autowired
	private UserDao userDao;

	private com.inn.cafe.POJO.User userDetail;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Inside loadUserByUsername {}", username);
		userDetail = userDao.findByEmailId(username);
		if (!Objects.isNull(username))
			return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
		else
			throw new UsernameNotFoundException("user not found");
	}

	public com.inn.cafe.POJO.User getUserDetail() {
		return userDetail;
	}
/*
	 Example of userDetail Object in Memory
	 When fetching from the database, userDetail would look like this:
	{
	    "id": 1,
	    "email": "user@example.com",
	    "password": "$2a$10$X9vQ8bXQ7hXuyA7b5J9rUex.TJz/8W9GJ6r5P7Oy5QbG/JU7KnR5y",  // (Encrypted)
	    "role": "USER",
	    "status": true
	}
*/
}

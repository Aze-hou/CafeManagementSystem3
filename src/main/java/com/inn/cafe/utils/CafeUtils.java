package com.inn.cafe.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CafeUtils {

	public CafeUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {

		return new ResponseEntity<String>("{\"message\":\"" + responseMessage + "\"}", httpStatus);
	}

}

package com.inn.cafe.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.inn.cafe.POJO.Category;

@RequestMapping(path = "/category")
public interface CategoryRest {

	@PostMapping(path = "/add")
	public ResponseEntity<String> addNewCategory(@RequestBody(required = true) Map<String, String> requestMap);

	@GetMapping(path = "/get")
	public ResponseEntity<List<Category>> getAllCategory(@RequestParam(value = "filterValue" ,required = false) String filterValue);
	
	@PostMapping(path = "/update")
	public ResponseEntity<String> updateCategory(@RequestBody(required = true) Map<String, String> requestMap);
	
	
}


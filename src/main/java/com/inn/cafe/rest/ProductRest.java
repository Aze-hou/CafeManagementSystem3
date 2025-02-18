package com.inn.cafe.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inn.cafe.wrapper.ProductWrapper;

@RequestMapping(path = "/product")
public interface ProductRest {

	@PostMapping(path = "/add")
	public ResponseEntity<String> addNewProduct(@RequestBody(required = true) Map<String, String> requestMap);

	@GetMapping(path = "/get")
	public ResponseEntity<List<ProductWrapper>> getAllProduct();

	@PostMapping(path = "/update")
	public ResponseEntity<String> updateProduct(@RequestBody(required = true) Map<String, String> requestMap);

	@PostMapping(path = "/delete/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable(value = "id") Integer id);

	@PostMapping(path = "/updateStatus")
	public ResponseEntity<String> updateStatus(@RequestBody(required = true) Map<String, String> requestMap);

	@GetMapping(path = "/getProductByCategory/{id}")
	public ResponseEntity<List<ProductWrapper>> getProductByCategory(@PathVariable(value = "id") Integer id);

	@GetMapping(path = "/getProductById/{id}")
	public ResponseEntity<ProductWrapper> getProductById(@PathVariable(value = "id") Integer id);

}

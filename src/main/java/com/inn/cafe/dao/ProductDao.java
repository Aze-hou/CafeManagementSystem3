package com.inn.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inn.cafe.POJO.Product;
import com.inn.cafe.wrapper.ProductWrapper;

import jakarta.transaction.Transactional;

@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {

	List<ProductWrapper> getAllProduct();

	@Modifying
	@Transactional
	Integer UpdateStatusProduct(@Param("status") String status, @Param("id") Integer id);

	List<ProductWrapper> getProductByCategory(@Param("id") Integer id);

	ProductWrapper getProductById(@Param("id") Integer id);

}

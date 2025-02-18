package com.inn.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.constents.CafeConstant;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private JwtFilter jwtFilter;

//	addNewCategory
	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (ValidateCategoryMap(requestMap, false)) {
					categoryDao.save(getCategoryFromMap(requestMap, false));
					return CafeUtils.getResponseEntity("Category added succeess", HttpStatus.OK);
				}
			}
			return CafeUtils.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean ValidateCategoryMap(Map<String, String> requestMap, boolean validateId) {
		if (requestMap.containsKey("name")) {
			if (requestMap.containsKey("id") && validateId) {
				return true;
			} else if (!validateId) {
				return true;
			}
		}
		return false;
	}

	private Category getCategoryFromMap(Map<String, String> requestMap, boolean isAdd) {
		Category category = new Category();
		if (isAdd) {
			category.setId(Integer.parseInt(requestMap.get("id")));
		}
		category.setName(requestMap.get("name"));
		return category;
	}

//	getAllCategory
	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
				log.info("inside if ...");
				return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(), HttpStatus.OK);

			}
			return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (ValidateCategoryMap(requestMap, true)) {
					Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
					if (!optional.isEmpty()) {
						categoryDao.save(getCategoryFromMap(requestMap, true));
						return CafeUtils.getResponseEntity("the Category updated succusffully ", HttpStatus.OK);
					} else {
						return CafeUtils.getResponseEntity("the Category does'nt exist", HttpStatus.BAD_REQUEST);
					}

				}
				return CafeUtils.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
			} else {
				return CafeUtils.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// TODO: handle exception
		}
		return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

package com.boostani.backend.api.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boostani.backend.api.persistance.dao.CategoryRepository;
import com.boostani.backend.api.persistance.model.Category;
import com.boostani.backend.api.web.request.CategoryRequest;
import com.boostani.backend.api.web.response.category.CategoryListResponse;
import com.boostani.backend.api.web.response.category.CategoryResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping(path = "/category")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@ApiOperation(value = "Lists all campain categories stored on Local database.", response = CategoryListResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully listed categories"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CategoryListResponse> list() {
		CategoryListResponse response = new CategoryListResponse();

		List<Category> categories = categoryRepository.findAll();
		response.setCategories(categories);

		return new ResponseEntity<CategoryListResponse>(response, HttpStatus.OK);
	}

	@ApiOperation(value = "Saves a specific campain categories to Local database.", response = CategoryResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully created category"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal Server error on backend server") })
	@RequestMapping(value = "/save", method = RequestMethod.POST, headers = "Accept=application/json", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CategoryResponse> save(@Valid @RequestBody CategoryRequest categoryRequest, BindingResult formBinding) {
		if (formBinding.hasErrors()) {
			return null;
		}

		Category category = new Category();

		category.setName(categoryRequest.getName());
		category.setDescription(categoryRequest.getDescription());

		categoryRepository.save(category);
		
		CategoryResponse response = new CategoryResponse();
		response.setCategory(category);
		
		return new ResponseEntity<CategoryResponse>(response, HttpStatus.OK);
	}

}

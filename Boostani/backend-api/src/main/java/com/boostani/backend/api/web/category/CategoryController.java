package com.boostani.backend.api.web.category;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.boostani.backend.api.persistence.dao.CategoryRepository;
import com.boostani.backend.api.persistence.model.Category;

@Controller
@RequestMapping(path = "/category")
public class CategoryController {

	private final CategoryRepository categoryRepository;

	@Inject
	public CategoryController(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<CategoryListResponse> list() {
		CategoryListResponse response = new CategoryListResponse();

		List<Category> categories = categoryRepository.findAll();
		response.setCategories(categories);

		return new ResponseEntity<CategoryListResponse>(response, HttpStatus.OK);
	}

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

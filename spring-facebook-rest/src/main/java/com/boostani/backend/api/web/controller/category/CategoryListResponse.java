package com.boostani.backend.api.web.controller.category;

import java.util.Collections;
import java.util.List;

import com.boostani.backend.api.persistance.model.Category;
import com.boostani.backend.api.web.controller.user.Response;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Loay
 *
 */
public class CategoryListResponse extends Response{

	@ApiModelProperty(notes = "Lists all stored categories on Boostani local database")
	private List<Category> categories= Collections.emptyList();

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	
}

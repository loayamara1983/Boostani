package com.boostani.backend.api.web.controller.category;

import com.boostani.backend.api.persistance.model.Category;
import com.boostani.backend.api.web.controller.user.Response;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Loay
 *
 */
public class CategoryResponse extends Response{

	@ApiModelProperty(notes = "Displays the created category to Boostani local database")
	private Category category;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}

package com.boostani.backend.api.web.category;

import com.boostani.backend.api.persistence.model.Category;
import com.boostani.backend.api.web.Response;

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

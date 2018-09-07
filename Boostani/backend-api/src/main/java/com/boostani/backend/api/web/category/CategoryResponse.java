package com.boostani.backend.api.web.category;

import com.boostani.backend.api.persistence.model.Category;

/**
 * 
 * @author Loay
 *
 */
public class CategoryResponse {

	private Category category;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}

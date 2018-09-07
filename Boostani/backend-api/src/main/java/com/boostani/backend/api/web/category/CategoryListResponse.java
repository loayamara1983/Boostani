package com.boostani.backend.api.web.category;

import java.util.List;

import com.boostani.backend.api.persistence.model.Category;

/**
 * 
 * @author Loay
 *
 */
public class CategoryListResponse {

	private List<Category> categories;

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	
}

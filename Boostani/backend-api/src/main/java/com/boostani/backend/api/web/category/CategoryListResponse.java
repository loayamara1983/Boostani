package com.boostani.backend.api.web.category;

import java.util.Collections;
import java.util.List;

import com.boostani.backend.api.persistence.model.Category;
import com.boostani.backend.api.web.Response;

/**
 * 
 * @author Loay
 *
 */
public class CategoryListResponse extends Response{

	private List<Category> categories= Collections.emptyList();

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	
}

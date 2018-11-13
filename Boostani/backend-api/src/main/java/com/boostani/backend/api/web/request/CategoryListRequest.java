package com.boostani.backend.api.web.request;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.boostani.backend.api.persistance.model.Category;

/**
 * 
 * @author Loay
 *
 */
public class CategoryListRequest {
	
    @NotEmpty
    private List<Category> categories;

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

    
}

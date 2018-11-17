package com.boostani.backend.api.web.request;

import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

import com.boostani.backend.api.persistance.model.Category;

/**
 * 
 * @author Loay
 *
 */
public class CategoryListRequest {
	
    @NotEmpty
    private Set<Category> categories;

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

    
}

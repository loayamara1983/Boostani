package com.boostani.backend.api.web.controller.category;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * @author Loay
 *
 */
public class CategoryRequest {
	
    @NotEmpty
    private String name;

    private String description;

    public CategoryRequest() {
        super();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Category [name=" + name + ", description=" + description + "]";
	}

}

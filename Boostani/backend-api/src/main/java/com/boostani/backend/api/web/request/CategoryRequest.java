package com.boostani.backend.api.web.request;

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
    
    private Long externalId;

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

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	@Override
	public String toString() {
		return "CategoryRequest [name=" + name + ", description=" + description + ", externalId=" + externalId + "]";
	}

	

}

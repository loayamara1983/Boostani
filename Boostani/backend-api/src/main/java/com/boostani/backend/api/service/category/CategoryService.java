package com.boostani.backend.api.service.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.boostani.backend.api.persistance.dao.CategoryRepository;
import com.boostani.backend.api.persistance.model.Category;
import com.boostani.backend.api.web.request.CategoryRequest;

/**
 * 
 * @author Loay
 *
 */

@Service
@CacheConfig(cacheNames="categories")
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	/**
	 * 
	 * @return
	 */
	@Cacheable
	public List<Category> findAll() {
		
		return categoryRepository.findAll();
	}
	
	@CachePut
	public Category save(CategoryRequest categoryRequest) {
		Category category = new Category();

		category.setName(categoryRequest.getName());
		category.setDescription(categoryRequest.getDescription());
		category.setExternalId(categoryRequest.getExternalId());
		
		return categoryRepository.save(category);
	}
}

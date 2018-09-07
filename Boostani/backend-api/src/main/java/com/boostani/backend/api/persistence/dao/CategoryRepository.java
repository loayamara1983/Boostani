package com.boostani.backend.api.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boostani.backend.api.persistence.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {


}

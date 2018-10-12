package com.boostani.backend.api.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boostani.backend.api.persistance.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {


}

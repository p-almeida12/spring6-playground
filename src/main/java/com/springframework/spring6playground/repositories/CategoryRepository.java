package com.springframework.spring6playground.repositories;

import com.springframework.spring6playground.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}

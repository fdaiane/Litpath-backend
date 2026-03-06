package com.litpath.litpath.repository;

import com.litpath.litpath.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByNameContainingIgnoreCase(String name);
    List<Author> findByNationalityIgnoreCase(String nationality);
}
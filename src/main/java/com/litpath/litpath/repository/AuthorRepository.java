package com.litpath.litpath.repository;

import com.litpath.litpath.model.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @EntityGraph(attributePaths = {"books", "books.genres"})
    List<Author> findAll();

    @EntityGraph(attributePaths = {"books", "books.genres"})
    Optional<Author> findById(Long id);

    List<Author> findByNameContainingIgnoreCase(String name);
    List<Author> findByNationalityIgnoreCase(String nationality);

    @Query("""
        SELECT DISTINCT a FROM Author a
        JOIN a.books b
        JOIN b.genres g
        WHERE g.id IN :genreIds
        ORDER BY a.name
    """)
    List<Author> findByGenreIds(List<Long> genreIds);
}
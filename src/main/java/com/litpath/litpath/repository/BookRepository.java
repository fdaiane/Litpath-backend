package com.litpath.litpath.repository;

import com.litpath.litpath.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"genres", "author"})
    List<Book> findAll();

    @EntityGraph(attributePaths = {"genres", "author"})
    Optional<Book> findById(Long id);

    @EntityGraph(attributePaths = {"genres", "author"})
    List<Book> findByAuthorId(Long authorId);

    @EntityGraph(attributePaths = {"genres", "author"})
    List<Book> findByTitleContainingIgnoreCase(String title);

    boolean existsById(Long id);

    // Livros dos gêneros preferidos excluindo os que já estão nas listas do usuário
    @Query("""
        SELECT DISTINCT b FROM Book b
        JOIN b.genres g
        WHERE g.id IN :genreIds
        AND b.id NOT IN (
            SELECT i.book.id FROM UserBookListItem i
            JOIN i.userBookList l
            WHERE l.user.id = :userId
        )
        ORDER BY b.title
    """)
    List<Book> findByGenresAndNotInUserLists(List<Long> genreIds, Long userId);

    // Livros bem avaliados por quem o usuário segue excluindo os que já estão nas listas
    @Query("""
        SELECT DISTINCT b FROM Book b
        JOIN BookReview r ON r.book.id = b.id
        JOIN UserFollow f ON f.following.id = r.user.id
        WHERE f.follower.id = :userId
        AND r.rating >= 4
        AND b.id NOT IN (
            SELECT i.book.id FROM UserBookListItem i
            JOIN i.userBookList l
            WHERE l.user.id = :userId
        )
        ORDER BY r.rating DESC
    """)
    List<Book> findBooksRatedByFollowing(Long userId);

    // Livros mais populares excluindo os que já estão nas listas
    @Query("""
        SELECT b FROM Book b
        LEFT JOIN BookReview r ON r.book.id = b.id
        WHERE b.id NOT IN (
            SELECT i.book.id FROM UserBookListItem i
            JOIN i.userBookList l
            WHERE l.user.id = :userId
        )
        GROUP BY b.id
        ORDER BY COUNT(r.id) DESC, AVG(r.rating) DESC
    """)
    List<Book> findPopularBooksNotInUserLists(Long userId);
}
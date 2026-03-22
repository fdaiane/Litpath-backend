package com.litpath.litpath.service;

import com.litpath.litpath.dto.AuthorResponseDTO;
import com.litpath.litpath.dto.BookResponseDTO;
import com.litpath.litpath.dto.UserFavoritesResponseDTO;
import com.litpath.litpath.exception.BusinessException;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.*;
import com.litpath.litpath.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritesService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final UserFavoriteBookRepository favoriteBookRepository;
    private final UserFavoriteAuthorRepository favoriteAuthorRepository;
    private final BookService bookService;
    private final AuthorService authorService;

    public FavoritesService(UserRepository userRepository,
                             BookRepository bookRepository,
                             AuthorRepository authorRepository,
                             UserFavoriteBookRepository favoriteBookRepository,
                             UserFavoriteAuthorRepository favoriteAuthorRepository,
                             BookService bookService,
                             AuthorService authorService) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.favoriteBookRepository = favoriteBookRepository;
        this.favoriteAuthorRepository = favoriteAuthorRepository;
        this.bookService = bookService;
        this.authorService = authorService;
    }

    
    public UserFavoritesResponseDTO getMyFavorites(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        return buildFavoritesResponse(user.getId());
    }

    
    public UserFavoritesResponseDTO getFavoritesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário não encontrado!");
        }
        return buildFavoritesResponse(userId);
    }

    
    private UserFavoritesResponseDTO buildFavoritesResponse(Long userId) {
        UserFavoritesResponseDTO dto = new UserFavoritesResponseDTO();

        
        List<BookResponseDTO> favoriteBooks = favoriteBookRepository.findByUserId(userId)
                .stream()
                .map(fav -> bookService.getBookById(fav.getBook().getId()))
                .collect(Collectors.toList());

        
        List<AuthorResponseDTO> favoriteAuthors = favoriteAuthorRepository.findByUserId(userId)
                .stream()
                .map(fav -> authorService.getAuthorById(fav.getAuthor().getId()))
                .collect(Collectors.toList());

        dto.setFavoriteBooks(favoriteBooks);
        dto.setFavoriteAuthors(favoriteAuthors);
        return dto;
    }

    
    @Transactional
    public void addFavoriteBook(String email, Long bookId) {
        User user = findUserByEmail(email);

        if (favoriteBookRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new BusinessException("Livro já está nos favoritos!");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado!"));

        UserFavoriteBook favorite = new UserFavoriteBook();
        favorite.setUser(user);
        favorite.setBook(book);
        favoriteBookRepository.save(favorite);
    }

    
    @Transactional
    public void removeFavoriteBook(String email, Long bookId) {
        User user = findUserByEmail(email);

        if (!favoriteBookRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new ResourceNotFoundException("Livro não está nos favoritos!");
        }

        favoriteBookRepository.deleteByUserIdAndBookId(user.getId(), bookId);
    }

    
    @Transactional
    public void addFavoriteAuthor(String email, Long authorId) {
        User user = findUserByEmail(email);

        if (favoriteAuthorRepository.existsByUserIdAndAuthorId(user.getId(), authorId)) {
            throw new BusinessException("Autor já está nos favoritos!");
        }

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado!"));

        UserFavoriteAuthor favorite = new UserFavoriteAuthor();
        favorite.setUser(user);
        favorite.setAuthor(author);
        favoriteAuthorRepository.save(favorite);
    }

    
    @Transactional
    public void removeFavoriteAuthor(String email, Long authorId) {
        User user = findUserByEmail(email);

        if (!favoriteAuthorRepository.existsByUserIdAndAuthorId(user.getId(), authorId)) {
            throw new ResourceNotFoundException("Autor não está nos favoritos!");
        }

        favoriteAuthorRepository.deleteByUserIdAndAuthorId(user.getId(), authorId);
    }

    
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
    }
}
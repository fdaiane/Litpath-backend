package com.litpath.litpath.service;

import com.litpath.litpath.dto.AuthorResponseDTO;
import com.litpath.litpath.dto.BookResponseDTO;
import com.litpath.litpath.dto.RecommendationResponseDTO;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.User;
import com.litpath.litpath.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final UserRepository userRepository;
    private final UserGenrePreferenceRepository userGenrePreferenceRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookService bookService;
    private final AuthorService authorService;

    
    private static final int MAX_RESULTS = 10;

    public RecommendationService(UserRepository userRepository,
                                  UserGenrePreferenceRepository userGenrePreferenceRepository,
                                  BookRepository bookRepository,
                                  AuthorRepository authorRepository,
                                  BookService bookService,
                                  AuthorService authorService) {
        this.userRepository = userRepository;
        this.userGenrePreferenceRepository = userGenrePreferenceRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookService = bookService;
        this.authorService = authorService;
    }

    
    public RecommendationResponseDTO getRecommendations(String email) {
        User user = findUserByEmail(email);

        
        List<Long> genreIds = userGenrePreferenceRepository.findByUserId(user.getId())
                .stream()
                .map(pref -> pref.getGenre().getId())
                .collect(Collectors.toList());

        RecommendationResponseDTO dto = new RecommendationResponseDTO();

        
        if (!genreIds.isEmpty()) {
            List<BookResponseDTO> booksByGenre = bookRepository
                    .findByGenresAndNotInUserLists(genreIds, user.getId())
                    .stream()
                    .limit(MAX_RESULTS)
                    .map(book -> bookService.toDTO(book))
                    .collect(Collectors.toList());
            dto.setBooksByGenre(booksByGenre);
        } else {
            dto.setBooksByGenre(List.of());
        }

        
        List<BookResponseDTO> booksByFollowing = bookRepository
                .findBooksRatedByFollowing(user.getId())
                .stream()
                .limit(MAX_RESULTS)
                .map(book -> bookService.toDTO(book))
                .collect(Collectors.toList());
        dto.setBooksByFollowing(booksByFollowing);

        
        List<BookResponseDTO> popularBooks = bookRepository
                .findPopularBooksNotInUserLists(user.getId())
                .stream()
                .limit(MAX_RESULTS)
                .map(book -> bookService.toDTO(book))
                .collect(Collectors.toList());
        dto.setPopularBooks(popularBooks);

        
        if (!genreIds.isEmpty()) {
            List<AuthorResponseDTO> authorsByGenre = authorRepository
                    .findByGenreIds(genreIds)
                    .stream()
                    .limit(MAX_RESULTS)
                    .map(author -> authorService.toDTO(author))
                    .collect(Collectors.toList());
            dto.setAuthorsByGenre(authorsByGenre);
        } else {
            dto.setAuthorsByGenre(List.of());
        }

        return dto;
    }

    
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
    }
}
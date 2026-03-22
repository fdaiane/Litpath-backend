package com.litpath.litpath.service;

import com.litpath.litpath.dto.BookRequestDTO;
import com.litpath.litpath.dto.BookResponseDTO;
import com.litpath.litpath.dto.GenreDTO;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.Author;
import com.litpath.litpath.model.Book;
import com.litpath.litpath.model.Genre;
import com.litpath.litpath.repository.AuthorRepository;
import com.litpath.litpath.repository.BookRepository;
import com.litpath.litpath.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
    }

    
    @Transactional
    public BookResponseDTO createBook(BookRequestDTO dto) {

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado!"));

        Set<Genre> genres = new HashSet<>();
        if (dto.getGenreIds() != null && !dto.getGenreIds().isEmpty()) {
            genres = dto.getGenreIds().stream()
                    .map(id -> genreRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado!")))
                    .collect(Collectors.toSet());
        }

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setSynopsis(dto.getSynopsis());
        book.setPublicationYear(dto.getPublicationYear());
        book.setCoverUrl(dto.getCoverUrl());
        book.setAuthor(author);
        book.setGenres(genres);

        book = bookRepository.save(book);
        return toDTO(book);
    }

    
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    
    @Transactional(readOnly = true)
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado!"));
        return toDTO(book);
    }

    
    @Transactional(readOnly = true)
    public List<BookResponseDTO> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    
    @Transactional(readOnly = true)
    public List<BookResponseDTO> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    
    @Transactional
    public BookResponseDTO updateBook(Long id, BookRequestDTO dto) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado!"));

        if (dto.getTitle() != null)           book.setTitle(dto.getTitle());
        if (dto.getSynopsis() != null)         book.setSynopsis(dto.getSynopsis());
        if (dto.getPublicationYear() != null)  book.setPublicationYear(dto.getPublicationYear());
        if (dto.getCoverUrl() != null)         book.setCoverUrl(dto.getCoverUrl());

        if (dto.getGenreIds() != null) {
            Set<Genre> genres = dto.getGenreIds().stream()
                    .map(genreId -> genreRepository.findById(genreId)
                            .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado!")))
                    .collect(Collectors.toSet());
            book.setGenres(genres);
        }

        book = bookRepository.save(book);
        return toDTO(book);
    }

    
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Livro não encontrado!");
        }
        bookRepository.deleteById(id);
    }

    
    public BookResponseDTO toDTO(Book book) {

        BookResponseDTO dto = new BookResponseDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setSynopsis(book.getSynopsis());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setCoverUrl(book.getCoverUrl());
        dto.setAuthorName(book.getAuthor().getName());

        if (book.getGenres() != null && !book.getGenres().isEmpty()) {
            List<GenreDTO> genres = book.getGenres().stream()
                    .map(g -> {
                        GenreDTO genreDTO = new GenreDTO();
                        genreDTO.setId(g.getId());
                        genreDTO.setName(g.getName());
                        return genreDTO;
                    })
                    .collect(Collectors.toList());
            dto.setGenres(genres);
        }

        return dto;
    }
}
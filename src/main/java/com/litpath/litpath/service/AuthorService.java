package com.litpath.litpath.service;

import com.litpath.litpath.dto.AuthorRequestDTO;
import com.litpath.litpath.dto.AuthorResponseDTO;
import com.litpath.litpath.dto.BookResponseDTO;
import com.litpath.litpath.dto.GenreDTO;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.Author;
import com.litpath.litpath.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // ===============================
    // CRIAR AUTOR (manual pelo admin)
    // ===============================
    @Transactional
    public AuthorResponseDTO createAuthorFromOpenLibrary(AuthorRequestDTO dto) {

        Author author = new Author();
        author.setName(dto.getName());
        author.setBiography(dto.getBiography());
        author.setNationality(dto.getNationality());
        author.setBirthDate(dto.getBirthDate());
        author.setPhotoUrl(dto.getPhotoUrl());

        author = authorRepository.save(author);

        return toDTO(author);
    }

    // ===============================
    // ATUALIZAR AUTOR
    // ===============================
    @Transactional
    public AuthorResponseDTO updateAuthor(Long id, AuthorRequestDTO dto) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado!"));

        if (dto.getName() != null) {
            author.setName(dto.getName());
        }
        if (dto.getBiography() != null) {
            author.setBiography(dto.getBiography());
        }
        if (dto.getNationality() != null) {
            author.setNationality(dto.getNationality());
        }
        if (dto.getBirthDate() != null) {
            author.setBirthDate(dto.getBirthDate());
        }
        if (dto.getPhotoUrl() != null) {
            author.setPhotoUrl(dto.getPhotoUrl());
        }

        author = authorRepository.save(author);

        return toDTO(author);
    }

    // ===============================
    // LISTAR TODOS
    // ===============================
    public List<AuthorResponseDTO> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // BUSCAR POR ID
    // ===============================
    public AuthorResponseDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado!"));
        return toDTO(author);
    }

    // ===============================
    // DELETAR
    // ===============================
    @Transactional
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Autor não encontrado!");
        }
        authorRepository.deleteById(id);
    }

    // ===============================
    // CONVERSÃO PARA DTO
    // ===============================
    public AuthorResponseDTO toDTO(Author author) {

        AuthorResponseDTO dto = new AuthorResponseDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setBiography(author.getBiography());
        dto.setNationality(author.getNationality());
        dto.setBirthDate(author.getBirthDate());
        dto.setPhotoUrl(author.getPhotoUrl());

        // Livros do autor
        List<BookResponseDTO> books = author.getBooks() != null
                ? author.getBooks().stream()
                        .map(book -> {
                            BookResponseDTO bookDTO = new BookResponseDTO();
                            bookDTO.setId(book.getId());
                            bookDTO.setTitle(book.getTitle());
                            bookDTO.setSynopsis(book.getSynopsis());
                            bookDTO.setPublicationYear(book.getPublicationYear());
                            bookDTO.setCoverUrl(book.getCoverUrl());
                            bookDTO.setAuthorName(author.getName());

                            if (book.getGenres() != null) {
                                bookDTO.setGenres(
                                        book.getGenres().stream()
                                                .map(genre -> {
                                                    GenreDTO g = new GenreDTO();
                                                    g.setId(genre.getId());
                                                    g.setName(genre.getName());
                                                    return g;
                                                })
                                                .collect(Collectors.toList())
                                );
                            }
                            return bookDTO;
                        })
                        .collect(Collectors.toList())
                : new ArrayList<>();

        dto.setBooks(books);

        // Gêneros derivados dos livros (sem duplicatas)
        Set<GenreDTO> genres = author.getBooks() != null
                ? author.getBooks().stream()
                        .filter(book -> book.getGenres() != null)
                        .flatMap(book -> book.getGenres().stream())
                        .map(genre -> {
                            GenreDTO g = new GenreDTO();
                            g.setId(genre.getId());
                            g.setName(genre.getName());
                            return g;
                        })
                        .collect(Collectors.toSet())
                : Set.of();

        dto.setGenres(genres);

        return dto;
    }
}
package com.litpath.litpath.service;

import com.litpath.litpath.dto.GenreDTO;
import com.litpath.litpath.exception.BusinessException;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.Genre;
import com.litpath.litpath.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    // ===============================
    // CRIAR GÊNERO
    // ===============================
    @Transactional
    public GenreDTO createGenre(GenreDTO dto) {
        if (genreRepository.findByName(dto.getName()).isPresent()) {
            throw new BusinessException("Gênero já cadastrado!");
        }

        Genre genre = new Genre();
        genre.setName(dto.getName());
        genre = genreRepository.save(genre);

        return toDTO(genre);
    }

    // ===============================
    // BUSCAR OU CRIAR (usado pelo import da OpenLibrary)
    // ===============================
    @Transactional
    public Genre findOrCreate(String name) {
        return genreRepository.findByName(name)
                .orElseGet(() -> {
                    Genre genre = new Genre();
                    genre.setName(name);
                    return genreRepository.save(genre);
                });
    }

    // ===============================
    // LISTAR TODOS
    // ===============================
    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // DELETAR GÊNERO
    // ===============================
    @Transactional
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gênero não encontrado!");
        }
        genreRepository.deleteById(id);
    }

    // ===============================
    // CONVERSÃO PARA DTO
    // ===============================
    public GenreDTO toDTO(Genre genre) {
        GenreDTO dto = new GenreDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }
}
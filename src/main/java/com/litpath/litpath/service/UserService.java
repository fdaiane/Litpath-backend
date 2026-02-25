package com.litpath.litpath.service;

import com.litpath.litpath.dto.UserRequestDTO;
import com.litpath.litpath.dto.UserResponseDTO;
import com.litpath.litpath.exception.BusinessException;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.User;
import com.litpath.litpath.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {

        validatePasswords(dto);
        validateDuplicatedUser(dto);

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user = userRepository.save(user);

        return toDTO(user);
    }

    // ===============================
    // READ ALL
    // ===============================
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===============================
    // READ BY ID
    // ===============================
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário não encontrado!"));

        return toDTO(user);
    }

    // ===============================
    // DELETE
    // ===============================
    @Transactional
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado!");
        }

        userRepository.deleteById(id);
    }

    // ===============================
    // MÉTODOS AUXILIARES
    // ===============================

    private void validatePasswords(UserRequestDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("As senhas não coincidem!");
        }
    }

    private void validateDuplicatedUser(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado!");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("Nome de usuário já cadastrado!");
        }
    }

    private UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        return dto;
    }
}

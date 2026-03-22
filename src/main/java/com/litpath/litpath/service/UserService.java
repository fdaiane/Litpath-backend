package com.litpath.litpath.service;

import com.litpath.litpath.dto.LoginRequestDTO;
import com.litpath.litpath.dto.UserRequestDTO;
import com.litpath.litpath.dto.UserResponseDTO;
import com.litpath.litpath.exception.BusinessException;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.User;
import com.litpath.litpath.model.UserProfile;
import com.litpath.litpath.repository.*;
import com.litpath.litpath.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserGenrePreferenceRepository userGenrePreferenceRepository;
    private final UserFavoriteBookRepository userFavoriteBookRepository;
    private final UserFavoriteAuthorRepository userFavoriteAuthorRepository;
    private final UserBookListRepository userBookListRepository;
    private final UserFollowRepository userFollowRepository;
    private final BookReviewRepository bookReviewRepository;
    private final ReviewCommentReactionRepository reviewCommentReactionRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewReactionRepository reviewReactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            UserGenrePreferenceRepository userGenrePreferenceRepository,
            UserFavoriteBookRepository userFavoriteBookRepository,
            UserFavoriteAuthorRepository userFavoriteAuthorRepository,
            UserBookListRepository userBookListRepository,
            UserFollowRepository userFollowRepository,
            BookReviewRepository bookReviewRepository,
            ReviewCommentReactionRepository reviewCommentReactionRepository,
            ReviewCommentRepository reviewCommentRepository,
            ReviewReactionRepository reviewReactionRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userGenrePreferenceRepository = userGenrePreferenceRepository;
        this.userFavoriteBookRepository = userFavoriteBookRepository;
        this.userFavoriteAuthorRepository = userFavoriteAuthorRepository;
        this.userBookListRepository = userBookListRepository;
        this.userFollowRepository = userFollowRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.reviewCommentReactionRepository = reviewCommentReactionRepository;
        this.reviewCommentRepository = reviewCommentRepository;
        this.reviewReactionRepository = reviewReactionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    
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

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        userProfileRepository.save(profile);

        return toDTO(user);
    }


    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        return toDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        deleteAllUserData(user);
    }


    public String login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Email ou senha inválidos");
        }

        return jwtService.generateToken(user.getEmail());
    }

    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        return toDTO(user);
    }


    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        deleteAllUserData(user);
    }

    private void deleteAllUserData(User user) {
        Long userId = user.getId();

        reviewCommentReactionRepository.deleteByUserId(userId);

        reviewCommentRepository.findByUserId(userId).forEach(comment ->
            reviewCommentReactionRepository.deleteByCommentId(comment.getId())
        );

        reviewCommentRepository.deleteByUserId(userId);

        reviewReactionRepository.deleteByUserId(userId);

        bookReviewRepository.findByUserId(userId).forEach(review -> {
            reviewReactionRepository.deleteByReviewId(review.getId());
            reviewCommentRepository.deleteByReviewId(review.getId());
        });

        bookReviewRepository.deleteByUserId(userId);

        userBookListRepository.deleteByUserId(userId);

        userFavoriteBookRepository.deleteByUserId(userId);
        userFavoriteAuthorRepository.deleteByUserId(userId);

        userFollowRepository.deleteByFollowerId(userId);
        userFollowRepository.deleteByFollowingId(userId);

        userGenrePreferenceRepository.deleteByUserId(userId);

        userProfileRepository.deleteByUserId(userId);

        userRepository.delete(user);
    }

    
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
        dto.setFirstLogin(user.isFirstLogin());
        return dto;
    }

}
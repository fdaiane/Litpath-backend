package com.litpath.litpath.service;

import com.litpath.litpath.dto.GenreDTO;
import com.litpath.litpath.dto.UserPreferencesRequestDTO;
import com.litpath.litpath.dto.UserProfileRequestDTO;
import com.litpath.litpath.dto.UserProfileResponseDTO;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.Genre;
import com.litpath.litpath.model.User;
import com.litpath.litpath.model.UserGenrePreference;
import com.litpath.litpath.model.UserProfile;
import com.litpath.litpath.repository.GenreRepository;
import com.litpath.litpath.repository.UserGenrePreferenceRepository;
import com.litpath.litpath.repository.UserProfileRepository;
import com.litpath.litpath.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserGenrePreferenceRepository userGenrePreferenceRepository;
    private final GenreRepository genreRepository;

    public UserProfileService(UserRepository userRepository,
                               UserProfileRepository userProfileRepository,
                               UserGenrePreferenceRepository userGenrePreferenceRepository,
                               GenreRepository genreRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userGenrePreferenceRepository = userGenrePreferenceRepository;
        this.genreRepository = genreRepository;
    }

    
    public UserProfileResponseDTO getMyProfile(String email) {
        User user = findUserByEmail(email);
        return toProfileDTO(user);
    }

    
    public UserProfileResponseDTO getProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        return toProfileDTO(user);
    }

    
    @Transactional
    public UserProfileResponseDTO updateProfile(String email, UserProfileRequestDTO dto) {

        User user = findUserByEmail(email);

        
        boolean userChanged = false;
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName());
            userChanged = true;
        }
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName());
            userChanged = true;
        }
        if (userChanged) {
            userRepository.save(user);
        }

        
        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        if (dto.getPhotoUrl() != null) {
            profile.setPhotoUrl(dto.getPhotoUrl());
        }
        if (dto.getBio() != null) {
            profile.setBio(dto.getBio());
        }
        if (dto.getCity() != null) {
            profile.setCity(dto.getCity());
        }
        if (dto.getBirthDate() != null) {
            profile.setBirthDate(dto.getBirthDate());
        }

        userProfileRepository.save(profile);

        return toProfileDTO(user);
    }

    
    @Transactional
    public UserProfileResponseDTO savePreferences(String email, UserPreferencesRequestDTO dto) {

        User user = findUserByEmail(email);

        
        userGenrePreferenceRepository.deleteByUserId(user.getId());

        
        for (Long genreId : dto.getGenreIds()) {
            Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado!"));

            UserGenrePreference preference = new UserGenrePreference();
            preference.setUser(user);
            preference.setGenre(genre);
            userGenrePreferenceRepository.save(preference);
        }

        
        user.setFirstLogin(false);
        userRepository.save(user);

        return toProfileDTO(user);
    }

    
    private UserProfileResponseDTO toProfileDTO(User user) {

        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstLogin(user.isFirstLogin());

        
        userProfileRepository.findByUserId(user.getId()).ifPresent(profile -> {
            dto.setPhotoUrl(profile.getPhotoUrl());
            dto.setBio(profile.getBio());
            dto.setCity(profile.getCity());
            dto.setBirthDate(profile.getBirthDate());
        });

        
        dto.setPreferences(toGenreDTOList(user.getId()));

        return dto;
    }

    
    private List<GenreDTO> toGenreDTOList(Long userId) {
        return userGenrePreferenceRepository.findByUserId(userId)
                .stream()
                .map(pref -> {
                    GenreDTO genreDTO = new GenreDTO();
                    genreDTO.setId(pref.getGenre().getId());
                    genreDTO.setName(pref.getGenre().getName());
                    return genreDTO;
                })
                .collect(Collectors.toList());
    }

    
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
    }
}
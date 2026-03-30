package com.mocktestpro.module.user.service.impl;

import com.mocktestpro.common.exception.ResourceNotFoundException;
import com.mocktestpro.common.util.SecurityUtils;
import com.mocktestpro.module.user.dto.UserRequestDTO;
import com.mocktestpro.module.user.dto.UserResponseDTO;
import com.mocktestpro.module.user.entity.Role;
import com.mocktestpro.module.user.entity.User;
import com.mocktestpro.module.user.repository.UserRepository;
import com.mocktestpro.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getCurrentUserProfile() {
        String keycloakId = securityUtils.getCurrentKeycloakId();
        log.debug("Fetching profile for keycloakId: {}", keycloakId);

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId", keycloakId));

        // REMOVED: userRepository.updateLastLoginAt(keycloakId, LocalDateTime.now());
        return UserResponseDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateCurrentUserProfile(UserRequestDTO request) {
        String keycloakId = securityUtils.getCurrentKeycloakId();
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId", keycloakId));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setProfilePictureUrl(request.getProfilePictureUrl());

        User saved = userRepository.save(user);
        log.info("Profile updated for user: {}", saved.getEmail());
        return UserResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserResponseDTO.fromEntity(user);
    }

    @Override
    @Transactional
    public User findOrCreateUser(String keycloakId, String email, String firstName, String lastName) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    log.info("First login for: {} - creating user record", email);
                    Role role = determineRole(email);
                    User newUser = User.builder()
                            .keycloakId(keycloakId)
                            .email(email)
                            .firstName(firstName != null ? firstName : "")
                            .lastName(lastName != null ? lastName : "")
                            .role(role)
                            .isActive(true)
                            .isEmailVerified(true)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    private Role determineRole(String email) {
        return Role.ROLE_ATTENDEE;
    }
}
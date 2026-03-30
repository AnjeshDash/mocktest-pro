package com.mocktestpro.module.user.service;

import com.mocktestpro.module.user.dto.UserRequestDTO;
import com.mocktestpro.module.user.dto.UserResponseDTO;
import com.mocktestpro.module.user.entity.User;
import java.util.UUID;

public interface UserService {

    UserResponseDTO getCurrentUserProfile();

    UserResponseDTO updateCurrentUserProfile(UserRequestDTO request);

    UserResponseDTO getUserById(UUID id);

    User findOrCreateUser(String keycloakId, String email, String firstName, String lastName);
}
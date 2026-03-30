package com.mocktestpro.module.user.dto;

import com.mocktestpro.module.user.entity.Role;
import com.mocktestpro.module.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserResponseDTO {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Role role;
    private String profilePictureUrl;
    private String phoneNumber;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole())
                .profilePictureUrl(user.getProfilePictureUrl())
                .phoneNumber(user.getPhoneNumber())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
package com.synapsse.backend.service;

import com.synapsse.backend.dto.ChangePasswordRequest;
import com.synapsse.backend.dto.RegisterRequest;
import com.synapsse.backend.dto.UpdateUserProfileRequest;
import com.synapsse.backend.dto.UserProfileResponse;
import com.synapsse.backend.dto.UserSummary;
import com.synapsse.backend.entity.Role;
import com.synapsse.backend.entity.User;
import com.synapsse.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setAddress(request.address());
        user.setCity(request.city());
        user.setProvince(request.province());
        user.setPostalCode(request.postalCode());
        if (request.admin()) {
            user.setRoles(EnumSet.of(Role.ADMIN, Role.CUSTOMER));
        } else {
            user.setRoles(EnumSet.of(Role.CUSTOMER));
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserSummary> listUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserSummary(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRoles(),
                        user.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        User user = findByEmail(email);
        return mapToProfile(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateUserProfileRequest request) {
        User user = findByEmail(email);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setAddress(request.address());
        user.setCity(request.city());
        user.setProvince(request.province());
        user.setPostalCode(request.postalCode());
        return mapToProfile(userRepository.save(user));
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas nuevas no coinciden");
        }
        if (request.newPassword().length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private UserProfileResponse mapToProfile(User user) {
        return new UserProfileResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getAddress(),
                user.getCity(),
                user.getProvince(),
                user.getPostalCode()
        );
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }
}

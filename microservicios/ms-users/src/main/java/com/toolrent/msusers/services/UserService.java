package com.toolrent.msusers.services;

import com.toolrent.msusers.entities.UserEntity;
import com.toolrent.msusers.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Para producción, descomentar y usar
    // @Autowired
    // private PasswordEncoder passwordEncoder;

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    public List<UserEntity> getUsersByRole(UserEntity.UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<UserEntity> getActiveUsers() {
        return userRepository.findByActive(true);
    }

    @Transactional
    public UserEntity createUser(UserEntity user) {
        validateUserData(user);

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // En producción, encriptar password:
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Transactional
    public UserEntity updateUser(Long id, UserEntity user) {
        UserEntity existing = getUserById(id);

        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setRole(user.getRole());

        if (user.getEmail() != null && !user.getEmail().equals(existing.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
            existing.setEmail(user.getEmail());
        }

        return userRepository.save(existing);
    }

    @Transactional
    public UserEntity changePassword(Long id, String newPassword) {
        UserEntity user = getUserById(id);
        // En producción: user.setPassword(passwordEncoder.encode(newPassword));
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity deactivateUser(Long id) {
        UserEntity user = getUserById(id);
        user.setActive(false);
        return userRepository.save(user);
    }

    @Transactional
    public UserEntity activateUser(Long id) {
        UserEntity user = getUserById(id);
        user.setActive(true);
        return userRepository.save(user);
    }

    @Transactional
    public void updateLastLogin(String username) {
        UserEntity user = getUserByUsername(username);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    private void validateUserData(UserEntity user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es requerido");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("El rol es requerido");
        }
    }
}


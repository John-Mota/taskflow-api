package dev.taskflow.service;

import dev.taskflow.domain.User;
import dev.taskflow.dto.UserRequest;
import dev.taskflow.dto.UserResponse;
import dev.taskflow.dto.UserUpdateDTO;
import dev.taskflow.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new WebApplicationException("Email already in use", Response.Status.CONFLICT);
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new WebApplicationException("Username already in use", Response.Status.CONFLICT);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(BcryptUtil.bcryptHash(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : "USER");

        userRepository.persist(user);
        return new UserResponse(user);
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("User not found", Response.Status.NOT_FOUND));
        return new UserResponse(user);
    }

    public List<UserResponse> listUsers(int page, int size) {
        PanacheQuery<User> query = userRepository.findAll().page(page, size);
        return query.list().stream().map(UserResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateDTO updateDTO) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("User not found", Response.Status.NOT_FOUND));

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(updateDTO.getEmail()).isPresent()) {
                throw new WebApplicationException("Email already in use", Response.Status.CONFLICT);
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getUsername() != null && !updateDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(updateDTO.getUsername()).isPresent()) {
                throw new WebApplicationException("Username already in use", Response.Status.CONFLICT);
            }
            user.setUsername(updateDTO.getUsername());
        }

        if (updateDTO.getRole() != null) {
            user.setRole(updateDTO.getRole());
        }

        return new UserResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        boolean deleted = userRepository.deleteById(id);
        if (!deleted) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }
    }
}

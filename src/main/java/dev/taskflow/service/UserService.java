package dev.taskflow.service;

import dev.taskflow.domain.Entity.User;
import dev.taskflow.domain.enuns.Role;
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
import java.util.UUID;
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
        if (userRepository.findByName(request.getName()).isPresent()) {
            throw new WebApplicationException("Name already in use", Response.Status.CONFLICT);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(BcryptUtil.bcryptHash(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.USER);

        userRepository.persist(user);
        return new UserResponse(user);
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("User not found", Response.Status.NOT_FOUND));
        return new UserResponse(user);
    }

    public List<UserResponse> listUsers(int page, int size) {
        PanacheQuery<User> query = userRepository.findAll().page(page, size);
        return query.list().stream().map(UserResponse::new).collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateDTO updateDTO) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new WebApplicationException("User not found", Response.Status.NOT_FOUND));

        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(updateDTO.getEmail()).isPresent()) {
                throw new WebApplicationException("Email already in use", Response.Status.CONFLICT);
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getName() != null && !updateDTO.getName().equals(user.getName())) {
            if (userRepository.findByName(updateDTO.getName()).isPresent()) {
                throw new WebApplicationException("Name already in use", Response.Status.CONFLICT);
            }
            user.setName(updateDTO.getName());
        }

        if (updateDTO.getRole() != null) {
            user.setRole(updateDTO.getRole());
        }

        return new UserResponse(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        boolean deleted = userRepository.deleteById(id);
        if (!deleted) {
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }
    }
}

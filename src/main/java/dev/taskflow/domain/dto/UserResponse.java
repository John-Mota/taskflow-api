package dev.taskflow.domain.dto;

import dev.taskflow.domain.enuns.Role;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String name,
    String email,
    Role role
) {}

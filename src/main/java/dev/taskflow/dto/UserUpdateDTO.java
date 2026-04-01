package dev.taskflow.dto;

import dev.taskflow.domain.enuns.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {

    @Size(min = 3, max = 50, message = "Name must have between 3 and 50 characters")
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    private Role role;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}

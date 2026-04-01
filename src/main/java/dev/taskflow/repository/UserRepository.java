package dev.taskflow.repository;

import dev.taskflow.domain.Entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, UUID> {

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByName(String name) {
        return find("name", name).firstResultOptional();
    }
}

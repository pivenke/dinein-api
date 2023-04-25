package it.dinein.api.dineinapi.repository;

import it.dinein.api.dineinapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByUsername(String username);

    User findUserByEmail(String email);
}

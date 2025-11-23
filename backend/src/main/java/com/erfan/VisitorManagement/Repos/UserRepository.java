package com.erfan.VisitorManagement.Repos;



import com.erfan.VisitorManagement.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import com.erfan.VisitorManagement.Enums.UserType;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String normalizedEmail);

    List<User> findByUserType(UserType userType);
}

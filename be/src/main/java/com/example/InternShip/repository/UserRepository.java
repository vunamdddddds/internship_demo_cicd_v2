package com.example.InternShip.repository;

import com.example.InternShip.entity.User;
import com.example.InternShip.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("""
        SELECT u FROM User u
        WHERE (:role IS NULL OR u.role = :role)
          AND (:keyword IS NULL OR 
              LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
              u.phone LIKE CONCAT('%', :keyword, '%')
           
          )
    """)
    Page<User> searchUsers(@Param("role") Role role, @Param("keyword") String keyword, Pageable pageable);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    Optional<User> findByUsername(String username);

    Optional<User> findByGoogleId(String googleId);

    List<User> findByRole(Role role);
}

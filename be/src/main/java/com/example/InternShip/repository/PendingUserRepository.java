package com.example.InternShip.repository;

import com.example.InternShip.entity.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PendingUserRepository extends JpaRepository<PendingUser, Integer> {
    Optional<PendingUser> findByToken(String token);
}

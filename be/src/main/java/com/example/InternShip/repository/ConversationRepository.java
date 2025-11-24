package com.example.InternShip.repository;

import com.example.InternShip.entity.Conversation;
import com.example.InternShip.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByHr(User hr);

    Optional<Conversation> findByCandidate(User candidate);

    Optional<Conversation> findByGuestId(String guestId);

    List<Conversation> findByHrIsNull();
}

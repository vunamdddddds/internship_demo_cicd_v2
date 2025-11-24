package com.example.InternShip.controller;

import com.example.InternShip.dto.chat.response.ChatMessageResponse;
import com.example.InternShip.dto.chat.response.ConversationListResponse;
import com.example.InternShip.entity.Conversation;
import com.example.InternShip.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ChatService chatService;

    // Endpoint for HR to get their conversations
    @GetMapping("/hr")
    public ResponseEntity<ConversationListResponse> getHrConversations(@AuthenticationPrincipal Jwt jwt) {
        String hrEmail = jwt.getSubject();
        ConversationListResponse conversations = chatService.getConversationsForHr(hrEmail);
        return ResponseEntity.ok(conversations);
    }

    // Endpoint to get messages for a specific conversation
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getConversationMessages(@PathVariable Long conversationId) {
        return ResponseEntity.ok(chatService.getMessagesForConversation(conversationId));
    }

    // Endpoint for a candidate to find or create a conversation
    @PostMapping("/initiate")
    public ResponseEntity<Object> findOrCreateConversation(@AuthenticationPrincipal Jwt jwt) {
        String candidateEmail = jwt.getSubject();
        Conversation conversation = chatService.findOrCreateConversation(candidateEmail);
        return ResponseEntity.ok(Collections.singletonMap("conversationId", conversation.getId()));
    }

    // Endpoint for an HR user to claim an unassigned conversation
    @PostMapping("/{conversationId}/claim")
    public ResponseEntity<Void> claimConversation(@PathVariable Long conversationId, @AuthenticationPrincipal Jwt jwt) {
        String hrEmail = jwt.getSubject();
        chatService.claimConversation(conversationId, hrEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{conversationId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_HR')")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long conversationId,
            @AuthenticationPrincipal Jwt jwt) {
        String hrEmail = jwt.getSubject();
        chatService.deleteConversation(conversationId, hrEmail);
        return ResponseEntity.noContent().build();
    }
}

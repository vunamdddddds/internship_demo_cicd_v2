package com.example.InternShip.controller;

import com.example.InternShip.dto.chat.request.ChatMessageRequest;
import com.example.InternShip.dto.chat.response.ChatMessageResponse;
import com.example.InternShip.entity.Conversation;
import com.example.InternShip.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest chatMessageRequest, Principal principal) {
        String senderIdentifier;
        if (principal != null) {
            senderIdentifier = principal.getName();
        } else if (chatMessageRequest.getGuestId() != null) {
            senderIdentifier = chatMessageRequest.getGuestId();
        } else {
            // Handle error: no sender information
            return;
        }

        ChatMessageResponse savedMessage = chatService.saveMessage(chatMessageRequest, senderIdentifier);

        String destination = "/topic/conversation/" + savedMessage.getConversationId();
        messagingTemplate.convertAndSend(destination, savedMessage);
    }

    @PostMapping("/api/chat/initiate")
    public ResponseEntity<?> findOrCreateConversation(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("User not authenticated.");
        }
        Conversation conversation = chatService.findOrCreateConversation(principal.getName());
        return ResponseEntity.ok(Map.of("conversationId", conversation.getId()));
    }

    @PostMapping("/api/chat/guest/conversation")
    public ResponseEntity<?> findOrCreateGuestConversation(@RequestBody Map<String, String> payload) {
        String guestId = payload.get("guestId");
        if (guestId == null || guestId.isBlank()) {
            return ResponseEntity.badRequest().body("Guest ID is required.");
        }
        Conversation conversation = chatService.findOrCreateGuestConversation(guestId);
        return ResponseEntity.ok(Map.of("conversationId", conversation.getId()));
    }
}

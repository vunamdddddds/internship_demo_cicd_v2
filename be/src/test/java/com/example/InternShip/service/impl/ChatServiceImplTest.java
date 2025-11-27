package com.example.InternShip.service.impl;

import com.example.InternShip.dto.chat.request.ChatMessageRequest;
import com.example.InternShip.dto.chat.response.ChatMessageResponse;
import com.example.InternShip.dto.chat.response.ConversationListResponse;
import com.example.InternShip.entity.ChatMessage;
import com.example.InternShip.entity.Conversation;
import com.example.InternShip.entity.User;
import com.example.InternShip.repository.ChatMessageRepository;
import com.example.InternShip.repository.ConversationRepository;
import com.example.InternShip.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatServiceImpl chatService;

    private User hrUser;
    private User candidateUser;
    private Conversation conversation;
    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        hrUser = new User();
        hrUser.setId(1);
        hrUser.setUsername("hr@example.com");
        hrUser.setFullName("HR User");

        candidateUser = new User();
        candidateUser.setId(2);
        candidateUser.setUsername("candidate@example.com");
        candidateUser.setFullName("Candidate User");

        conversation = new Conversation();
        conversation.setId(1L);
        conversation.setHr(hrUser);
        conversation.setCandidate(candidateUser);
        conversation.setCreatedAt(LocalDateTime.now());

        chatMessage = new ChatMessage();
        chatMessage.setId(1L);
        chatMessage.setConversation(conversation);
        chatMessage.setContent("Test message");
        chatMessage.setSender(candidateUser);
        chatMessage.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getConversationsForHr_happyPath() {
        when(userRepository.findByUsername("hr@example.com")).thenReturn(Optional.of(hrUser));
        when(conversationRepository.findByHr(hrUser)).thenReturn(Collections.singletonList(conversation));
        when(conversationRepository.findByHrIsNull()).thenReturn(Collections.emptyList());
        when(chatMessageRepository.findFirstByConversationIdOrderByCreatedAtDesc(1L)).thenReturn(Optional.of(chatMessage));

        ConversationListResponse response = chatService.getConversationsForHr("hr@example.com");

        assertNotNull(response);
        assertEquals(1, response.getAssignedConversations().size());
    }

    @Test
    void getMessagesForConversation_happyPath() {
        when(chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(1L)).thenReturn(Collections.singletonList(chatMessage));

        List<ChatMessageResponse> response = chatService.getMessagesForConversation(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void saveMessage_happyPath() {
        ChatMessageRequest request = new ChatMessageRequest();
        request.setConversationId(1L);
        request.setContent("New message");

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(userRepository.findByUsernameOrEmail("candidate@example.com")).thenReturn(Optional.of(candidateUser));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        ChatMessageResponse response = chatService.saveMessage(request, "candidate@example.com");

        assertNotNull(response);
        assertEquals("Test message", response.getContent());
    }

    @Test
    void findOrCreateConversation_happyPath() {
        when(userRepository.findByUsername("candidate@example.com")).thenReturn(Optional.of(candidateUser));
        when(conversationRepository.findByCandidate(candidateUser)).thenReturn(Optional.of(conversation));

        Conversation response = chatService.findOrCreateConversation("candidate@example.com");

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void findOrCreateGuestConversation_happyPath() {
        when(conversationRepository.findByGuestId("guest123")).thenReturn(Optional.of(conversation));

        Conversation response = chatService.findOrCreateGuestConversation("guest123");

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void claimConversation_happyPath() {
        conversation.setHr(null);
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(userRepository.findByUsername("hr@example.com")).thenReturn(Optional.of(hrUser));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);

        Conversation response = chatService.claimConversation(1L, "hr@example.com");

        assertNotNull(response);
        assertEquals(hrUser, response.getHr());
    }

    @Test
    void deleteConversation_happyPath() {
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(userRepository.findByUsername("hr@example.com")).thenReturn(Optional.of(hrUser));

        chatService.deleteConversation(1L, "hr@example.com");
    }
}

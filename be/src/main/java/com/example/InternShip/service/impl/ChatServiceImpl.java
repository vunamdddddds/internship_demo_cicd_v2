
package com.example.InternShip.service.impl;
import com.example.InternShip.dto.chat.request.ChatMessageRequest;
import com.example.InternShip.dto.chat.response.ChatMessageResponse;
import com.example.InternShip.dto.chat.response.ConversationListResponse;
import com.example.InternShip.dto.chat.response.ConversationResponse;
import com.example.InternShip.entity.ChatMessage;
import com.example.InternShip.entity.Conversation;
import com.example.InternShip.entity.User;
import com.example.InternShip.exception.ErrorCode;
import com.example.InternShip.repository.ChatMessageRepository;
import com.example.InternShip.repository.ConversationRepository;
import com.example.InternShip.repository.UserRepository;
import com.example.InternShip.service.ChatService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ConversationListResponse getConversationsForHr(String hrUsername) {
        User hrUser = userRepository.findByUsername(hrUsername)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_EXISTED.getMessage()));

        List<Conversation> assigned = conversationRepository.findByHr(hrUser);
        List<Conversation> unassigned = conversationRepository.findByHrIsNull();

        List<ConversationResponse> assignedResponses = assigned.stream()
                .map(this::mapToConversationResponse)
                .collect(Collectors.toList());

        List<ConversationResponse> unassignedResponses = unassigned.stream()
                .map(this::mapToConversationResponse)
                .collect(Collectors.toList());

        return ConversationListResponse.builder()
                .assignedConversations(assignedResponses)
                .unassignedConversations(unassignedResponses)
                .build();
    }

    @Override
    public List<ChatMessageResponse> getMessagesForConversation(Long conversationId) {
        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::mapToChatMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageResponse saveMessage(ChatMessageRequest request, String senderIdentifier) {
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENTOR_NOT_EXISTED.getMessage()));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setConversation(conversation);
        chatMessage.setContent(request.getContent());

        // Check if the sender is a registered user or a guest
        Optional<User> senderOptional = userRepository.findByUsernameOrEmail(senderIdentifier);
        if (senderOptional.isPresent()) {
            chatMessage.setSender(senderOptional.get());
        } else {
            // This is a guest message, sender remains null
        }

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        return mapToChatMessageResponse(savedMessage);
    }

    @Override
    public Conversation findOrCreateConversation(String candidateUsername) {
        User candidate = userRepository.findByUsername(candidateUsername)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_EXISTED.getMessage()));

        return conversationRepository.findByCandidate(candidate)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    newConversation.setCandidate(candidate);
                    newConversation.setHr(null); // HR is null until claimed
                    Conversation savedConversation = conversationRepository.save(newConversation);

                    // Notify all HRs about the new unassigned conversation
                    messagingTemplate.convertAndSend("/topic/conversations/unassigned", mapToConversationResponse(savedConversation));

                    return savedConversation;
                });
    }

    @Override
    public Conversation findOrCreateGuestConversation(String guestId) {
        return conversationRepository.findByGuestId(guestId)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    newConversation.setGuestId(guestId);
                    newConversation.setHr(null); // HR is null until claimed
                    Conversation savedConversation = conversationRepository.save(newConversation);

                    // Notify all HRs about the new unassigned conversation
                    messagingTemplate.convertAndSend("/topic/conversations/unassigned", mapToConversationResponse(savedConversation));

                    return savedConversation;
                });
    }

    @Override
    @Transactional
    public Conversation claimConversation(Long conversationId, String hrEmail) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CONVERSATION_NOT_EXISTS.getMessage()));

        if (conversation.getHr() != null) {
            // Conversation already claimed
            return conversation;
        }

        User hrUser = userRepository.findByUsername(hrEmail)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_EXISTED.getMessage()));

        conversation.setHr(hrUser);
        Conversation claimedConversation = conversationRepository.save(conversation);

        // Notify all clients that this conversation has been claimed
        messagingTemplate.convertAndSend("/topic/conversations/claimed", mapToConversationResponse(claimedConversation));

        return claimedConversation;
    }

    @Override
    @Transactional
    public void deleteConversation(Long conversationId, String hrUsername) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CONVERSATION_NOT_EXISTS.getMessage()));

        User hrUser = userRepository.findByUsername(hrUsername)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_EXISTED.getMessage()));

        // Ensure the HR user owns this conversation
        if (conversation.getHr() == null || !conversation.getHr().getId().equals(hrUser.getId())) {
            throw new AccessDeniedException(ErrorCode.NOT_PERMISSION.getMessage());
        }

        // Delete all messages in the conversation first
        chatMessageRepository.deleteAllByConversation(conversation);

        // Then delete the conversation
        conversationRepository.delete(conversation);
    }

    private ConversationResponse mapToConversationResponse(Conversation conversation) {
        Optional<ChatMessage> lastMessageOpt = chatMessageRepository.findFirstByConversationIdOrderByCreatedAtDesc(conversation.getId());

        String lastMessageContent = lastMessageOpt.map(ChatMessage::getContent).orElse("No messages yet.");
        var lastMessageTimestamp = lastMessageOpt.map(ChatMessage::getCreatedAt).orElse(conversation.getCreatedAt());

        String candidateName = "Guest";
        if (conversation.getCandidate() != null) {
            candidateName = conversation.getCandidate().getFullName();
        }

        return ConversationResponse.builder()
                .id(conversation.getId())
                .candidateName(candidateName)
                .hrId(  conversation.getHr()==null?null:  conversation.getHr().getId())
                .lastMessage(lastMessageContent)
                .lastMessageTimestamp(lastMessageTimestamp)
                .unreadCount(0) // Placeholder for unread count
                .build();
    }


    private ChatMessageResponse mapToChatMessageResponse(ChatMessage chatMessage) {
        ChatMessageResponse.ChatMessageResponseBuilder builder = ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .conversationId(chatMessage.getConversation().getId())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt());

        if (chatMessage.getSender() != null) {
            builder.senderId(chatMessage.getSender().getId())
                   .senderName(chatMessage.getSender().getFullName());
        } else {
            builder.guestId(chatMessage.getConversation().getGuestId());
        }

        return builder.build();
    }
}

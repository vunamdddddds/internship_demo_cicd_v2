package com.example.InternShip.service;

import com.example.InternShip.dto.chat.request.ChatMessageRequest;
import com.example.InternShip.dto.chat.response.ChatMessageResponse;
import com.example.InternShip.dto.chat.response.ConversationListResponse;
import com.example.InternShip.entity.Conversation;

import java.util.List;

public interface ChatService {
    ConversationListResponse getConversationsForHr(String hrEmail);

    List<ChatMessageResponse> getMessagesForConversation(Long conversationId);

    ChatMessageResponse saveMessage(ChatMessageRequest request, String senderEmail);

    Conversation findOrCreateConversation(String candidateUsername);

    Conversation findOrCreateGuestConversation(String guestId);

    Conversation claimConversation(Long conversationId, String hrEmail);

    void deleteConversation(Long conversationId, String hrEmail);
}

package com.example.InternShip.dto.chat.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConversationListResponse {
    private List<ConversationResponse> assignedConversations;
    private List<ConversationResponse> unassignedConversations;
}

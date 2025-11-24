package com.example.InternShip.dto.chat.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConversationResponse {
    private Long id;
    private String candidateName;
    private Integer hrId;
    private String lastMessage;
    private LocalDateTime lastMessageTimestamp;
    private int unreadCount;
}

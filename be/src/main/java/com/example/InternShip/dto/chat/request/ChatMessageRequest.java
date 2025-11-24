package com.example.InternShip.dto.chat.request;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long conversationId;
    private String content;
    private String guestId;
}

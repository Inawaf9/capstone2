package com.nawaf.meetingpoint.Client;

import com.nawaf.meetingpoint.DTO.OpenRouter.AiRankingResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class OpenRouterClient {

    private final ChatClient chatClient;

    public OpenRouterClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public AiRankingResponse rankPlaces(String prompt) {
        return chatClient.prompt().user(prompt).call().entity(AiRankingResponse.class);
    }
}
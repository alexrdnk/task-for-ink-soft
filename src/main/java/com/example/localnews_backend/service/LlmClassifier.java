package com.example.localnews_backend.service;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LlmClassifier {
    private final OpenAiService openAi;

    public LlmClassifier(@Value("${openai.api-key}") String apiKey) {
        this.openAi = new OpenAiService(apiKey);
    }

    public String classify(String title, String body) {
        String prompt = """
      You are an assistant.
      Title: %s
      Body: %s

      Respond JSON: { "scope":"LOCAL" or "GLOBAL", "cityState":"City, ST" or "N/A" }
      """.formatted(title, body);

        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model("gpt-4o-mini")      // ← select GPT-4o-mini
                .messages(List.of(new ChatMessage("user", prompt)))
                .maxTokens(60)
                .build();

        return openAi.createChatCompletion(req)
                .getChoices().get(0).getMessage().getContent();
    }
}


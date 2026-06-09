package com.nhnacademy.springaiflyschedulepractice.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SimpleChatService {

    private final ChatClient ollamaChatClient;
    private final ChatClient geminiChatClient;

    @Autowired
    public SimpleChatService(
            @Qualifier("ollamaChatClientBuilder") ChatClient.Builder ollamaChatClientBuilder,
            @Qualifier("geminiChatClientBuilder") ChatClient.Builder geminiChatClientBuilder){
        this.ollamaChatClient = ollamaChatClientBuilder.build();
        this.geminiChatClient = geminiChatClientBuilder.build();
    }

    public String askOllama(String question){
        return ollamaChatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    public String askGemini(String question){
        return geminiChatClient.prompt()
                .user(question)
                .call()
                .content();
    }

}

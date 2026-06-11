package com.nhnacademy.springaiflyschedulepractice.config;

import com.nhnacademy.springaiflyschedulepractice.logging.ChatLoggingAdvisor;
import com.nhnacademy.springaiflyschedulepractice.logging.ToolLoggingCallback;
import com.nhnacademy.springaiflyschedulepractice.tool.AirlineInfoTool;
import com.nhnacademy.springaiflyschedulepractice.tool.AirportInfoTool;
import com.nhnacademy.springaiflyschedulepractice.tool.FlightSearchTool;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

    private final ChatLoggingAdvisor chatLoggingAdvisor;

    @Bean
    @Primary
    public ChatClient.Builder ollamaChatClientBuilder(
            @Qualifier("ollamaChatModel") ChatModel ollamaChatModel,
            FlightSearchTool flightSearchTool,
            AirlineInfoTool airlineInfoTool,
            AirportInfoTool airportInfoTool) {


        List<ToolCallback> toolCallbacks = monitoringCallbacks(
                ToolCallbacks.from(flightSearchTool, airportInfoTool, airlineInfoTool));

        return ChatClient.builder(ollamaChatModel)
                .defaultToolCallbacks(toolCallbacks)
                .defaultAdvisors(chatLoggingAdvisor);
    }

    @Bean
    public ChatClient.Builder geminiChatClientBuilder(
            @Qualifier("googleGenAiChatModel") ChatModel geminiChatModel,
            FlightSearchTool flightSearchTool,
            AirlineInfoTool airlineInfoTool,
            AirportInfoTool airportInfoTool) {
        List<ToolCallback> toolCallbacks = monitoringCallbacks(
                ToolCallbacks.from(flightSearchTool, airportInfoTool, airlineInfoTool));

        return ChatClient.builder(geminiChatModel)
                .defaultToolCallbacks(toolCallbacks)
                .defaultAdvisors(chatLoggingAdvisor);
    }

    private List<ToolCallback> monitoringCallbacks(ToolCallback[] callbacks) {
        return Arrays.stream(callbacks)
                .map(callback -> (ToolCallback) new ToolLoggingCallback(callback))
                .toList();
    }
}

package com.panicatthedebug.pathsync.config;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.core.util.HttpClientOptions;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.azure.openai.autoconfigure.AzureOpenAIClientBuilderCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AIConfig {
    @Value("${spring.ai.azure.openai.endpoint}")
    private String endpoint;

    @Value("${spring.ai.azure.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.azure.openai.chat.options.deployment-name}")
    private String deploymentName;

    @Value("${spring.ai.azure.openai.chat.options.temperature}")
    private Double temperature;

    @Bean
    public ChatClient openAiChatClient() {
        OpenAIClientBuilder openAIClientBuilder = new OpenAIClientBuilder()
                .httpClient(new NettyAsyncHttpClientBuilder()
                        .connectTimeout(Duration.ofSeconds(30)).build())
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey));

        AzureOpenAiChatOptions options = AzureOpenAiChatOptions.builder()
                .deploymentName(deploymentName)
                .temperature(temperature)
                .build();

        return ChatClient.create(AzureOpenAiChatModel.builder()
                .openAIClientBuilder(openAIClientBuilder)
                .defaultOptions(options).build());
    }

}

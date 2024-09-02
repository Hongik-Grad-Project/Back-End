package trackers.demo.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class ChatGPTConfig {

    @Value("${ChatGPT.model}")
    private String model;

    @Value("${ChatGPT.api-url.completion}")
    private String completionApiUrl;

    @Value("${ChatGPT.api-url.assistant}")
    private String assistantApiUrl;

    @Value("${ChatGPT.api-url.message}")
    private String messageApiUrl;

    @Value("${ChatGPT.api-url.run}")
    private String runApiUrl;

    @Value("${ChatGPT.api-url.thread}")
    private String threadApiUrl;

    @Value("${ChatGPT.api-url.retrieve}")
    private String retrieveApiUrl;

    @Value("${ChatGPT.api-url.thread-messages}")
    private String threadMessageApiUrl;

    @Value("${ChatGPT.api-url.delete-messages}")
    private String deleteMessageApiUrl;

    @Value("${ChatGPT.api-key}")
    private String openaiApiKey;

    @Bean
    public RestTemplate completionTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            return execution.execute(request, body);
        } );

        return restTemplate;
    }

    @Bean
    public RestTemplate assistantTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            request.getHeaders().add("OpenAI-Beta", "assistants=v2");
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}

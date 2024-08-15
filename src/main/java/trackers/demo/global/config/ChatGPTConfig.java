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

    @Value("${ChatGPT.api-url}")
    private String apiURL;

    @Value("${ChatGPT.api-key}")
    private String openaiApiKey;

    @Bean
    public RestTemplate template(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            return execution.execute(request, body);
        } );

        return restTemplate;
    }
}

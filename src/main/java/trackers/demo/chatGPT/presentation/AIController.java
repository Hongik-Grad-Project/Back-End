package trackers.demo.chatGPT.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import trackers.demo.chatGPT.dto.request.ChatGPTRequest;
import trackers.demo.chatGPT.dto.response.ChatGPTResponse;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Value("${ChatGPT.model}")
    private String model;

    @Value("${ChatGPT.api-url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @GetMapping ("/chat")
    public ResponseEntity<String> chat(@RequestBody String prompt){
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGPTResponse chatGPTResponse = template.postForObject(apiURL, request, ChatGPTResponse.class);
        String response = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        return ResponseEntity.ok().body(response);
    }
}

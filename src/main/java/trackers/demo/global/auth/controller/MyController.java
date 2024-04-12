package trackers.demo.global.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/oauth2/my")
    public String myAPI(){
        return "my route";
    }
}

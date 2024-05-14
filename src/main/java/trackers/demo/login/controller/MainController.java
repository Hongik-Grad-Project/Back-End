package trackers.demo.login.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/oauth2/home")
    public String mainAPI(){
        return "main route";
    }


}

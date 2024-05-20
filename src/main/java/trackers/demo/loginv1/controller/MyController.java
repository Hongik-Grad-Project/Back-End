package trackers.demo.loginv1.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class MyController {

    @Value("${server.env}")
    private String env;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.address}")
    private String address;

    @Value("${serverName}")
    private String serverName;


    @GetMapping("/hc")
    public ResponseEntity<?> healthcheck(){
        Map<String, String> responseData = new TreeMap<>();

        responseData.put("serverName", serverName);
        responseData.put("serverAddress", address);
        responseData.put("serverPort", serverPort);
        responseData.put("env", env);

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/env")
    public ResponseEntity<?> getEnv(){

        return ResponseEntity.ok(env);
    }
}

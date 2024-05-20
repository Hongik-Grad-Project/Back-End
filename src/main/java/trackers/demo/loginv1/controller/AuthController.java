//package trackers.demo.loginv1.controller;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//import trackers.demo.loginv1.service.Authservice;
//
//@RestController
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final Authservice authservice;
//    @PostMapping("/reissue")
//    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
//        return authservice.reissue(request, response);
//    }
//
//}

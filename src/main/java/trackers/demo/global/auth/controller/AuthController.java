package trackers.demo.global.auth.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import trackers.demo.global.auth.jwt.JWTUtil;
import trackers.demo.global.auth.service.Authservice;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final Authservice authservice;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
        return authservice.reissue(request, response);
    }

}

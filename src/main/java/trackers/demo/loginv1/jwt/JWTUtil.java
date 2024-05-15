//package trackers.demo.loginv1.jwt;
//
//import io.jsonwebtoken.Jwts;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//@Component
//public class JWTUtil {
//
//    private SecretKey secretKey;
//
//    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
//        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
//    }
//
//    public String getAuthkey(String token) {
//
//        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("authKey", String.class);
//    }
//
//    public String getRole(String token) {
//
//        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
//    }
//
//    public String getCategory(String token){
//        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
//    }
//
//    public Boolean isExpired(String token) {
//
//        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
//    }
//
//    public String createJwt(String category, String authKey, String role, Long expiredMs) {
//
//        return Jwts.builder()
//                .claim("category", category)
//                .claim("authKey", authKey)
//                .claim("role", role)
//                .issuer("Trackers")
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + expiredMs))
//                .signWith(secretKey)
//                .compact();
//    }
//
//}
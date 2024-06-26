//package trackers.demo.global.config;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.logout.LogoutFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import trackers.demo.loginv2.domain.repository.RefreshTokenRepository;
//import trackers.demo.loginv1.jwt.CustomLogoutFilter;
//import trackers.demo.loginv1.jwt.JWTFilter;
//import trackers.demo.loginv1.jwt.JWTUtil;
//import trackers.demo.loginv1.oauth.CustomOAuth2UserService;
//import trackers.demo.loginv1.oauth.CustomSuccessHandler;
//
//import java.util.Collections;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    private final CustomOAuth2UserService customOAuth2UserService;
//    private final CustomSuccessHandler customSuccessHandler;
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final JWTUtil jwtUtil;
//
////    private final AuthenticationConfiguration authenticationConfiguration;
//
//    //AuthenticationManager Bean 등록
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//
//        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//
//                        CorsConfiguration configuration = new CorsConfiguration();
//
//                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//                        configuration.setAllowedMethods(Collections.singletonList("*"));
//                        configuration.setAllowCredentials(true);
//                        configuration.setAllowedHeaders(Collections.singletonList("*"));
//                        configuration.setMaxAge(3600L);
//
//                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
//                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
//
//                        return configuration;
//                    }
//                }));
//
//        // csrf disable
//        http.csrf((auth) -> auth.disable());
//
//        // Form 로그인 방식 disable
//        http.formLogin((auth) -> auth.disable());
//
//        // http basic 인증 방식 disable
//        http.httpBasic((auth) -> auth.disable());
//
//        // JWT 필터 등록
//        http.addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);
//
//        // 로그아웃 필터 등록
//        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class);
//
//        // oauth2 로그인
//        http.oauth2Login((oauth2) -> oauth2
//                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
//                        .userService(customOAuth2UserService))
//                .successHandler(customSuccessHandler));
//
//        // 경로별 인가 작업
//        http.authorizeHttpRequests((auth) -> auth
//                .requestMatchers("/login", "/reissue", "/oauth2/home").permitAll()
//                .anyRequest().authenticated()); // 그 외의 요청은 모두 로그인을 해야함
//
//        // 세션 설정
//        http.sessionManagement((session) -> session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }
//}

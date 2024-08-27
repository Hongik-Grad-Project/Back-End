package trackers.demo.admin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import trackers.demo.admin.infrastructure.BCryptPasswordEncoder;
import trackers.demo.admin.infrastructure.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

package live.dgrr.global.security;

import live.dgrr.global.config.url.UrlConfig;
import live.dgrr.global.security.jwt.JwtAccessDeniedHandler;
import live.dgrr.global.security.jwt.JwtAuthenticationEntryPoint;
import live.dgrr.global.security.jwt.JwtAuthenticationFilter;
import live.dgrr.global.security.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
public class SecurityConfig {

    private final String FRONT_URL;

    public SecurityConfig(UrlConfig urlConfig) {
        this.FRONT_URL = urlConfig.getFront();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, CorsConfigurationSource corsConfigurationSource) throws Exception {
        System.out.println(http.toString());

        http
                .formLogin(fl -> fl.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .exceptionHandling(eh -> eh.authenticationEntryPoint(new JwtAuthenticationEntryPoint()).accessDeniedHandler(new JwtAccessDeniedHandler()))
                .authorizeRequests((requests) -> requests
                        .requestMatchers("/api/v1/kakao/**", "/ws", "/api/v1/member", "/api/v1/member/", "/api/v1/member/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(TokenProvider tokenProvider) {
        return new JwtAuthenticationFilter(tokenProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(FRONT_URL)); // 허용할 Origin 지정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
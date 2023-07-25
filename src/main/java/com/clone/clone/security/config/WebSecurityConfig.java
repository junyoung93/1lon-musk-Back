package com.clone.clone.security.config;


import com.clone.clone.security.impl.UserDetailsServiceImpl;
import com.clone.clone.security.filter.JwtAuthenticationFilter;
import com.clone.clone.security.filter.JwtAuthorizationFilter;
import com.clone.clone.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity  //spring security
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    //securityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //csrf 사용 안함. RESTful api에서는 사용하지 않아도 되며 성능 면에서 사용 안함
        http.csrf((csrf) -> csrf.disable());

        // HTTP 요청에 대한 접근 제어를 설정
        http.authorizeHttpRequests((authorizeHttpRequests) ->        //람다식 표현 -> http 요청에 대한 접근 제어를 설정.
                        authorizeHttpRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/**").permitAll()
                                .anyRequest().authenticated()       //anyRequest:모든 요청에 대해 적용할 규칙 적용.
        );
        return http.build();    //HttpSecurity 객체를 빌드 하고 반환합니다.
    }

}

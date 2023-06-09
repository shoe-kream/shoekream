package com.shoekream.common.config;

import com.shoekream.common.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET,"/api/v1/brands/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/v1/products/**").permitAll()

                .requestMatchers(HttpMethod.PATCH,"/api/v1/users/password").authenticated()
                .requestMatchers(HttpMethod.PATCH,"/api/v1/users/nickname").authenticated()
                .requestMatchers(HttpMethod.DELETE,"/api/v1/users").authenticated()
                .requestMatchers(HttpMethod.PATCH,"/api/v1/users/account").authenticated()
                .requestMatchers(HttpMethod.GET,"/api/v1/users/account").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/v1/users/addresses").authenticated()
                .requestMatchers(HttpMethod.GET,"/api/v1/users/addresses").authenticated()
                .requestMatchers(HttpMethod.DELETE,"/api/v1/users/addresses/**").authenticated()
                .requestMatchers(HttpMethod.PATCH,"/api/v1/users/addresses/**").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/v1/users/points/**").authenticated()
                .requestMatchers(HttpMethod.GET,"/api/v1/users/points/**").authenticated()

                .requestMatchers(HttpMethod.POST,"/api/v1/brands").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,"/api/v1/brands/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/v1/brands/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,"/api/v1/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,"/api/v1/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/v1/products/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.GET,"/api/v1/carts/**").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/v1/carts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE,"/api/v1/carts/**").authenticated()
                .anyRequest().permitAll()

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}

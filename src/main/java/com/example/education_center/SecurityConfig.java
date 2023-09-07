package com.example.education_center;

import com.example.education_center.filter.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    JwtTokenFilter jwtTokenFilter;

    //TODO: sua lai array cho tung doi tuong
    //Ngoai doi tuong nay ra thi cho vao tat
    String []teacherAllows={"/course/**"};
    String []learnerAllows={"/room/"};
    String []adminAllows={"/course/**"};

    String []needAuth={"/course/"};
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    public void config(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests((authz)->authz
                        .requestMatchers(needAuth).authenticated()
                        .requestMatchers(teacherAllows).hasRole("TEACHER")
                .requestMatchers(learnerAllows).hasRole("LEARNER")
                .requestMatchers(adminAllows).hasRole("ADMIN")
                .anyRequest().permitAll()).httpBasic(withDefaults())
                .csrf(AbstractHttpConfigurer::disable) //de dung jwt
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.NEVER));
        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }



}

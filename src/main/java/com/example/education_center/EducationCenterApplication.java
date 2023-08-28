package com.example.education_center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EducationCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(EducationCenterApplication.class, args);
    }

}

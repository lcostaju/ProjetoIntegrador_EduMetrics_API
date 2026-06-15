package br.edu.iftm.edumetrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EduMetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduMetricsApplication.class, args);
    }

}

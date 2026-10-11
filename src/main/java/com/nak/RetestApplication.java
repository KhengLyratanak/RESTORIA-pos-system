package com.nak.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class RetestApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(RetestApplication.class, args);
    }

}

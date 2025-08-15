package com.alkemy.marvel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.alkemy.marvel", "com.alkemy.marvel.client"})
public class MarvelRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarvelRestApiApplication.class, args);
    }
}
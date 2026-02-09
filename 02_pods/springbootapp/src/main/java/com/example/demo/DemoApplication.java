package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @org.springframework.beans.factory.annotation.Value("${MY_ACCOUNT:default}")
    private String myAccount;

    @org.springframework.beans.factory.annotation.Value("${MY_PASSWORD:default}")
    private String myPassword;

    @GetMapping("/")
    public String home() {
        return "myAccount: " + myAccount + ", myPassword: " + myPassword;
    }
}

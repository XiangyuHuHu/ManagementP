package com.example.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.platform.mapper")
@EnableScheduling
public class PlatformServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformServerApplication.class, args);
    }

}

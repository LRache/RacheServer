package com.rache.racheserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rache.racheserver.mapper")
public class RacheServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RacheServerApplication.class, args);
    }

}

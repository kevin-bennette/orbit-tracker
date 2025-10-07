package com.gaiaorbittracker.orbittracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@SpringBootApplication
public class OrbitTrackerApplication {

    public String index(){
        return "index.html";
    }
    public static void main(String[] args) {
        SpringApplication.run(OrbitTrackerApplication.class, args);
    }


}

package com.vibran;

import com.vibran.domain.user.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FleetManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleetManagementSystemApplication.class, args);

    }

}

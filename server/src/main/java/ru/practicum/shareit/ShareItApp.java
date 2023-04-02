package ru.practicum.shareit;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class ShareItApp {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }

}

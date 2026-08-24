package ru.seledcov;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.seledcov.repository.ClientRepository;

@SpringBootApplication
public class FitnessCRM {

    public static void main(String[] args) {
        SpringApplication.run(FitnessCRM.class, args);
    }
}

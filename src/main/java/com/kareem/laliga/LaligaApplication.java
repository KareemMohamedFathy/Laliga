package com.kareem.laliga;

import com.kareem.laliga.service.CsvImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LaligaApplication {

	public static void main(String[] args) {
        SpringApplication.run(LaligaApplication.class, args);

	}
    @Bean
    CommandLineRunner importData(CsvImportService csvImportService) {
        return args -> csvImportService.importMatches();
    }

}

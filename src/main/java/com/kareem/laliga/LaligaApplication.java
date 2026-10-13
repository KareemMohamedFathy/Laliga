package com.kareem.laliga;

import com.kareem.laliga.dto.FootballMatchesResponse;
import com.kareem.laliga.service.CsvImportService;
import com.kareem.laliga.service.FootballMatchesData;
import org.apache.catalina.User;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LaligaApplication {

	public static void main(String[] args) {
        SpringApplication.run(LaligaApplication.class, args);

	}
    @Bean
    CommandLineRunner importData(CsvImportService csvImportService) {
        return args -> csvImportService.importMatches();
    }
//    @Bean
//    ApplicationRunner showUser(FootballMatchesData footballMatchesData) {
//
//        return args -> {
//            FootballMatchesResponse res = footballMatchesData.getFootballMatches();
//            System.out.println(res.matches().size());
//        };
//    }
}

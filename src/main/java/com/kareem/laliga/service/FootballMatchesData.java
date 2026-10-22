package com.kareem.laliga.service;


import com.kareem.laliga.dto.FootballMatchesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class FootballMatchesData {
    private final RestClient restClient;
    public FootballMatchesData(RestClient.Builder clientBuilder,@Value("${FOOTBALL_API_TOKEN:}") String apiToken) {
        this.restClient = clientBuilder.baseUrl("https://api.football-data.org")
                .defaultHeader("X-Auth-Token", apiToken)
                .build();
    }

    public FootballMatchesResponse getFootballMatches(Integer season,String status){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("v4/competitions/PD/matches")
                .queryParam("season", season).queryParam("status", status).build())
                .retrieve().body(FootballMatchesResponse.class)
                ;    }

}

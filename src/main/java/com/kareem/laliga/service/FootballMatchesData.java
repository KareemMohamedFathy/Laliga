package com.kareem.laliga.service;


import com.kareem.laliga.dto.FootballMatchesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Service
public class FootballMatchesData {
    private final RestClient restClient;
    public FootballMatchesData(RestClient.Builder clientBuilder,@Value("${FOOTBALL_API_TOKEN:}") String apiToken) {
      SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
      requestFactory.setConnectTimeout(Duration.ofSeconds(4));
      requestFactory.setReadTimeout(Duration.ofSeconds(7));
        this.restClient = clientBuilder
                .baseUrl("https://api.football-data.org")
                .defaultHeader("X-Auth-Token", apiToken)
                .requestFactory(requestFactory).build();

    }

    public FootballMatchesResponse getFootballMatches(Integer season,String status){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("v4/competitions/PD/matches")
                .queryParam("season", season).queryParam("status", status).build())
                .retrieve().body(FootballMatchesResponse.class)
                ;    }

}

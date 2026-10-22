package com.kareem.laliga;

import com.kareem.laliga.Repository.MatchRepository;
import com.kareem.laliga.Repository.TeamRepository;
import com.kareem.laliga.dto.FootballMatchesResponse;
import com.kareem.laliga.dto.TeamStats;
import com.kareem.laliga.model.Match;
import com.kareem.laliga.model.Team;
import com.kareem.laliga.service.FootballMatchesData;
import com.kareem.laliga.service.MatchMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestContainersConfig.class)
class LaligaApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FootballMatchesData footballMatchesData;

    @Autowired
    private MatchMapper matchMapper;

    @Test
    void shouldReturnTeams() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/teams?page=0&size=5",
                String.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        assertEquals(5, jsonNode.get("content").size());
        assertEquals(23, jsonNode.get("totalElements").asInt());

    }


    @Test
    void shouldReturnTeamById() {
        Team expected = teamRepository.findTeamByProviderTeamId(81L)
                .orElseThrow();


        ResponseEntity<Team> response = restTemplate.getForEntity(
                "/teams/" + expected.getId(),
                Team.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Barcelona", response.getBody().getName());
        assertEquals(expected.getId(), response.getBody().getId());
    }

    @Test
    void shouldReturn404ForUnknownTeam() {

        ResponseEntity<Team> response =
                restTemplate.getForEntity(
                        "/teams/999999",
                        Team.class
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );
    }

    @Test
    void shouldReturnMatchById() {
        ResponseEntity<Match> response = restTemplate.getForEntity("/matches/2", Match.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Match body = response.getBody();
        assertEquals("Villarreal", body.getHomeTeam().getName());
        assertEquals("Oviedo", body.getAwayTeam().getName());
        assertEquals(2, body.getHomeGoals());
        assertEquals(0, body.getAwayGoals());
        assertEquals(2025, body.getSeason());

    }

    @Test
    void shouldReturn404ForUnknownMatch() {

        ResponseEntity<Match> response =
                restTemplate.getForEntity(
                        "/matches/999999",
                        Match.class
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );
    }

    @Test
    void shouldReturnMatches() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/matches?page=0&size=5",
                String.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        assertEquals(5, jsonNode.get("content").size());
        assertEquals(380, jsonNode.get("totalElements").asInt());

    }
    @Test
    void shouldReturnMatchesNewSeason() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "/matches?season=2026&page=0&size=5",
                String.class
        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        assertEquals(0, jsonNode.get("content").size());
        assertEquals(0, jsonNode.get("totalElements").asInt());

    }

    @Test
    void shouldReturnCorrectRealMadridStats() {
        Team realMadrid = teamRepository
                .findByName("Real Madrid")
                .orElseThrow();

        TeamStats stats = restTemplate.getForObject(
                "/teams/" + realMadrid.getId() + "/stats",
                TeamStats.class
        );

        assertNotNull(stats);
        assertEquals("Real Madrid", stats.teamName());
        assertEquals(38, stats.played());
        assertEquals(27, stats.wins());
        assertEquals(5, stats.draws());
        assertEquals(6, stats.losses());
        assertEquals(77, stats.goalsFor());
        assertEquals(35, stats.goalsAgainst());
    }

    @Test
    void shouldMapCorrectly() {

        var apiMatch = new FootballMatchesResponse.FootballMatch(
                12345L,
                Instant.parse("2026-10-17T22:30:00Z"),
                null,
                "FINISHED",
                9,
                new FootballMatchesResponse.FootballSeason(
                        2518L, "2026-08-16", "2027-05-30"
                ),
                new FootballMatchesResponse.FootballTeam(81L, "FC Barcelona"),
                new FootballMatchesResponse.FootballTeam(86L, "Real Madrid CF"),
                new FootballMatchesResponse.FootballScore(
                        "HOME_TEAM",
                        new FootballMatchesResponse.FootballScoreline(2, 1),
                        new FootballMatchesResponse.FootballScoreline(1, 0)
                )
        );

        when(footballMatchesData.getFootballMatches(2026,"FINISHED"))
                .thenReturn(new FootballMatchesResponse(List.of(apiMatch)));

        var response = footballMatchesData.getFootballMatches(2026,"FINISHED");

        Match mapped = matchMapper.toMatch(
                response.matches().getFirst(),
                new Team("Barcelona", 81L),
                new Team("Real Madrid", 86L)
        );

        assertEquals(12345L, mapped.getProviderMatchId());
        assertEquals(2026, mapped.getSeason());
        assertEquals(2, mapped.getHomeGoals());
        assertEquals(1, mapped.getAwayGoals());
        assertEquals("H", mapped.getResult());

        // 22:30 UTC becomes 00:30 the following day in Madrid
        assertEquals(LocalDate.of(2026, 10, 18), mapped.getMatchDate());
        assertEquals(LocalTime.of(0, 30), mapped.getMatchTime());
    }
}

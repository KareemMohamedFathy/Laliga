package com.kareem.laliga;

import com.kareem.laliga.Repository.TeamRepository;
import com.kareem.laliga.dto.TeamStats;
import com.kareem.laliga.model.Match;
import com.kareem.laliga.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

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
    private ObjectMapper objectMapper;
    @Test
	void shouldReturnTeams()  {
      ResponseEntity<String> responseEntity= restTemplate.getForEntity(
                "/teams?page=0&size=5",
                String.class
        );
      assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        assertEquals(5, jsonNode.get("content").size());
        assertEquals(20,jsonNode.get("totalElements").asInt());

	}
    @Test
    void shouldReturnTeamById() {
        Team expected = teamRepository.findByName("Barcelona")
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
        ResponseEntity<Match> response=restTemplate.getForEntity("/matches/2",Match.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        Match body=response.getBody();
        assertEquals("Villarreal",body.getHomeTeam().getName());
        assertEquals("Oviedo",body.getAwayTeam().getName());
        assertEquals(2,body.getHomeGoals());
        assertEquals(0,body.getAwayGoals());

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
    void shouldReturnMatches()  {
        ResponseEntity<String> responseEntity= restTemplate.getForEntity(
                "/matches?page=0&size=5",
                String.class
        );
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
        assertEquals(5, jsonNode.get("content").size());
        assertEquals(380,jsonNode.get("totalElements").asInt());

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


    }

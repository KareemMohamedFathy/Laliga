package com.kareem.laliga;

import com.kareem.laliga.dto.TeamStats;
import com.kareem.laliga.model.Match;
import com.kareem.laliga.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class LaligaApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

	@Test
	void shouldReturnTeams() {
      ResponseEntity<String> responseEntity= restTemplate.getForEntity(
                "/teams?page=0&size=5",
                String.class
        );
      assertEquals(HttpStatus.OK,responseEntity.getStatusCode());



	}
    @Test
    void shouldReturnTeamById() {

        ResponseEntity<Team> response =
                restTemplate.getForEntity(
                        "/teams/1",
                        Team.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
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
    void shouldReturnTeamStats() {

        ResponseEntity<TeamStats> response =
                restTemplate.getForEntity(
                        "/teams/1/stats",
                        TeamStats.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        TeamStats stats = response.getBody();

        assertNotNull(stats);
        assertEquals(1L, stats.teamId());
        assertTrue(stats.played() > 0);
    }
    @Test
    void shouldReturnMatchById() {
        ResponseEntity<Match> response=restTemplate.getForEntity("/matches/2",Match.class);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());


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
    }

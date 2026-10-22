
package com.kareem.laliga;

import com.kareem.laliga.Repository.MatchRepository;
import com.kareem.laliga.Repository.TeamRepository;
import com.kareem.laliga.dto.FootballMatchesResponse;
import com.kareem.laliga.model.Match;
import com.kareem.laliga.model.Team;
import com.kareem.laliga.service.FootballMatchesData;
import com.kareem.laliga.service.MatchSyncService;
import com.kareem.laliga.service.SyncSummary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestContainersConfig.class)
class MatchServiceTest {

    @MockitoBean
    private FootballMatchesData footballMatchesData;

    @Autowired
    private MatchSyncService matchSyncService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    void shouldInsertMatch() {

        FootballMatchesResponse.FootballMatch apiMatch =
                createApiMatch(2, "FINISHED");

        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenReturn(new FootballMatchesResponse(List.of(apiMatch)));

        SyncSummary syncSummary = matchSyncService.syncMatches(2026);

        assertEquals(1, syncSummary.inserted());
        assertEquals(0, syncSummary.rejected());
        assertEquals(0, syncSummary.skipped());
        assertEquals(0, syncSummary.updated());

        Optional<Match> match =
                matchRepository.findMatchByProviderMatchId(apiMatch.id());

        assertTrue(match.isPresent());

        Match stored = match.orElseThrow();

        assertEquals(2, stored.getHomeGoals());
        assertEquals(1, stored.getAwayGoals());
        assertEquals(2026, stored.getSeason());
        assertEquals(81L, stored.getHomeTeam().getProviderTeamId());
        assertEquals(86L, stored.getAwayTeam().getProviderTeamId());
    }

    @Test
    void shouldSkip() {

        FootballMatchesResponse.FootballMatch apiMatch =
                createApiMatch(2, "FINISHED");

        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenReturn(new FootballMatchesResponse(List.of(apiMatch)));

        SyncSummary syncSummary = matchSyncService.syncMatches(2026);

        assertEquals(1, syncSummary.inserted());
        assertEquals(0, syncSummary.rejected());
        assertEquals(0, syncSummary.skipped());
        assertEquals(0, syncSummary.updated());

        SyncSummary syncSummary1 = matchSyncService.syncMatches(2026);

        assertEquals(0, syncSummary1.inserted());
        assertEquals(0, syncSummary1.rejected());
        assertEquals(1, syncSummary1.skipped());
        assertEquals(0, syncSummary1.updated());

        Optional<Match> match =
                matchRepository.findMatchByProviderMatchId(apiMatch.id());

        assertTrue(match.isPresent());

        Match stored = match.orElseThrow();

        assertEquals(2, stored.getHomeGoals());
        assertEquals(1, stored.getAwayGoals());
        assertEquals(2026, stored.getSeason());
        assertEquals(81L, stored.getHomeTeam().getProviderTeamId());
        assertEquals(86L, stored.getAwayTeam().getProviderTeamId());
    }

    @Test
    void shouldUpdateCorrectedMatch() {

        FootballMatchesResponse.FootballMatch apiMatch =
                createApiMatch(2, "FINISHED");

        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenReturn(new FootballMatchesResponse(List.of(apiMatch)));

        SyncSummary syncSummary = matchSyncService.syncMatches(2026);

        assertEquals(1, syncSummary.inserted());
        assertEquals(0, syncSummary.rejected());
        assertEquals(0, syncSummary.skipped());
        assertEquals(0, syncSummary.updated());

        // Capture the original database ID
        Long originalId = matchRepository
                .findMatchByProviderMatchId(900001L)
                .orElseThrow()
                .getId();

        FootballMatchesResponse.FootballMatch correctedMatch =
                createApiMatch(3, "FINISHED");

        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenReturn(new FootballMatchesResponse(List.of(correctedMatch)));

        SyncSummary syncSummary2 = matchSyncService.syncMatches(2026);

        assertEquals(0, syncSummary2.inserted());
        assertEquals(0, syncSummary2.rejected());
        assertEquals(0, syncSummary2.skipped());
        assertEquals(1, syncSummary2.updated());

        Match stored = matchRepository
                .findMatchByProviderMatchId(900001L)
                .orElseThrow();

        // Verify same row was updated, not replaced
        assertEquals(originalId, stored.getId());

        assertEquals(3, stored.getHomeGoals());
        assertEquals(1, stored.getAwayGoals());
        assertEquals(2026, stored.getSeason());
        assertEquals(81L, stored.getHomeTeam().getProviderTeamId());
        assertEquals(86L, stored.getAwayTeam().getProviderTeamId());
    }

    @Test
    void shouldRejectInvalidMatch() {

        FootballMatchesResponse.FootballMatch invalidMatch =
                createApiMatch(2, "POSTPONED");

        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenReturn(new FootballMatchesResponse(List.of(invalidMatch)));

        SyncSummary summary = matchSyncService.syncMatches(2026);

        assertEquals(0, summary.inserted());
        assertEquals(0, summary.updated());
        assertEquals(0, summary.skipped());
        assertEquals(1, summary.rejected());

        // Invalid match must not be written to PostgreSQL
        assertTrue(
                matchRepository
                        .findMatchByProviderMatchId(900001L)
                        .isEmpty()
        );
    }

    @Test
    void shouldCreateNewTeams() {

        FootballMatchesResponse.FootballMatch original =
                createApiMatch(2, "FINISHED");

        // Replace the existing teams with two fictional new teams.
        FootballMatchesResponse.FootballMatch apiMatch =
                new FootballMatchesResponse.FootballMatch(
                        original.id(),
                        original.utcDate(),
                        original.lastUpdated(),
                        original.status(),
                        original.matchday(),
                        original.season(),
                        new FootballMatchesResponse.FootballTeam(
                                9998L, "New LaLiga Home Team"
                        ),
                        new FootballMatchesResponse.FootballTeam(
                                9999L, "New LaLiga Away Team"
                        ),
                        original.score()
                );

        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenReturn(new FootballMatchesResponse(List.of(apiMatch)));

        SyncSummary summary = matchSyncService.syncMatches(2026);

        assertEquals(1, summary.inserted());
        assertEquals(0, summary.updated());
        assertEquals(0, summary.skipped());
        assertEquals(0, summary.rejected());

        Team homeTeam = teamRepository
                .findTeamByProviderTeamId(9998L)
                .orElseThrow();

        Team awayTeam = teamRepository
                .findTeamByProviderTeamId(9999L)
                .orElseThrow();

        assertEquals("New LaLiga Home Team", homeTeam.getName());
        assertEquals("New LaLiga Away Team", awayTeam.getName());

        // Verify that the saved match references the newly created teams.
        Match stored = matchRepository
                .findMatchByProviderMatchId(900001L)
                .orElseThrow();

        assertEquals(homeTeam.getId(), stored.getHomeTeam().getId());
        assertEquals(awayTeam.getId(), stored.getAwayTeam().getId());
    }

    @Test
    void shouldRollBackTeams() {

        // First match: valid, should be inserted before the failure
        FootballMatchesResponse.FootballMatch validMatch =
                createApiMatch(2, "FINISHED");

        // Second match: two different team IDs with the same name
        FootballMatchesResponse.FootballMatch failingMatch =
                new FootballMatchesResponse.FootballMatch(
                        900002L,
                        validMatch.utcDate(),
                        validMatch.lastUpdated(),
                        validMatch.status(),
                        validMatch.matchday(),
                        validMatch.season(),
                        new FootballMatchesResponse.FootballTeam(
                                9998L, "New LaLiga Home Team"
                        ),
                        new FootballMatchesResponse.FootballTeam(
                                9999L, "New LaLiga Home Team"
                        ),
                        validMatch.score()
                );

        // Valid match FIRST, failing match SECOND
        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenReturn(new FootballMatchesResponse(
                        List.of(validMatch, failingMatch)
                ));

        assertThrows(
                RuntimeException.class,
                () -> matchSyncService.syncMatches(2026)
        );

        // The first match must also be rolled back
        assertTrue(
                matchRepository.findMatchByProviderMatchId(900001L).isEmpty()
        );

        // The failing match must not exist
        assertTrue(
                matchRepository.findMatchByProviderMatchId(900002L).isEmpty()
        );

        // No test-created teams should remain
        assertTrue(
                teamRepository.findTeamByProviderTeamId(9998L).isEmpty()
        );

        assertTrue(
                teamRepository.findTeamByProviderTeamId(9999L).isEmpty()
        );
    }

    // Reusable fictional API match with configurable goals and status
    private FootballMatchesResponse.FootballMatch createApiMatch(
            int homeGoals, String status) {

        return new FootballMatchesResponse.FootballMatch(
                900001L,
                Instant.parse("2026-10-17T20:00:00Z"),
                null,
                status,
                9,
                new FootballMatchesResponse.FootballSeason(
                        2518L, "2026-08-16", "2027-05-30"
                ),
                new FootballMatchesResponse.FootballTeam(
                        81L, "FC Barcelona"
                ),
                new FootballMatchesResponse.FootballTeam(
                        86L, "Real Madrid CF"
                ),
                new FootballMatchesResponse.FootballScore(
                        "HOME_TEAM",
                        new FootballMatchesResponse.FootballScoreline(homeGoals, 1),
                        new FootballMatchesResponse.FootballScoreline(1, 0)
                )
        );
    }

    @AfterEach
    @BeforeEach
    void deleteTestData() {

        // Delete matches first because they reference teams
        matchRepository.findMatchByProviderMatchId(900001L)
                .ifPresent(matchRepository::delete);

        matchRepository.findMatchByProviderMatchId(900002L)
                .ifPresent(matchRepository::delete);

        // Then delete any teams created by the tests
        teamRepository.findTeamByProviderTeamId(9998L)
                .ifPresent(teamRepository::delete);

        teamRepository.findTeamByProviderTeamId(9999L)
                .ifPresent(teamRepository::delete);
    }
}

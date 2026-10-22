
package com.kareem.laliga;

import com.kareem.laliga.dto.FootballMatchesResponse;
import com.kareem.laliga.dto.FootballMatchesResponse.FootballMatch;
import com.kareem.laliga.dto.FootballMatchesResponse.FootballScore;
import com.kareem.laliga.dto.FootballMatchesResponse.FootballScoreline;
import com.kareem.laliga.dto.FootballMatchesResponse.FootballSeason;
import com.kareem.laliga.dto.FootballMatchesResponse.FootballTeam;
import com.kareem.laliga.service.FootballMatchesData;
import com.kareem.laliga.service.MatchSyncService;
import com.kareem.laliga.service.MatchValidator;
import com.kareem.laliga.service.MatchWriter;
import com.kareem.laliga.service.SyncSummary;
import com.kareem.laliga.service.ValidatedMatches;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MatchSyncServiceTest {

    private final FootballMatchesData footballMatchesData =
            mock(FootballMatchesData.class);

    private final MatchWriter matchWriter =
            mock(MatchWriter.class);

    private final MatchSyncService matchSyncService =
            new MatchSyncService(
                    footballMatchesData,
                    new MatchValidator(),
                    matchWriter
            );

    @Test
    void shouldAcceptValidMatchesAndRejectInvalidOnes() {

        FootballMatch valid = createMatch(100L, 81L, "HOME_TEAM");

        // Missing home team provider ID
        FootballMatch invalidTeam = createMatch(101L, null, "HOME_TEAM");

        // Unsupported winner
        FootballMatch invalidWinner = createMatch(102L, 81L, "UNKNOWN");

        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenReturn(new FootballMatchesResponse(
                        List.of(valid, invalidTeam, invalidWinner)
                ));

        when(matchWriter.writeMatch(any(ValidatedMatches.class)))
                .thenReturn(new SyncSummary(1, 0, 0, 2));

        SyncSummary result = matchSyncService.syncMatches(2026);

        assertEquals(1, result.inserted());
        assertEquals(0, result.updated());
        assertEquals(0, result.skipped());
        assertEquals(2, result.rejected());

        ArgumentCaptor<ValidatedMatches> captor =
                ArgumentCaptor.forClass(ValidatedMatches.class);

        verify(matchWriter).writeMatch(captor.capture());

        ValidatedMatches validatedMatches = captor.getValue();

        assertEquals(1, validatedMatches.validMatches().size());
        assertEquals(2, validatedMatches.rejectedCount());
        assertSame(valid, validatedMatches.validMatches().getFirst());

        verify(footballMatchesData, times(1))
                .getFootballMatches(2026, "FINISHED");
    }

    @Test
    void shouldHandleEmptyMatchList() {

        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenReturn(new FootballMatchesResponse(List.of()));

        when(matchWriter.writeMatch(any(ValidatedMatches.class)))
                .thenReturn(new SyncSummary(0, 0, 0, 0));

        SyncSummary result = matchSyncService.syncMatches(2026);

        assertEquals(0, result.inserted());
        assertEquals(0, result.updated());
        assertEquals(0, result.skipped());
        assertEquals(0, result.rejected());

        // No valid matches should be passed to the writer.
        ArgumentCaptor<ValidatedMatches> captor =
                ArgumentCaptor.forClass(ValidatedMatches.class);

        verify(matchWriter).writeMatch(captor.capture());

        assertTrue(captor.getValue().validMatches().isEmpty());
        assertEquals(0, captor.getValue().rejectedCount());
    }

    @Test
    void shouldPropagateApiFailure() {

        when(footballMatchesData.getFootballMatches(2026, "FINISHED"))
                .thenThrow(new IllegalStateException("API unavailable"));

        assertThrows(
                IllegalStateException.class,
                () -> matchSyncService.syncMatches(2026)
        );

        verifyNoInteractions(matchWriter);
    }

    // Helper for constructing test matches
    private FootballMatch createMatch(
            Long id,
            Long homeTeamId,
            String winner) {

        return new FootballMatch(
                id,
                Instant.parse("2026-10-17T20:30:00Z"),
                null,
                "FINISHED",
                9,
                new FootballSeason(
                        2518L, "2026-08-16", "2027-05-30"
                ),
                new FootballTeam(homeTeamId, "FC Barcelona"),
                new FootballTeam(86L, "Real Madrid CF"),
                new FootballScore(
                        winner,
                        new FootballScoreline(2, 1),
                        new FootballScoreline(1, 0)
                )
        );
    }
}

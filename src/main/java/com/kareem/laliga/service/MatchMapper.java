package com.kareem.laliga.service;

import com.kareem.laliga.dto.FootballMatchesResponse;
import com.kareem.laliga.dto.FootballMatchesResponse.FootballMatch;
import com.kareem.laliga.model.Match;
import com.kareem.laliga.model.Team;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class MatchMapper {

    public Match toMatch(FootballMatch match, Team homeTeam, Team awayTeam) {

        Match footballMatch = new Match(homeTeam, awayTeam);

        // Season
        if (match.season() != null && match.season().startDate() != null) {
            footballMatch.setSeason(
                    LocalDate.parse(match.season().startDate()).getYear()
            );
        }

        footballMatch.setProviderMatchId(match.id());
        footballMatch.setKickoffUtc(match.utcDate());

        // Scores
        FootballMatchesResponse.FootballScore score = match.score();

        FootballMatchesResponse.FootballScoreline fullTime =
                score == null ? null : score.fullTime();

        FootballMatchesResponse.FootballScoreline halfTime =
                score == null ? null : score.halfTime();

        footballMatch.setHomeGoals(
                fullTime == null ? null : fullTime.home()
        );

        footballMatch.setAwayGoals(
                fullTime == null ? null : fullTime.away()
        );

        footballMatch.setHalfTimeHomeGoals(
                halfTime == null ? null : halfTime.home()
        );

        footballMatch.setHalfTimeAwayGoals(
                halfTime == null ? null : halfTime.away()
        );

        // Result
        String winner = score == null ? null : score.winner();

        String mapper = winner == null ? null : switch (winner) {
            case "HOME_TEAM" -> "H";
            case "AWAY_TEAM" -> "A";
            case "DRAW" -> "D";
            default -> throw new IllegalArgumentException(
                    "Unknown winner: " + winner
            );
        };

        footballMatch.setResult(mapper);

        // Date and time
        Instant instant = match.utcDate();

        if (instant != null) {
            ZonedDateTime local =
                    instant.atZone(ZoneId.of("Europe/Madrid"));

            LocalDate date = local.toLocalDate();
            LocalTime time = local.toLocalTime();

            footballMatch.setMatchDate(date);
            footballMatch.setMatchTime(time);
        }

        return footballMatch;
    }
}
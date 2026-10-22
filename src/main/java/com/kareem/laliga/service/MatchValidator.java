
package com.kareem.laliga.service;

import com.kareem.laliga.dto.FootballMatchesResponse.FootballMatch;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@Service
public class MatchValidator {

    public boolean isValid(FootballMatch match, int expectedSeason) {

        if (match == null || match.id() == null ||
                !"FINISHED".equals(match.status()) ||
                match.utcDate() == null) {
            return false;
        }

        // Season
        if (match.season() == null ||
                match.season().startDate() == null) {
            return false;
        }

        try {
            int season = LocalDate.parse(
                    match.season().startDate()
            ).getYear();

            if (season != expectedSeason) {
                return false;
            }
        } catch (DateTimeParseException e) {
            return false;
        }

        // Teams
        var home = match.homeTeam();
        var away = match.awayTeam();

        if (home == null || away == null ||
                home.id() == null || away.id() == null ||
                Objects.equals(home.id(), away.id())) {
            return false;
        }

        if (home.name() == null || home.name().isBlank() ||
                away.name() == null || away.name().isBlank()) {
            return false;
        }

        // Final score
        var score = match.score();

        if (score == null || score.fullTime() == null) {
            return false;
        }

        Integer homeGoals = score.fullTime().home();
        Integer awayGoals = score.fullTime().away();

        if (homeGoals == null || awayGoals == null ||
                homeGoals < 0 || awayGoals < 0) {
            return false;
        }

        // Winner must agree with final score
        if (score.winner() == null) {
            return false;
        }

        return switch (score.winner()) {
            case "HOME_TEAM" -> homeGoals > awayGoals;
            case "AWAY_TEAM" -> awayGoals > homeGoals;
            case "DRAW" -> homeGoals.equals(awayGoals);
            default -> false;
        };
    }
}

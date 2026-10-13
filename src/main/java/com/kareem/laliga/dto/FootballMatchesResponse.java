
package com.kareem.laliga.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FootballMatchesResponse(
        List<FootballMatch> matches
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FootballMatch(
            Long id,
            Instant utcDate,
            Instant lastUpdated,
            String status,
            Integer matchday,
            FootballSeason season,
            FootballTeam homeTeam,
            FootballTeam awayTeam,
            FootballScore score
    ) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FootballSeason(
            Long id,
            String startDate,
            String endDate
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FootballTeam(
            Long id,
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FootballScore(
            String winner,
            FootballScoreline fullTime,
            FootballScoreline halfTime
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FootballScoreline(
            Integer home,
            Integer away
    ) {}
}

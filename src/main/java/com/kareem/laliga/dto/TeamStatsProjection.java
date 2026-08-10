package com.kareem.laliga.dto;


public interface TeamStatsProjection {
    Long getPlayed();
    Long getWins();
    Long getDraws();
    Long getLosses();
    Long getGoalsFor();
    Long getGoalsAgainst();
}


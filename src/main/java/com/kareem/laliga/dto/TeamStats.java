package com.kareem.laliga.dto;

public record TeamStats(
        Long teamId,
        String teamName,
        int played,
        int wins,
        int draws,
        int losses,
        int goalsFor,
        int goalsAgainst
) {
}
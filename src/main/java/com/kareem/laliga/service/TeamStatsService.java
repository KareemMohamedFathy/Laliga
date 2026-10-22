package com.kareem.laliga.service;

import com.kareem.laliga.Repository.MatchRepository;
import com.kareem.laliga.Repository.TeamRepository;
import com.kareem.laliga.dto.TeamStats;
import com.kareem.laliga.dto.TeamStatsProjection;
import com.kareem.laliga.model.Team;
import org.springframework.stereotype.Service;

@Service
public class TeamStatsService {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public TeamStatsService(
            TeamRepository teamRepository,
            MatchRepository matchRepository) {

        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    public TeamStats getStats(long teamId,Integer season) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow();

        TeamStatsProjection stats =
                matchRepository.getTeamStatsBySeason(teamId,season);

        return new TeamStats(
                team.getId(),
                team.getName(),
                stats.getPlayed().intValue(),
                stats.getWins().intValue(),
                stats.getDraws().intValue(),
                stats.getLosses().intValue(),
                stats.getGoalsFor().intValue(),
                stats.getGoalsAgainst().intValue()
        );
    }
}
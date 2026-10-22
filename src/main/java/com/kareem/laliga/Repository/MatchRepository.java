package com.kareem.laliga.Repository;

import com.kareem.laliga.dto.TeamStatsProjection;
import com.kareem.laliga.model.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match,Long>, PagingAndSortingRepository<Match,Long> {
    Page<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId, Pageable pageable);

    @Query(value = """
            SELECT
                COUNT(*) AS played,
            
                SUM(
                    CASE
                        WHEN home_team_id = :teamId
                             AND home_goals > away_goals THEN 1
                        WHEN away_team_id = :teamId
                             AND away_goals > home_goals THEN 1
                        ELSE 0
                    END
                ) AS wins,
            
                SUM(
                    CASE
                        WHEN home_goals = away_goals THEN 1
                        ELSE 0
                    END
                ) AS draws,
            
                SUM(
                    CASE
                        WHEN home_team_id = :teamId
                             AND home_goals < away_goals THEN 1
                        WHEN away_team_id = :teamId
                             AND away_goals < home_goals THEN 1
                        ELSE 0
                    END
                ) AS losses,
            
                SUM(
                    CASE
                        WHEN home_team_id = :teamId
                        THEN home_goals
                        ELSE away_goals
                    END
                ) AS goalsFor,
            
                SUM(
                    CASE
                        WHEN home_team_id = :teamId
                        THEN away_goals
                        ELSE home_goals
                    END
                ) AS goalsAgainst
            
            FROM matches
            WHERE home_team_id = :teamId
               OR away_team_id = :teamId
            """,
            nativeQuery = true)
    TeamStatsProjection getTeamStats(Long teamId);

    public Optional<Match> findMatchByProviderMatchId(Long providerMatchId);
    public Page<Match> findMatchesBySeason(Integer season, Pageable pageable);
    public boolean existsBySeason(Integer season);
}

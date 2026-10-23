package com.kareem.laliga.Repository;

import com.kareem.laliga.dto.TeamStatsProjection;
import com.kareem.laliga.model.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match,Long>, PagingAndSortingRepository<Match,Long> {
    @Query("""
    SELECT m FROM Match m
      JOIN  FETCH m.homeTeam
      JOIN  FETCH m.awayTeam
    WHERE (m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId)
      AND (:season IS NULL OR m.season = :season)
    """)
    Page<Match> findTeamMatches(
            Long teamId,
            Integer season,
            Pageable pageable
    );
    @Query(value = """
            SELECT
                COUNT(*) AS played,
            
                COALESCE(SUM(
                    CASE
                        WHEN home_team_id = :teamId
                             AND home_goals > away_goals THEN 1
                        WHEN away_team_id = :teamId
                             AND away_goals > home_goals THEN 1
                        ELSE 0
                    END
                ),0)AS wins,
            
               COALESCE( SUM(
                    CASE
                        WHEN home_goals = away_goals THEN 1
                        ELSE 0
                    END
                ) ,0)AS draws,
            
                COALESCE(SUM(
                    CASE
                        WHEN home_team_id = :teamId
                             AND home_goals < away_goals THEN 1
                        WHEN away_team_id = :teamId
                             AND away_goals < home_goals THEN 1
                        ELSE 0
                    END
                ),0)AS losses,
            
                COALESCE(SUM(
                    CASE
                        WHEN home_team_id = :teamId
                        THEN home_goals
                        ELSE away_goals
                    END
                ),0) AS goalsFor,
            
               COALESCE( SUM(
                    CASE
                        WHEN home_team_id = :teamId
                        THEN away_goals
                        ELSE home_goals
                    END
                ),0) AS goalsAgainst
            
            FROM matches
            WHERE (home_team_id = :teamId
               OR away_team_id = :teamId)
             AND (:season IS NULL OR season = :season)
            """,
            nativeQuery = true)
    TeamStatsProjection getTeamStatsBySeason(Long teamId,Integer season);

    public Optional<Match> findMatchByProviderMatchId(Long providerMatchId);
    @EntityGraph(attributePaths = {"homeTeam", "awayTeam"})
    public Page<Match> findMatchesBySeason(Integer season, Pageable pageable);
    public boolean existsBySeason(Integer season);
    public Long countBySeason(Integer season);
    @Override
    @EntityGraph(attributePaths = {"homeTeam", "awayTeam"})
    Page<Match> findAll(Pageable pageable);
}

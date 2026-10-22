package com.kareem.laliga.service;

import com.kareem.laliga.Repository.MatchRepository;
import com.kareem.laliga.Repository.TeamRepository;
import com.kareem.laliga.dto.FootballMatchesResponse;
import com.kareem.laliga.model.Match;
import com.kareem.laliga.model.Team;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class MatchWriter {
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    public MatchWriter(TeamRepository teamRepository, MatchRepository matchRepository, MatchMapper matchMapper) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.matchMapper = matchMapper;
    }

    @Transactional
    public SyncSummary writeMatch(ValidatedMatches validatedMatches){
        int inserted = 0;
        int updated = 0;
        int skipped = 0;
        for(FootballMatchesResponse.FootballMatch footballMatch:validatedMatches.validMatches()) {
            FootballMatchesResponse.FootballTeam homeTeam= footballMatch.homeTeam();
            Long homeId=homeTeam.id();
            FootballMatchesResponse.FootballTeam awayTeam= footballMatch.awayTeam();
            Long awayId=awayTeam.id();

            Team hTeam = teamRepository.findTeamByProviderTeamId(homeId).orElseGet(()->teamRepository.save(new Team(homeTeam.name(),homeId)));
            Team aTeam = teamRepository.findTeamByProviderTeamId(awayId).orElseGet(()->teamRepository.save(new Team(awayTeam.name(),awayId)));

            Optional<Match> matchFind=matchRepository.findMatchByProviderMatchId(footballMatch.id());
            Match incomingMatch=matchMapper.toMatch(footballMatch,hTeam,aTeam);

            if(matchFind.isEmpty()){
                matchRepository.save(incomingMatch);
                inserted++;

            }else{
                Match exisistingMatch=matchFind.get();
                if(hasChanges(exisistingMatch,incomingMatch)){
                    updateMatch(exisistingMatch,incomingMatch);
                    updated++;
                    continue;
                }

                skipped++;
            }

        }
        return new SyncSummary(inserted,updated,skipped, validatedMatches.rejectedCount());

    }

    private void updateMatch(Match existing, Match incoming) {
        // Teams
        existing.setHomeTeam(incoming.getHomeTeam());
        existing.setAwayTeam(incoming.getAwayTeam());

        // Season and kickoff
        existing.setSeason(incoming.getSeason());
        existing.setKickoffUtc(incoming.getKickoffUtc());
        existing.setMatchDate(incoming.getMatchDate());
        existing.setMatchTime(incoming.getMatchTime());

        // Full-time scores
        existing.setHomeGoals(incoming.getHomeGoals());
        existing.setAwayGoals(incoming.getAwayGoals());
        existing.setResult(incoming.getResult());

        // Half-time scores are optional
        if (incoming.getHalfTimeHomeGoals() != null) {
            existing.setHalfTimeHomeGoals(incoming.getHalfTimeHomeGoals());
        }

        if (incoming.getHalfTimeAwayGoals() != null) {
            existing.setHalfTimeAwayGoals(incoming.getHalfTimeAwayGoals());
        }
    }

    private boolean hasChanges(Match existing, Match incoming) {

        return
                // Teams
                !Objects.equals(existing.getHomeTeam().getId(),
                        incoming.getHomeTeam().getId()) ||
                        !Objects.equals(existing.getAwayTeam().getId(),
                                incoming.getAwayTeam().getId()) ||

                        // Season and kickoff
                        !Objects.equals(existing.getSeason(), incoming.getSeason()) ||
                        !Objects.equals(existing.getKickoffUtc(), incoming.getKickoffUtc()) ||
                        !Objects.equals(existing.getMatchDate(), incoming.getMatchDate()) ||
                        !Objects.equals(existing.getMatchTime(), incoming.getMatchTime()) ||

                        // Full-time scores
                        !Objects.equals(existing.getHomeGoals(), incoming.getHomeGoals()) ||
                        !Objects.equals(existing.getAwayGoals(), incoming.getAwayGoals()) ||
                        !Objects.equals(existing.getResult(), incoming.getResult()) ||

                        // Half-time scores (optional)
                        (incoming.getHalfTimeHomeGoals() != null &&
                                !Objects.equals(existing.getHalfTimeHomeGoals(),
                                        incoming.getHalfTimeHomeGoals())) ||

                        (incoming.getHalfTimeAwayGoals() != null &&
                                !Objects.equals(existing.getHalfTimeAwayGoals(),
                                        incoming.getHalfTimeAwayGoals()));
    }
}

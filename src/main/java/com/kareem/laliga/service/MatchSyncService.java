package com.kareem.laliga.service;

import com.kareem.laliga.dto.FootballMatchesResponse;
import com.kareem.laliga.model.MatchStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchSyncService {
    private final FootballMatchesData footballMatchesData;
    private final MatchValidator matchValidator;
    private final MatchWriter matchWriter;

    public MatchSyncService(FootballMatchesData footballMatchesData, MatchValidator matchValidator, MatchWriter matchWriter) {
        this.footballMatchesData = footballMatchesData;
        this.matchValidator = matchValidator;
        this.matchWriter = matchWriter;
    }

    public SyncSummary syncMatches(Integer season){
       FootballMatchesResponse response = footballMatchesData.getFootballMatches(season, MatchStatus.FINISHED.name());
       int countInvalid=0;
       List<FootballMatchesResponse.FootballMatch> matchList=new ArrayList<>();
        if (response == null || response.matches() == null) {
            throw new IllegalStateException("Invalid API response");
        }
       for(FootballMatchesResponse.FootballMatch match:response.matches()){
         boolean valid=  matchValidator.isValid(match,season);
         if(valid){
             matchList.add(match);
         }
         else{
             countInvalid++;
         }
       }
     return matchWriter.writeMatch( new ValidatedMatches(matchList, countInvalid));
    }
}

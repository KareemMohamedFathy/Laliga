package com.kareem.laliga.service;

import com.kareem.laliga.Repository.MatchRepository;
import com.kareem.laliga.Repository.TeamRepository;
import com.kareem.laliga.model.Match;
import com.kareem.laliga.model.Team;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CsvImportService {
   private final TeamRepository teamRepository;
   private final MatchRepository matchRepository;
   public CsvImportService(TeamRepository teamRepository, MatchRepository matchRepository){
       this.teamRepository=teamRepository;
       this.matchRepository=matchRepository;
   }
   public void importMatches() throws IOException {
       if (matchRepository.existsBySeason(2025)) {
           return;
       }
       ClassPathResource resource =
               new ClassPathResource(
                       "data/laliga_2025_2026.csv"
               );

       DateTimeFormatter dateFormatter =
               DateTimeFormatter.ofPattern(
                       "dd/MM/yyyy"
               );

       try (Reader reader =
                    new InputStreamReader(
                            resource.getInputStream())) {




           CSVFormat format =
                   CSVFormat.DEFAULT.builder()
                           .setHeader()
                           .setSkipHeaderRecord(true)
                           .get();
           try (CSVParser parser = new CSVParser(reader, format)) {
               for (CSVRecord record : parser) {

                   Team homeTeam =
                           findOrCreateTeam(
                                   record.get("HomeTeam")
                           );

                   Team awayTeam =
                           findOrCreateTeam(
                                   record.get("AwayTeam")
                           );

                   Match match =
                           new Match(
                                   homeTeam,
                                   awayTeam
                           );

                   match.setMatchDate(
                           LocalDate.parse(
                                   record.get("Date"),
                                   dateFormatter
                           )
                   );

                   match.setMatchTime(
                           LocalTime.parse(
                                   record.get("Time")
                           )
                   );

                   match.setHomeGoals(
                           Integer.parseInt(
                                   record.get("FTHG")
                           )
                   );

                   match.setAwayGoals(
                           Integer.parseInt(
                                   record.get("FTAG")
                           )
                   );

                   match.setResult(
                           record.get("FTR")
                   );
                   match.setHalfTimeHomeGoals(
                           Integer.parseInt(record.get("HTHG"))
                   );

                   match.setHalfTimeAwayGoals(
                           Integer.parseInt(record.get("HTAG"))
                   );

                   match.setHalfTimeResult(
                           record.get("HTR")
                   );

                   match.setHomeShots(
                           Integer.parseInt(record.get("HS"))
                   );

                   match.setAwayShots(
                           Integer.parseInt(record.get("AS"))
                   );

                   match.setHomeShotsOnTarget(
                           Integer.parseInt(record.get("HST"))
                   );

                   match.setAwayShotsOnTarget(
                           Integer.parseInt(record.get("AST"))
                   );

                   match.setHomeFouls(
                           Integer.parseInt(record.get("HF"))
                   );

                   match.setAwayFouls(
                           Integer.parseInt(record.get("AF"))
                   );

                   match.setHomeCorners(
                           Integer.parseInt(record.get("HC"))
                   );

                   match.setAwayCorners(
                           Integer.parseInt(record.get("AC"))
                   );

                   match.setHomeYellowCards(
                           Integer.parseInt(record.get("HY"))
                   );

                   match.setAwayYellowCards(
                           Integer.parseInt(record.get("AY"))
                   );

                   match.setHomeRedCards(
                           Integer.parseInt(record.get("HR"))
                   );

                   match.setAwayRedCards(
                           Integer.parseInt(record.get("AR"))
                   );
                   match.setSeason(2025);

                   matchRepository.save(match);
               }
           }
       }

   }

    private Team findOrCreateTeam(
            String name) {
      Optional<Team> team= teamRepository.findByName(name);
      if(team.isPresent()){
          return team.get();
      }
      Team newTeam= new Team(name);
        return teamRepository.save(newTeam);
    }


}

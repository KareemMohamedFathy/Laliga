package com.kareem.laliga.Controller;

import com.kareem.laliga.Repository.MatchRepository;
import com.kareem.laliga.Repository.TeamRepository;
import com.kareem.laliga.dto.TeamStats;
import com.kareem.laliga.model.Match;
import com.kareem.laliga.model.Team;
import com.kareem.laliga.service.TeamStatsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RequestMapping("/teams")
@RestController
public class TeamController {
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final TeamStatsService teamStatsService;

    public TeamController(TeamRepository teamRepository, MatchRepository matchRepository,TeamStatsService teamStatsService) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.teamStatsService=teamStatsService;
    }


    @GetMapping
    public Page<Team> findAll(Pageable pageable) {
        return teamRepository.findAll(pageable);
    }
    @GetMapping("/{requestedId}")
    public ResponseEntity<Team> findTeam(@PathVariable long requestedId) {
        Optional<Team> team= teamRepository.findById(requestedId);
        if(team.isPresent()){
            return ResponseEntity.ok(team.get());
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{requestedId}/matches")
    public ResponseEntity<Page<Match>> getTeamMatches(@PathVariable long requestedId,@PageableDefault(
            size = 5,    sort = {"matchDate", "id"}
            ,
            direction = Sort.Direction.DESC
    )Pageable pageable,@RequestParam(required = false) Integer season){
        if(teamRepository.existsById(requestedId)){
            Page<Match>  matches=  matchRepository.findTeamMatches(requestedId,season,pageable);
            return ResponseEntity.ok(matches);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{teamId}/stats")
    public ResponseEntity<TeamStats> getTeamStats(@PathVariable long teamId,@RequestParam(required = false) Integer season) {
        if(teamRepository.existsById(teamId)){
            TeamStats stats=  teamStatsService.getStats(teamId,season);
            return ResponseEntity.ok(stats);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }






    }

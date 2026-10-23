package com.kareem.laliga.Controller;

import com.kareem.laliga.Repository.MatchRepository;
import com.kareem.laliga.model.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/matches")
@RestController
public class MatchController {
    private final MatchRepository matchRepository;

    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }
    @EntityGraph(attributePaths = {"homeTeam", "awayTeam"})
    @GetMapping
    public Page<Match> findAllBySeason( @RequestParam(required = false) Integer season,@PageableDefault(
    size = 5,sort = {"matchDate", "id"},
            direction = Sort.Direction.DESC
    ) Pageable pageable){
        if (season == null) {
            return matchRepository.findAll(pageable);
        }
        return matchRepository.findMatchesBySeason(season,pageable);
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<Match> findMatch(@PathVariable long matchId){
        Optional<Match> match=matchRepository.findById(matchId);
        if(match.isPresent()){
            return ResponseEntity.ok(match.get());
        }
        return  ResponseEntity.notFound().build();

    }



}

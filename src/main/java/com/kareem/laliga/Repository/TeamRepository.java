package com.kareem.laliga.Repository;

import com.kareem.laliga.model.Match;
import com.kareem.laliga.model.Team;
import org.apache.tomcat.util.http.parser.TE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team,Long>, PagingAndSortingRepository<Team,Long> {
    Optional<Team> findByName(String name);
    public Optional<Team> findTeamByProviderTeamId(Long providerTeamId);

}

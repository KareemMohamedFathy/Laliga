package com.kareem.laliga.Repository;

import com.kareem.laliga.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team,Long>, PagingAndSortingRepository<Team,Long> {
    Optional<Team> findByName(String name);

}

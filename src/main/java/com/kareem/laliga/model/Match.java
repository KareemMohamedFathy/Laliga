package com.kareem.laliga.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    private LocalDate matchDate;

    private LocalTime matchTime;

    private Integer homeGoals;
    private Integer awayGoals;

    private String result;

    private Integer halfTimeHomeGoals;
    private Integer halfTimeAwayGoals;

    private String halfTimeResult;

    private Integer homeShots;
    private Integer awayShots;

    private Integer homeShotsOnTarget;
    private Integer awayShotsOnTarget;

    private Integer homeFouls;
    private Integer awayFouls;

    private Integer homeCorners;
    private Integer awayCorners;

    private Integer homeYellowCards;
    private Integer awayYellowCards;

    private Integer homeRedCards;
    private Integer awayRedCards;
    public Match() {
    }

    public Match(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }
    public Long getId() {
        return id;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    public LocalTime getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(LocalTime matchTime) {
        this.matchTime = matchTime;
    }

    public Integer getHomeGoals() {
        return homeGoals;
    }

    public void setHomeGoals(Integer homeGoals) {
        this.homeGoals = homeGoals;
    }

    public Integer getAwayGoals() {
        return awayGoals;
    }

    public void setAwayGoals(Integer awayGoals) {
        this.awayGoals = awayGoals;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getHalfTimeHomeGoals() {
        return halfTimeHomeGoals;
    }

    public void setHalfTimeHomeGoals(Integer halfTimeHomeGoals) {
        this.halfTimeHomeGoals = halfTimeHomeGoals;
    }

    public Integer getHalfTimeAwayGoals() {
        return halfTimeAwayGoals;
    }

    public void setHalfTimeAwayGoals(Integer halfTimeAwayGoals) {
        this.halfTimeAwayGoals = halfTimeAwayGoals;
    }

    public String getHalfTimeResult() {
        return halfTimeResult;
    }

    public void setHalfTimeResult(String halfTimeResult) {
        this.halfTimeResult = halfTimeResult;
    }

    public Integer getHomeShots() {
        return homeShots;
    }

    public void setHomeShots(Integer homeShots) {
        this.homeShots = homeShots;
    }

    public Integer getAwayShots() {
        return awayShots;
    }

    public void setAwayShots(Integer awayShots) {
        this.awayShots = awayShots;
    }

    public Integer getHomeShotsOnTarget() {
        return homeShotsOnTarget;
    }

    public void setHomeShotsOnTarget(Integer homeShotsOnTarget) {
        this.homeShotsOnTarget = homeShotsOnTarget;
    }

    public Integer getAwayShotsOnTarget() {
        return awayShotsOnTarget;
    }

    public void setAwayShotsOnTarget(Integer awayShotsOnTarget) {
        this.awayShotsOnTarget = awayShotsOnTarget;
    }

    public Integer getHomeFouls() {
        return homeFouls;
    }

    public void setHomeFouls(Integer homeFouls) {
        this.homeFouls = homeFouls;
    }

    public Integer getAwayFouls() {
        return awayFouls;
    }

    public void setAwayFouls(Integer awayFouls) {
        this.awayFouls = awayFouls;
    }

    public Integer getHomeCorners() {
        return homeCorners;
    }

    public void setHomeCorners(Integer homeCorners) {
        this.homeCorners = homeCorners;
    }

    public Integer getAwayCorners() {
        return awayCorners;
    }

    public void setAwayCorners(Integer awayCorners) {
        this.awayCorners = awayCorners;
    }

    public Integer getHomeYellowCards() {
        return homeYellowCards;
    }

    public void setHomeYellowCards(Integer homeYellowCards) {
        this.homeYellowCards = homeYellowCards;
    }

    public Integer getAwayYellowCards() {
        return awayYellowCards;
    }

    public void setAwayYellowCards(Integer awayYellowCards) {
        this.awayYellowCards = awayYellowCards;
    }

    public Integer getHomeRedCards() {
        return homeRedCards;
    }

    public void setHomeRedCards(Integer homeRedCards) {
        this.homeRedCards = homeRedCards;
    }

    public Integer getAwayRedCards() {
        return awayRedCards;
    }

    public void setAwayRedCards(Integer awayRedCards) {
        this.awayRedCards = awayRedCards;
    }
}
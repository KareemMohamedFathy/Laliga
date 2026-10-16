package com.kareem.laliga.model;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false,unique = true)
    private String name;
    private long providerTeamId;
    public Team(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Team() {
    }

    public long getProviderTeamId() {
        return providerTeamId;
    }

    public void setProviderTeamId(long providerTeamId) {
        this.providerTeamId = providerTeamId;
    }
}
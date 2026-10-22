package com.kareem.laliga.service;

import com.kareem.laliga.dto.FootballMatchesResponse.FootballMatch;
import java.util.List;

public record ValidatedMatches(
        List<FootballMatch> validMatches,
        int rejectedCount
) {}
package com.kareem.laliga.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "laliga.sync.enabled", havingValue = "true")
public class MatchSyncScheduler {
    private static final Logger log = LoggerFactory.getLogger(MatchSyncScheduler.class);
    private final MatchSyncService matchSyncService;
    private final Integer season;

    public MatchSyncScheduler(MatchSyncService matchSyncService, @Value("${laliga.sync.season}") int season) {
        this.matchSyncService = matchSyncService;
        this.season = season;
    }
    @Scheduled(
            initialDelay = 10_000,
            fixedDelayString = "${laliga.sync.interval:PT24H}"
    )
    public void syncMatches() {
        try {
            SyncSummary summary = matchSyncService.syncMatches(season);
            log.info("Synchronization completed for season {}: {}", season, summary);
        } catch (Exception e) {
            log.error("Synchronization failed for season {}", season, e);
        }

}
}

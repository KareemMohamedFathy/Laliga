package com.kareem.laliga.service;

public record SyncSummary(
        int inserted,
        int updated,
        int skipped,
        int rejected
) {}

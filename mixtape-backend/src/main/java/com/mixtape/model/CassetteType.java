package com.mixtape.model;

public enum CassetteType {
    C60(3600),
    C90(5400),
    C120(7200);

    private final int maxDurationSeconds;

    CassetteType(int maxDurationSeconds) {
        this.maxDurationSeconds = maxDurationSeconds;
    }

    public int getMaxDurationSeconds() {
        return maxDurationSeconds;
    }
}
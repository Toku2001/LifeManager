package com.github.toku2001.batch.model;

public record HealthDailyMetrics(
    String targetDate,
    int sleepRecordCount,
    double avgTotalSleep,
    double avgDeepSleep,
    double avgRemSleep,
    double avgAwake,
    String firstSleepStart,
    String lastSleepEnd,
    int heartRateRecordCount,
    double avgHeartRate,
    double minHeartRate,
    double maxHeartRate
) {
}

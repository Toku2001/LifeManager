package com.github.toku2001.dto;

import java.math.BigDecimal;
import java.util.List;

@lombok.Data
public class SleepInfoRequest {

    private Data data;

    @lombok.Data
    public static class Data {
        private List<Metric> metrics;
    }

    @lombok.Data
    public static class Metric {
        private String units;
        private String name;
        private List<SleepRecord> data;
    }

    @lombok.Data
    public static class SleepRecord {
        private String source;
        private String inBedEnd;
        private BigDecimal asleep;
        private BigDecimal awake;
        private String date;
        private BigDecimal rem;
        private String sleepEnd;
        private BigDecimal totalSleep;
        private String inBedStart;
        private String sleepStart;
        private BigDecimal core;
        private BigDecimal inBed;
        private BigDecimal deep;
    }
}

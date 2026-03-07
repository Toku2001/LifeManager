package com.github.toku2001.dto;

import java.math.BigDecimal;
import java.util.List;

@lombok.Data
public class HeartRateInfoRequest {

    private Data data;

    @lombok.Data
    public static class Data {
        private List<Metric> metrics;
    }

    @lombok.Data
    public static class Metric {
        private String units;
        private String name;
        private List<HeartRateRecord> data;
    }

    @lombok.Data
    public static class HeartRateRecord {
        private BigDecimal Max;
        private String source;
        private BigDecimal Min;
        private BigDecimal Avg;
        private String date;
    }
}

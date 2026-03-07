package com.github.toku2001.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.github.toku2001.dto.SleepInfoRequest;

@Repository
public class SleepAnalysisRepository {

    private static final String INSERT_SQL = """
            INSERT INTO sleep_analysis_records (
                metric_name,
                metric_units,
                source,
                date,
                sleep_start,
                sleep_end,
                in_bed_start,
                in_bed_end,
                asleep,
                awake,
                rem,
                total_sleep,
                core,
                in_bed,
                deep
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private final JdbcTemplate jdbcTemplate;

    public SleepAnalysisRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertSleepRecord(String metricName, String metricUnits, SleepInfoRequest.SleepRecord record) {
        return jdbcTemplate.update(
            INSERT_SQL,
            metricName,
            metricUnits,
            record.getSource(),
            record.getDate(),
            record.getSleepStart(),
            record.getSleepEnd(),
            record.getInBedStart(),
            record.getInBedEnd(),
            record.getAsleep(),
            record.getAwake(),
            record.getRem(),
            record.getTotalSleep(),
            record.getCore(),
            record.getInBed(),
            record.getDeep()
        );
    }
}

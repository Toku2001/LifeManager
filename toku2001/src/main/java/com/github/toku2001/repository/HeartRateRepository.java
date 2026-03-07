package com.github.toku2001.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.github.toku2001.dto.HeartRateInfoRequest;

@Repository
public class HeartRateRepository {

    private static final String INSERT_SQL = """
            INSERT INTO heart_rate_records (
                metric_name,
                metric_units,
                source,
                date,
                max,
                min,
                avg
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private final JdbcTemplate jdbcTemplate;

    public HeartRateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insertHeartRateRecord(String metricName, String metricUnits, HeartRateInfoRequest.HeartRateRecord record) {
        return jdbcTemplate.update(
            INSERT_SQL,
            metricName,
            metricUnits,
            record.getSource(),
            record.getDate(),
            record.getMax(),
            record.getMin(),
            record.getAvg()
        );
    }
}

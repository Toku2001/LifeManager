package com.github.toku2001.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.github.toku2001.batch.model.HealthDailyMetrics;

@Repository
public class HealthAnalysisBatchRepository {

    private static final String SLEEP_AGG_SQL = """
            SELECT
                COUNT(*) AS sleep_count,
                COALESCE(AVG(total_sleep), 0) AS avg_total_sleep,
                COALESCE(AVG(deep), 0) AS avg_deep,
                COALESCE(AVG(rem), 0) AS avg_rem,
                COALESCE(AVG(awake), 0) AS avg_awake,
                COALESCE(MIN(sleep_start), '') AS first_sleep_start,
                COALESCE(MAX(sleep_end), '') AS last_sleep_end
            FROM sleep_analysis_records
            WHERE substr(date, 1, 10) = ?
            """;

    private static final String HEART_RATE_AGG_SQL = """
            SELECT
                COUNT(*) AS heart_rate_count,
                COALESCE(AVG(avg), 0) AS avg_heart_rate,
                COALESCE(MIN(min), 0) AS min_heart_rate,
                COALESCE(MAX(max), 0) AS max_heart_rate
            FROM heart_rate_records
            WHERE substr(date, 1, 10) = ?
            """;

    private static final String UPSERT_ANALYSIS_RESULT_SQL = """
            INSERT INTO health_analysis_result (
                target_date,
                input_summary_json,
                analysis_text,
                analysis_json,
                model,
                status,
                error_message
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(target_date) DO UPDATE SET
                input_summary_json = excluded.input_summary_json,
                analysis_text = excluded.analysis_text,
                analysis_json = excluded.analysis_json,
                model = excluded.model,
                status = excluded.status,
                error_message = excluded.error_message,
                created_at = CURRENT_TIMESTAMP
            """;

    private static final String INSERT_JOB_HISTORY_SQL = """
            INSERT INTO health_analysis_job_history (
                job_execution_id,
                from_datetime,
                to_datetime,
                status,
                processed_count,
                message
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String EXISTS_SUCCESS_SQL = """
            SELECT COUNT(*)
            FROM health_analysis_result
            WHERE target_date = ?
              AND status = 'SUCCESS'
            """;

    private final JdbcTemplate jdbcTemplate;

    public HealthAnalysisBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<HealthDailyMetrics> findDailyMetrics(LocalDate targetDate) {
        String date = targetDate.toString();

        SleepAgg sleepAgg = jdbcTemplate.queryForObject(SLEEP_AGG_SQL, this::mapSleepAgg, date);
        HeartRateAgg heartRateAgg = jdbcTemplate.queryForObject(HEART_RATE_AGG_SQL, this::mapHeartRateAgg, date);

        if (sleepAgg == null || heartRateAgg == null) {
            return Optional.empty();
        }

        if (sleepAgg.sleepCount == 0 && heartRateAgg.heartRateCount == 0) {
            return Optional.empty();
        }

        return Optional.of(new HealthDailyMetrics(
            date,
            sleepAgg.sleepCount,
            sleepAgg.avgTotalSleep,
            sleepAgg.avgDeep,
            sleepAgg.avgRem,
            sleepAgg.avgAwake,
            sleepAgg.firstSleepStart,
            sleepAgg.lastSleepEnd,
            heartRateAgg.heartRateCount,
            heartRateAgg.avgHeartRate,
            heartRateAgg.minHeartRate,
            heartRateAgg.maxHeartRate
        ));
    }

    public void upsertAnalysisResult(
        LocalDate targetDate,
        String inputSummaryJson,
        String analysisText,
        String analysisJson,
        String model,
        String status,
        String errorMessage
    ) {
        jdbcTemplate.update(
            UPSERT_ANALYSIS_RESULT_SQL,
            targetDate.toString(),
            inputSummaryJson,
            analysisText,
            analysisJson,
            model,
            status,
            errorMessage
        );
    }

    public void insertJobHistory(
        String jobExecutionId,
        String fromDateTime,
        String toDateTime,
        String status,
        int processedCount,
        String message
    ) {
        jdbcTemplate.update(
            INSERT_JOB_HISTORY_SQL,
            jobExecutionId,
            fromDateTime,
            toDateTime,
            status,
            processedCount,
            message
        );
    }

    public boolean existsSuccessfulResult(LocalDate targetDate) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_SUCCESS_SQL, Integer.class, targetDate.toString());
        return count != null && count > 0;
    }

    private SleepAgg mapSleepAgg(ResultSet rs, int rowNum) throws SQLException {
        return new SleepAgg(
            rs.getInt("sleep_count"),
            rs.getDouble("avg_total_sleep"),
            rs.getDouble("avg_deep"),
            rs.getDouble("avg_rem"),
            rs.getDouble("avg_awake"),
            rs.getString("first_sleep_start"),
            rs.getString("last_sleep_end")
        );
    }

    private HeartRateAgg mapHeartRateAgg(ResultSet rs, int rowNum) throws SQLException {
        return new HeartRateAgg(
            rs.getInt("heart_rate_count"),
            rs.getDouble("avg_heart_rate"),
            rs.getDouble("min_heart_rate"),
            rs.getDouble("max_heart_rate")
        );
    }

    private record SleepAgg(
        int sleepCount,
        double avgTotalSleep,
        double avgDeep,
        double avgRem,
        double avgAwake,
        String firstSleepStart,
        String lastSleepEnd
    ) {
    }

    private record HeartRateAgg(
        int heartRateCount,
        double avgHeartRate,
        double minHeartRate,
        double maxHeartRate
    ) {
    }
}

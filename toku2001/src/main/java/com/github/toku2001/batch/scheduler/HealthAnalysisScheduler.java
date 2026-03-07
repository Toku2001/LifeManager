package com.github.toku2001.batch.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HealthAnalysisScheduler {

    private static final Logger log = LoggerFactory.getLogger(HealthAnalysisScheduler.class);

    private final JobLauncher jobLauncher;
    private final Job healthAnalysisJob;

    @Value("${app.health-analysis.zone:Asia/Tokyo}")
    private String zone;

    public HealthAnalysisScheduler(JobLauncher jobLauncher, @Qualifier("healthAnalysisJob") Job healthAnalysisJob) {
        this.jobLauncher = jobLauncher;
        this.healthAnalysisJob = healthAnalysisJob;
    }

    // 定期実行を有効化中。cron設定に従ってジョブを起動します。
    @Scheduled(
        cron = "${app.health-analysis.cron:0 */1 * * * *}",
        zone = "${app.health-analysis.zone:Asia/Tokyo}"
    )
    public void run() {
        LocalDate targetDate = LocalDate.now(ZoneId.of(zone)).minusDays(1);

        JobParameters jobParameters = new JobParametersBuilder()
            .addString("targetDate", targetDate.toString())
            .addLong("requestedAt", System.currentTimeMillis())
            .toJobParameters();

        try {
            jobLauncher.run(healthAnalysisJob, jobParameters);
            log.info("healthAnalysisJob triggered. targetDate={}", targetDate);
        } catch (Exception e) {
            log.error("Failed to run healthAnalysisJob. targetDate={}", targetDate, e);
        }
    }
}

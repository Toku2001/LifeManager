package com.github.toku2001.batch.tasklet;

import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.toku2001.batch.model.HealthAnalysisResponse;
import com.github.toku2001.batch.model.HealthDailyMetrics;
import com.github.toku2001.repository.HealthAnalysisBatchRepository;
import com.github.toku2001.service.OpenAiHealthAnalysisService;

@Component
public class HealthAnalysisTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(HealthAnalysisTasklet.class);
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HealthAnalysisBatchRepository repository;
    private final OpenAiHealthAnalysisService analysisService;

    public HealthAnalysisTasklet(
        HealthAnalysisBatchRepository repository,
        OpenAiHealthAnalysisService analysisService
    ) {
        this.repository = repository;
        this.analysisService = analysisService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String targetDateParam = (String) chunkContext.getStepContext().getJobParameters().get("targetDate");
        LocalDate targetDate = targetDateParam == null || targetDateParam.isBlank()
            ? LocalDate.now(TOKYO).minusDays(1)
            : LocalDate.parse(targetDateParam);

        long jobExecutionId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();
        String now = java.time.OffsetDateTime.now(TOKYO).toString();

        if (repository.existsSuccessfulResult(targetDate)) {
            log.info("Health analysis already completed. targetDate={}", targetDate);
            repository.insertJobHistory(String.valueOf(jobExecutionId), now, now, "SKIPPED", 0, "already analyzed");
            return RepeatStatus.FINISHED;
        }

        HealthDailyMetrics metrics = repository.findDailyMetrics(targetDate).orElse(null);
        if (metrics == null) {
            log.info("No health records found. targetDate={}", targetDate);
            repository.insertJobHistory(String.valueOf(jobExecutionId), now, now, "NO_DATA", 0, "no records");
            return RepeatStatus.FINISHED;
        }

        String inputSummaryJson = OBJECT_MAPPER.writeValueAsString(metrics);

        try {
            HealthAnalysisResponse response = analysisService.analyze(targetDate, metrics);
            repository.upsertAnalysisResult(
                targetDate,
                inputSummaryJson,
                response.analysisText(),
                response.analysisJson(),
                analysisService.getModel(),
                "SUCCESS",
                null
            );
            repository.insertJobHistory(String.valueOf(jobExecutionId), now, now, "SUCCESS", 1, "analysis completed");
        } catch (Exception e) {
            repository.upsertAnalysisResult(
                targetDate,
                inputSummaryJson,
                null,
                null,
                analysisService.getModel(),
                "ERROR",
                e.getMessage()
            );
            repository.insertJobHistory(String.valueOf(jobExecutionId), now, now, "ERROR", 0, e.getMessage());
            throw e;
        }

        return RepeatStatus.FINISHED;
    }
}

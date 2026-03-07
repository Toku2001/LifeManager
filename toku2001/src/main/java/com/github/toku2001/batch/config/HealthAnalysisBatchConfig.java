package com.github.toku2001.batch.config;

import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.toku2001.batch.tasklet.HealthAnalysisTasklet;

@Configuration
public class HealthAnalysisBatchConfig {

    @Bean
    public Job healthAnalysisJob(JobRepository jobRepository, Step healthAnalysisStep) {
        return new JobBuilder("healthAnalysisJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(healthAnalysisStep)
            .build();
    }

    @Bean
    public Step healthAnalysisStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        HealthAnalysisTasklet healthAnalysisTasklet
    ) {
        return new StepBuilder("healthAnalysisStep", jobRepository)
            .tasklet(healthAnalysisTasklet, transactionManager)
            .build();
    }
}

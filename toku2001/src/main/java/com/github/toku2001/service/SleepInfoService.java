package com.github.toku2001.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.github.toku2001.dto.SleepInfoRequest;
import com.github.toku2001.repository.SleepAnalysisRepository;

@Service
public class SleepInfoService {

    private final SleepAnalysisRepository sleepAnalysisRepository;

    public SleepInfoService(SleepAnalysisRepository sleepAnalysisRepository) {
        this.sleepAnalysisRepository = sleepAnalysisRepository;
    }

    @Transactional
    public int saveSleepInfo(SleepInfoRequest request) {
        if (request == null || request.getData() == null || request.getData().getMetrics() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "data.metrics is required");
        }

        List<SleepInfoRequest.Metric> metrics = request.getData().getMetrics();
        int insertedCount = 0;

        for (SleepInfoRequest.Metric metric : metrics) {
            validateMetric(metric);

            for (SleepInfoRequest.SleepRecord record : metric.getData()) {
                validateRecord(record);
                insertedCount += sleepAnalysisRepository.insertSleepRecord(metric.getName(), metric.getUnits(), record);
            }
        }

        if (insertedCount == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no records to insert");
        }

        return insertedCount;
    }

    private void validateMetric(SleepInfoRequest.Metric metric) {
        if (metric == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "metric item is null");
        }
        if (isBlank(metric.getName()) || isBlank(metric.getUnits())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "metric.name and metric.units are required");
        }
        if (metric.getData() == null || metric.getData().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "metric.data is required");
        }
    }

    private void validateRecord(SleepInfoRequest.SleepRecord record) {
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "record is null");
        }
        if (isBlank(record.getDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "record.date is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

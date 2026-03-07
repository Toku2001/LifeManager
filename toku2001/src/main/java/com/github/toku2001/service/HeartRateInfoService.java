package com.github.toku2001.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.github.toku2001.dto.HeartRateInfoRequest;
import com.github.toku2001.repository.HeartRateRepository;

@Service
public class HeartRateInfoService {

    private final HeartRateRepository heartRateRepository;

    public HeartRateInfoService(HeartRateRepository heartRateRepository) {
        this.heartRateRepository = heartRateRepository;
    }

    @Transactional
    public int saveHeartRateInfo(HeartRateInfoRequest request) {
        if (request == null || request.getData() == null || request.getData().getMetrics() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "data.metrics is required");
        }

        List<HeartRateInfoRequest.Metric> metrics = request.getData().getMetrics();
        int insertedCount = 0;

        for (HeartRateInfoRequest.Metric metric : metrics) {
            validateMetric(metric);

            for (HeartRateInfoRequest.HeartRateRecord record : metric.getData()) {
                validateRecord(record);
                insertedCount += heartRateRepository.insertHeartRateRecord(metric.getName(), metric.getUnits(), record);
            }
        }

        if (insertedCount == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no records to insert");
        }

        return insertedCount;
    }

    private void validateMetric(HeartRateInfoRequest.Metric metric) {
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

    private void validateRecord(HeartRateInfoRequest.HeartRateRecord record) {
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

package com.github.toku2001.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.toku2001.dto.HeartRateInfoRequest;
import com.github.toku2001.dto.HeartRateInfoResponse;
import com.github.toku2001.dto.SleepInfoRequest;
import com.github.toku2001.dto.SleepInfoResponse;
import com.github.toku2001.service.HeartRateInfoService;
import com.github.toku2001.service.SleepInfoService;

@RestController
@RequestMapping("/batch")
public class HealthAutoExportsController {

    @Autowired
    private SleepInfoService sleepInfoService;

    @Autowired
    private HeartRateInfoService heartRateInfoService;

    @PostMapping(value = "/postSleepInfo")
    public ResponseEntity<SleepInfoResponse> postSleepInfo(@RequestBody SleepInfoRequest sleepInfo) {
        int insertedCount = sleepInfoService.saveSleepInfo(sleepInfo);
        return ResponseEntity.ok(new SleepInfoResponse("ok", insertedCount));
    }

    @PostMapping(value = "/postHeartRateInfo")
    public ResponseEntity<HeartRateInfoResponse> postHeartRateInfo(@RequestBody HeartRateInfoRequest heartRateInfo) {
        int insertedCount = heartRateInfoService.saveHeartRateInfo(heartRateInfo);
        return ResponseEntity.ok(new HeartRateInfoResponse("ok", insertedCount));
    }
}

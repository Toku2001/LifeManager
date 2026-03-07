package com.github.toku2001.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartRateInfoResponse {
    private String status;
    private int insertedCount;
}

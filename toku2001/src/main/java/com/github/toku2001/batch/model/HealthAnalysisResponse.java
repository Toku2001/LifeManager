package com.github.toku2001.batch.model;

public record HealthAnalysisResponse(
    String analysisText,
    String analysisJson,
    String rawResponse
) {
}

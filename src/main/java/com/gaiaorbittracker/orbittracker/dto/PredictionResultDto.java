package com.gaiaorbittracker.orbittracker.dto;

import java.util.Map;

public class PredictionResultDto {
    private String summary;
    private Map<String, Object> data;

    public PredictionResultDto() {}

    public PredictionResultDto(String summary, Map<String, Object> data) {
        this.summary = summary;
        this.data = data;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}

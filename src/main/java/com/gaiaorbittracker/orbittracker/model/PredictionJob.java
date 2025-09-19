package com.gaiaorbittracker.orbittracker.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class PredictionJob {

    @Id
    private UUID id;

    private String gaiaId;

    private Instant submittedAt;

    private String status; // PENDING, RUNNING, DONE, FAILED

    @Lob
    @Column(columnDefinition = "TEXT")
    private String resultJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String inputJson;

    public PredictionJob() {}

    public PredictionJob(UUID id, String gaiaId, Instant submittedAt, String status, String inputJson) {
        this.id = id;
        this.gaiaId = gaiaId;
        this.submittedAt = submittedAt;
        this.status = status;
        this.inputJson = inputJson;
    }

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getGaiaId() { return gaiaId; }
    public void setGaiaId(String gaiaId) { this.gaiaId = gaiaId; }

    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResultJson() { return resultJson; }
    public void setResultJson(String resultJson) { this.resultJson = resultJson; }

    public String getInputJson() { return inputJson; }
    public void setInputJson(String inputJson) { this.inputJson = inputJson; }
}

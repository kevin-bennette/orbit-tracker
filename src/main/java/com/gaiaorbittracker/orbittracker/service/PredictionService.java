package com.gaiaorbittracker.orbittracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaiaorbittracker.orbittracker.dto.PredictionResultDto;
import com.gaiaorbittracker.orbittracker.dto.StarInput;
import com.gaiaorbittracker.orbittracker.model.PredictionJob;
import com.gaiaorbittracker.orbittracker.repository.PredictionJobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PredictionService {

    private final PredictionJobRepository repo;
    private final OrbitalCalculator calculator;
    private final ObjectMapper mapper = new ObjectMapper();
    private ExecutorService executor;

    @Value("${gaia.api.key:}")
    private String gaiaApiKey;

    public PredictionService(PredictionJobRepository repo, OrbitalCalculator calculator) {
        this.repo = repo;
        this.calculator = calculator;
    }

    @PostConstruct
    public void init() {
        int pool = Integer.parseInt(System.getProperty("prediction.pool.size", "4"));
        this.executor = Executors.newFixedThreadPool(pool);
    }

    public UUID submitJob(StarInput input) {
        try {
            UUID jobId = UUID.randomUUID();
            String inputJson = mapper.writeValueAsString(input);
            PredictionJob job = new PredictionJob(jobId, input.getGaiaId(), Instant.now(), "PENDING", inputJson);
            repo.save(job);

            executor.submit(() -> processJob(jobId));
            return jobId;
        } catch (Exception e) {
            throw new RuntimeException("Unable to submit job: " + e.getMessage(), e);
        }
    }

    public Optional<PredictionJob> getJob(UUID jobId) {
        return repo.findById(jobId);
    }

    public List<PredictionJob> listAll() {
        return repo.findAll();
    }

    private void processJob(UUID jobId) {
        Optional<PredictionJob> maybe = repo.findById(jobId);
        if (maybe.isEmpty()) return;

        PredictionJob job = maybe.get();
        job.setStatus("RUNNING");
        repo.save(job);

        try {
            StarInput input = mapper.readValue(job.getInputJson(), StarInput.class);
            PredictionResultDto result = calculator.computePrediction(input, gaiaApiKey);

            String resultJson = mapper.writeValueAsString(result);
            job.setResultJson(resultJson);
            job.setStatus("DONE");
            repo.save(job);
        } catch (Exception e) {
            job.setStatus("FAILED");
            try {
                job.setResultJson(mapper.writeValueAsString(Map.of("error", e.getMessage())));
            } catch (Exception ex) { /* ignore */ }
            repo.save(job);
        }
    }
}

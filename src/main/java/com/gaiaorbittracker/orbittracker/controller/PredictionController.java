package com.gaiaorbittracker.orbittracker.controller;

import com.gaiaorbittracker.orbittracker.dto.StarInput;
import com.gaiaorbittracker.orbittracker.model.PredictionJob;
import com.gaiaorbittracker.orbittracker.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PredictionController {

    private final PredictionService service;

    public PredictionController(PredictionService service) {
        this.service = service;
    }

    /**
     * Submit prediction job. Returns jobId immediately (202 Accepted).
     * JSON body = StarInput
     */
    @PostMapping("/predict")
    public ResponseEntity<String> submitPrediction(@RequestBody StarInput input) {
        UUID jobId = service.submitJob(input);
        return ResponseEntity.accepted().body(jobId.toString());
    }

    /** Poll status and result */
    @GetMapping("/status/{jobId}")
    public ResponseEntity<?> getStatus(@PathVariable("jobId") UUID jobId) {
        Optional<PredictionJob> job = service.getJob(jobId);
        if (job.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(job.get());
    }

    @GetMapping("/predictions")
    public ResponseEntity<List<PredictionJob>> listPredictions() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }
}

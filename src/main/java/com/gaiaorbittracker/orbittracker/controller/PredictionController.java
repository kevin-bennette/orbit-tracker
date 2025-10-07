package com.gaiaorbittracker.orbittracker.controller;

import com.gaiaorbittracker.orbittracker.dto.StarInput;
import com.gaiaorbittracker.orbittracker.model.PredictionJob;
import com.gaiaorbittracker.orbittracker.service.PredictionService;
import com.gaiaorbittracker.orbittracker.service.GaiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PredictionController {

    private final PredictionService service;
    private final GaiaService gaiaService;

    public PredictionController(PredictionService service, GaiaService gaiaService) {
        this.service = service;
        this.gaiaService = gaiaService;
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

    @GetMapping("/predictions/export")
    public ResponseEntity<String> exportPredictions() {
        List<PredictionJob> jobs = service.listAll();
        StringBuilder sb = new StringBuilder();
        sb.append("id,gaiaId,submittedAt,status\n");
        for (PredictionJob j : jobs) {
            sb.append(j.getId()).append(',')
              .append(j.getGaiaId() == null ? "" : j.getGaiaId()).append(',')
              .append(j.getSubmittedAt()).append(',')
              .append(j.getStatus()).append('\n');
        }
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .body(sb.toString());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }

    // Batch endpoints
    @PostMapping("/predict/batch")
    public ResponseEntity<List<String>> submitBatch(@RequestBody List<StarInput> inputs) {
        List<String> ids = inputs.stream()
                .map(service::submitJob)
                .map(UUID::toString)
                .collect(Collectors.toList());
        return ResponseEntity.accepted().body(ids);
    }

    @PostMapping("/catalog/metrics")
    public ResponseEntity<Map<String, Map<String, Object>>> fetchCatalog(@RequestBody List<String> names) {
        return ResponseEntity.ok(gaiaService.getStarMetricsByNames(names));
    }
}

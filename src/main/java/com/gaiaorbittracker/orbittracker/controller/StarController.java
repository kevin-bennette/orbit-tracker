package com.gaiaorbittracker.orbittracker.controller;

import com.gaiaorbittracker.orbittracker.service.SimbadService;
import com.gaiaorbittracker.orbittracker.service.GaiaService;
import com.gaiaorbittracker.orbittracker.service.OrbitalCalculator;
import com.gaiaorbittracker.orbittracker.dto.StarInput;
import com.gaiaorbittracker.orbittracker.dto.PredictionResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StarController {

    private final SimbadService simbadService;
    private final GaiaService gaiaService;
    private final OrbitalCalculator orbitalCalculator;

    @Autowired
    public StarController(SimbadService simbadService, GaiaService gaiaService, OrbitalCalculator orbitalCalculator) {
        this.simbadService = simbadService;
        this.gaiaService = gaiaService;
        this.orbitalCalculator = orbitalCalculator;
    }

    @GetMapping("/star")
    public ResponseEntity<String> getStar(@RequestParam String name) {
        try {
            String result = simbadService.getStarByName(name);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/star/simbad")
    public ResponseEntity<Map<String, Object>> getStarFromSimbad(@RequestParam String name) {
        try {
            Map<String, Object> result = simbadService.getStarDataByName(name);
            if (result.containsKey("error")) {
                return ResponseEntity.badRequest().body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/star/gaia")
    public ResponseEntity<Map<String, Object>> getStarFromGaia(@RequestParam String name) {
        try {
            Map<String, Object> result = gaiaService.getStarMetricsByName(name);
            if (result.containsKey("error")) {
                return ResponseEntity.badRequest().body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/star/coordinates")
    public ResponseEntity<Map<String, Object>> getStarByCoordinates(
            @RequestParam double ra, 
            @RequestParam double dec) {
        try {
            Map<String, Object> result = gaiaService.getStarMetrics(ra, dec);
            if (result.containsKey("error")) {
                return ResponseEntity.badRequest().body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/star/predict")
    public ResponseEntity<PredictionResultDto> predictStarMotion(@RequestBody StarInput input) {
        try {
            PredictionResultDto result = orbitalCalculator.computePrediction(input, null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new PredictionResultDto("Error: " + e.getMessage(), Map.of("error", e.getMessage()))
            );
        }
    }

    @GetMapping("/test/sirius")
    public ResponseEntity<Map<String, Object>> testSirius() {
        try {
            Map<String, Object> result = gaiaService.getStarMetricsByName("sirius");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test/vega")
    public ResponseEntity<Map<String, Object>> testVega() {
        try {
            Map<String, Object> result = gaiaService.getStarMetricsByName("vega");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test/fallback")
    public ResponseEntity<Map<String, Object>> testFallback() {
        try {
            Map<String, Object> result = gaiaService.getStarMetricsByName("sirius");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
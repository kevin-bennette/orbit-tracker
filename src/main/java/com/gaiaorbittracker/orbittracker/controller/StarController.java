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

    @GetMapping("/star/rv-diagram")
    public ResponseEntity<Map<String, Object>> getRadialVelocityDiagram(@RequestParam String name,
                                                                        @RequestParam(required = false, defaultValue = "100") int years,
                                                                        @RequestParam(required = false, defaultValue = "50") int steps) {
        try {
            Map<String, Object> metrics = gaiaService.getStarMetricsByName(name);
            if (metrics.containsKey("error")) return ResponseEntity.badRequest().body(metrics);

            StarInput input = new StarInput();
            input.setGaiaId(name);
            input.setTimePeriodYears((double) years);
            input.setTimeSteps(steps);
            PredictionResultDto prediction = orbitalCalculator.computePrediction(input, null);
            @SuppressWarnings("unchecked")
            var preds = (java.util.List<java.util.Map<String, Object>>) prediction.getData().get("predictions");

            java.util.List<Double> t = new java.util.ArrayList<>();
            java.util.List<Double> rv = new java.util.ArrayList<>();
            for (var p : preds) {
                t.add(((Number)p.get("time")).doubleValue());
                rv.add(((Number)p.get("radialVelocityKmS")).doubleValue());
            }
            return ResponseEntity.ok(Map.of(
                "name", name,
                "time", t,
                "radialVelocityKmS", rv
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/star/photometry")
    public ResponseEntity<Map<String, Object>> getPhotometry(@RequestParam String name) {
        try {
            Map<String, Object> metrics = gaiaService.getStarMetricsByName(name);
            if (metrics.containsKey("error")) return ResponseEntity.badRequest().body(metrics);
            Double g = (Double) metrics.get("gMagnitude");
            Double bp = (Double) metrics.get("bpMagnitude");
            Double rp = (Double) metrics.get("rpMagnitude");
            Double bpRp = (Double) metrics.get("bpRp");
            Map<String, Object> out = new java.util.HashMap<>();
            out.put("name", name);
            out.put("G", g);
            out.put("BP", bp);
            out.put("RP", rp);
            out.put("BP_RP", bpRp);
            return ResponseEntity.ok(out);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
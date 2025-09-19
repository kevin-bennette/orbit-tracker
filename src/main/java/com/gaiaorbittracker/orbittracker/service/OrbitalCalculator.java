package com.gaiaorbittracker.orbittracker.service;

import com.gaiaorbittracker.orbittracker.dto.PredictionResultDto;
import com.gaiaorbittracker.orbittracker.dto.StarInput;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Replace the stubbed computePrediction method with your Gaia + orbital mechanics code.
 * When calling external Gaia APIs, use the gaiaApiKey parameter passed from the service.
 */
@Service
public class OrbitalCalculator {

    public PredictionResultDto computePrediction(StarInput input, String gaiaApiKey) throws Exception {
        // TODO: Replace this stub with your real code from the repo.
        // If input.getGaiaId() != null: call Gaia API here using gaiaApiKey, fetch astrometry, then run the algorithm.
        // If raw astrometry present: compute using that.

        Map<String,Object> data = new HashMap<>();
        data.put("ra", input.getRa());
        data.put("dec", input.getDec());
        data.put("pmra", input.getPmra());
        data.put("pmdec", input.getPmdec());
        data.put("parallax", input.getParallax());
        data.put("radialVelocity", input.getRadialVelocity());
        data.put("note", "This is a stub. Replace computePrediction with your orbital code.");

        String summary = (input.getGaiaId() != null ? "Prediction for Gaia ID " + input.getGaiaId() : "Prediction for supplied astrometry");
        return new PredictionResultDto(summary, data);
    }
}

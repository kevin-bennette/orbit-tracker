package com.gaiaorbittracker.orbittracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FallbackStarService {

    @Autowired
    private StellarOrbitalService stellarOrbitalService;

    public Map<String, Object> getMockStarData(String name) {
        String normalizedName = name.toLowerCase().trim();
        StellarOrbitalService.StellarOrbit orbit = stellarOrbitalService.getStellarOrbit(normalizedName);
        
        if (orbit != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("name", orbit.getCommonName());
            result.put("scientificName", orbit.getScientificName());
            result.put("sourceId", generateSourceId(orbit.getCommonName()));
            result.put("ra", orbit.getRa());
            result.put("dec", orbit.getDec());
            result.put("parallax", orbit.getParallax());
            result.put("pmra", orbit.getPmra());
            result.put("pmdec", orbit.getPmdec());
            result.put("radialVelocity", orbit.getRadialVelocity());
            result.put("distanceLy", orbit.getDistanceLy());
            result.put("totalProperMotion", orbit.getTotalProperMotion());
            result.put("gMagnitude", orbit.getGMagnitude());
            result.put("bpRp", 0.0);
            result.put("teff", orbit.getTeff());
            result.put("logg", orbit.getLogg());
            result.put("isMockData", true);
            result.put("hasOrbitalMotion", orbit.hasOrbitalMotion());
            result.put("orbitalPeriod", orbit.getOrbitalPeriod());
            result.put("eccentricity", orbit.getEccentricity());
            result.put("inclination", orbit.getInclination());
            result.put("note", "Using realistic stellar orbital data");
            return result;
        }
        
        return Map.of("error", "Star not found in orbital database: " + name);
    }
    
    private Long generateSourceId(String name) {
        // Generate a consistent source ID based on star name
        return Math.abs(name.hashCode()) + 1000000000000000000L;
    }
    
    public boolean hasMockData(String name) {
        return stellarOrbitalService.hasStellarOrbit(name);
    }
}

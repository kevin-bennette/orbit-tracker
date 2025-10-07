package com.gaiaorbittracker.orbittracker.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FallbackStarService {

    // Mock data for demonstration when external APIs are unavailable
    private static final Map<String, Map<String, Object>> MOCK_STAR_DATA = new HashMap<>();
    
    static {
        // Sirius mock data
        Map<String, Object> sirius = new HashMap<>();
        sirius.put("name", "Sirius");
        sirius.put("sourceId", 2928195921463109632L);
        sirius.put("ra", 101.287155);
        sirius.put("dec", -16.716116);
        sirius.put("parallax", 379.21); // mas
        sirius.put("pmra", -546.01); // mas/yr
        sirius.put("pmdec", -1223.07); // mas/yr
        sirius.put("radialVelocity", -7.6); // km/s
        sirius.put("distanceLy", 8.66);
        sirius.put("totalProperMotion", 1338.0);
        sirius.put("gMagnitude", -1.46);
        sirius.put("bpRp", 0.0);
        sirius.put("teff", 9940.0);
        sirius.put("logg", 4.33);
        MOCK_STAR_DATA.put("sirius", sirius);
        
        // Vega mock data
        Map<String, Object> vega = new HashMap<>();
        vega.put("name", "Vega");
        vega.put("sourceId", 1812091129606434560L);
        vega.put("ra", 279.234734);
        vega.put("dec", 38.783689);
        vega.put("parallax", 130.23); // mas
        vega.put("pmra", 200.94); // mas/yr
        vega.put("pmdec", 286.23); // mas/yr
        vega.put("radialVelocity", -13.9); // km/s
        vega.put("distanceLy", 25.04);
        vega.put("totalProperMotion", 350.0);
        vega.put("gMagnitude", 0.03);
        vega.put("bpRp", 0.0);
        vega.put("teff", 9602.0);
        vega.put("logg", 3.95);
        MOCK_STAR_DATA.put("vega", vega);
        
        // Alpha Centauri mock data
        Map<String, Object> alphaCentauri = new HashMap<>();
        alphaCentauri.put("name", "Alpha Centauri");
        alphaCentauri.put("sourceId", 5853498713050602880L);
        alphaCentauri.put("ra", 219.90085);
        alphaCentauri.put("dec", -60.83562);
        alphaCentauri.put("parallax", 747.1); // mas
        alphaCentauri.put("pmra", -3616.0); // mas/yr
        alphaCentauri.put("pmdec", 802.0); // mas/yr
        alphaCentauri.put("radialVelocity", -21.6); // km/s
        alphaCentauri.put("distanceLy", 4.37);
        alphaCentauri.put("totalProperMotion", 3700.0);
        alphaCentauri.put("gMagnitude", -0.27);
        alphaCentauri.put("bpRp", 0.0);
        alphaCentauri.put("teff", 5790.0);
        alphaCentauri.put("logg", 4.30);
        MOCK_STAR_DATA.put("alpha centauri", alphaCentauri);
    }

    public Map<String, Object> getMockStarData(String name) {
        String normalizedName = name.toLowerCase().trim();
        Map<String, Object> starData = MOCK_STAR_DATA.get(normalizedName);
        
        if (starData != null) {
            // Return a copy to avoid modifying the original
            Map<String, Object> result = new HashMap<>(starData);
            result.put("isMockData", true);
            result.put("note", "Using mock data - external APIs unavailable");
            return result;
        }
        
        return Map.of("error", "Star not found in mock data: " + name);
    }
    
    public boolean hasMockData(String name) {
        return MOCK_STAR_DATA.containsKey(name.toLowerCase().trim());
    }
}

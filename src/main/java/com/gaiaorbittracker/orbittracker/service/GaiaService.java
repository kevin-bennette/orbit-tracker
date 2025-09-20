package com.gaiaorbittracker.orbittracker.service;
import java.util.Map;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Service
public class GaiaService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GAIA_TAP_URL = "https://gea.esac.esa.int/tap-server/tap/sync";

    public String queryGaia(double ra, double dec) {
        try {
            String adql = String.format(
                    "SELECT TOP 1 source_id, ra, dec, parallax, pmra, pmdec " +
                    "FROM gaiadr3.gaia_source " +
                    "WHERE 1=CONTAINS(POINT('ICRS', ra, dec), CIRCLE('ICRS', %f, %f, 0.05))",
                    ra, dec
            );

            String url = GAIA_TAP_URL + "?REQUEST=doQuery&LANG=ADQL&FORMAT=json&QUERY=" +
                         UriUtils.encode(adql, StandardCharsets.UTF_8);

            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to fetch Gaia data\"}";
        }
    }
    public Map<String, String> getStarMetrics(double ra, double dec) {
        String json = queryGaia(ra, dec); // existing method
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json).get("data"); // adjust based on actual JSON structure
            if (root.isEmpty()) return Map.of("error", "Star not found");
        
            JsonNode star = root.get(0);
        
            double parallax = star.get(3).asDouble(); // parallax in mas
            double distance_pc = 1000.0 / parallax; // Gaia gives parallax in mas
            double distance_ly = distance_pc * 3.26156;
        
            double pmra = star.get(4).asDouble();
            double pmdec = star.get(5).asDouble();
            double totalProperMotion = Math.sqrt(pmra*pmra + pmdec*pmdec);
        
            Map<String, String> metrics = new HashMap<>();
            metrics.put("Distance (ly)", String.format("%.2f", distance_ly));
            metrics.put("Proper motion (mas/yr)", String.format("%.2f", totalProperMotion));
            metrics.put("RA (deg)", String.valueOf(star.get(1).asDouble()));
            metrics.put("Dec (deg)", String.valueOf(star.get(2).asDouble()));
        
            return metrics;
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Failed to process star data");
        }
    }
    public Map<String, String> getStarMetricsByName(String name) {
        try {
            String adql = String.format(
                "SELECT TOP 1 source_id, ra, dec, parallax, pmra, pmdec " +
                "FROM gaiadr3.gaia_source " +
                "WHERE main_id='%s'", name
            );
        
            String url = GAIA_TAP_URL + "?REQUEST=doQuery&LANG=ADQL&FORMAT=json&QUERY=" +
                         UriUtils.encode(adql, StandardCharsets.UTF_8);
        
            String json = restTemplate.getForObject(url, String.class);
        
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json).get("data");
            if (root.isEmpty()) return Map.of("error", "Star not found in Gaia");
        
            JsonNode star = root.get(0);
            double parallax = star.get(3).asDouble();
            double distance_ly = 1000.0 / parallax * 3.26156;
            double pmra = star.get(4).asDouble();
            double pmdec = star.get(5).asDouble();
            double totalProperMotion = Math.sqrt(pmra*pmra + pmdec*pmdec);
        
            Map<String, String> metrics = new HashMap<>();
            metrics.put("Name", name);
            metrics.put("Distance (ly)", String.format("%.2f", distance_ly));
            metrics.put("Proper motion (mas/yr)", String.format("%.2f", totalProperMotion));
            metrics.put("RA (deg)", String.valueOf(star.get(1).asDouble()));
            metrics.put("Dec (deg)", String.valueOf(star.get(2).asDouble()));
        
            return metrics;
        
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Failed to fetch star metrics");
        }
    }


}

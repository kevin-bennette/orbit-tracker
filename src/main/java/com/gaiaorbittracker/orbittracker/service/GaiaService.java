package com.gaiaorbittracker.orbittracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class GaiaService {

    @Autowired
    private FallbackStarService fallbackService;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GAIA_TAP_URL = "https://gea.esac.esa.int/tap-server/tap/sync";
    
    // Known star coordinates for fallback searches
    private static final Map<String, double[]> KNOWN_STARS = new HashMap<>();
    static {
        KNOWN_STARS.put("sirius", new double[]{101.287155, -16.716116}); // Sirius
        KNOWN_STARS.put("vega", new double[]{279.234734, 38.783689}); // Vega
        KNOWN_STARS.put("alpha centauri", new double[]{219.90085, -60.83562}); // Alpha Centauri
        KNOWN_STARS.put("betelgeuse", new double[]{88.792958, 7.407064}); // Betelgeuse
        KNOWN_STARS.put("rigel", new double[]{78.634467, -8.201638}); // Rigel
        KNOWN_STARS.put("procyon", new double[]{114.825494, 5.224993}); // Procyon
        KNOWN_STARS.put("altair", new double[]{297.695827, 8.868321}); // Altair
        KNOWN_STARS.put("arcturus", new double[]{213.915300, 19.182409}); // Arcturus
        KNOWN_STARS.put("spica", new double[]{201.298247, -11.161319}); // Spica
        KNOWN_STARS.put("antares", new double[]{247.351915, -26.432002}); // Antares
    }

    public String queryGaia(double ra, double dec) {
        try {
            String adql = String.format(
                    "SELECT TOP 1 source_id, ra, dec, parallax, pmra, pmdec, radial_velocity, " +
                    "phot_g_mean_mag, bp_rp, teff_gspphot, logg_gspphot, " +
                    "astrometric_excess_noise, astrometric_excess_noise_sig " +
                    "FROM gaiadr3.gaia_source " +
                    "WHERE 1=CONTAINS(POINT('ICRS', ra, dec), CIRCLE('ICRS', %f, %f, 0.1)) " +
                    "AND parallax > 0 AND parallax IS NOT NULL " +
                    "ORDER BY phot_g_mean_mag ASC",
                    ra, dec
            );

            String url = GAIA_TAP_URL + "?REQUEST=doQuery&LANG=ADQL&FORMAT=json&QUERY=" +
                         UriUtils.encode(adql, StandardCharsets.UTF_8);

            System.out.println("Gaia Query URL: " + url);
            String result = restTemplate.getForObject(url, String.class);
            System.out.println("Gaia Response: " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to fetch Gaia data: " + e.getMessage() + "\"}";
        }
    }

    public String queryGaiaByName(String name) {
        // First try to get coordinates from known stars
        String normalizedName = name.toLowerCase().trim();
        double[] coords = KNOWN_STARS.get(normalizedName);
        
        if (coords != null) {
            System.out.println("Found known star: " + name + " at coordinates: " + coords[0] + ", " + coords[1]);
            return queryGaia(coords[0], coords[1]);
        }
        
        // If not found in known stars, try SIMBAD first to get coordinates
        try {
            Map<String, Object> simbadData = getSimbadCoordinates(name);
            if (simbadData.containsKey("ra") && simbadData.containsKey("dec")) {
                double ra = (Double) simbadData.get("ra");
                double dec = (Double) simbadData.get("dec");
                System.out.println("Got coordinates from SIMBAD: " + ra + ", " + dec);
                return queryGaia(ra, dec);
            }
        } catch (Exception e) {
            System.out.println("SIMBAD lookup failed: " + e.getMessage());
        }
        
        return "{\"error\":\"Star not found in known catalog or SIMBAD\"}";
    }
    
    private Map<String, Object> getSimbadCoordinates(String name) {
        try {
            String url = "http://simbad.u-strasbg.fr/simbad/sim-id?output.format=VOTable&Ident=" + name;
            String votableXml = restTemplate.getForObject(url, String.class);
            
            return parseSimbadVOTable(votableXml);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get SIMBAD coordinates: " + e.getMessage());
        }
    }
    
    private Map<String, Object> parseSimbadVOTable(String votableXml) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Simple XML parsing for coordinates
            if (votableXml.contains("<TD>") && votableXml.contains("</TD>")) {
                String[] parts = votableXml.split("<TD>");
                if (parts.length >= 4) {
                    // Extract RA and Dec from the VOTable
                    String raStr = extractValue(parts[2]);
                    String decStr = extractValue(parts[3]);
                    
                    Double ra = parseCoordinate(raStr);
                    Double dec = parseCoordinate(decStr);
                    
                    if (ra != null && dec != null) {
                        result.put("ra", ra);
                        result.put("dec", dec);
                        result.put("name", "Unknown");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing SIMBAD VOTable: " + e.getMessage());
        }
        
        return result;
    }
    
    private String extractValue(String xmlPart) {
        try {
            int start = xmlPart.indexOf(">");
            int end = xmlPart.indexOf("</TD>");
            if (start > 0 && end > start) {
                return xmlPart.substring(start + 1, end).trim();
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return "";
    }
    
    private Double parseCoordinate(String coordStr) {
        try {
            if (coordStr == null || coordStr.trim().isEmpty()) {
                return null;
            }
            
            // Handle different coordinate formats
            if (coordStr.contains(":")) {
                // Parse sexagesimal format (HH:MM:SS.ss)
                String[] parts = coordStr.split(":");
                if (parts.length >= 2) {
                    double hours = Double.parseDouble(parts[0]);
                    double minutes = Double.parseDouble(parts[1]);
                    double seconds = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0;
                    
                    // Convert to decimal degrees
                    return hours * 15.0 + minutes * 0.25 + seconds * 0.004166666666666667;
                }
            } else {
                // Parse decimal format
                return Double.parseDouble(coordStr.replaceAll("[^0-9.-]", ""));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    public Map<String, Object> getStarMetrics(double ra, double dec) {
        String json = queryGaia(ra, dec);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json).get("data");
            if (root.isEmpty()) return Map.of("error", "Star not found");
        
            JsonNode star = root.get(0);
        
            double parallax = star.get(3).asDouble(); // parallax in mas
            double distance_pc = 1000.0 / parallax; // Gaia gives parallax in mas
            double distance_ly = distance_pc * 3.26156;
        
            double pmra = star.get(4).asDouble();
            double pmdec = star.get(5).asDouble();
            double totalProperMotion = Math.sqrt(pmra*pmra + pmdec*pmdec);
        
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("sourceId", star.get(0).asLong());
            metrics.put("ra", star.get(1).asDouble());
            metrics.put("dec", star.get(2).asDouble());
            metrics.put("parallax", parallax);
            metrics.put("pmra", pmra);
            metrics.put("pmdec", pmdec);
            metrics.put("radialVelocity", star.get(6).isNull() ? null : star.get(6).asDouble());
            metrics.put("distanceLy", distance_ly);
            metrics.put("totalProperMotion", totalProperMotion);
            metrics.put("gMagnitude", star.get(7).isNull() ? null : star.get(7).asDouble());
            metrics.put("bpRp", star.get(8).isNull() ? null : star.get(8).asDouble());
            metrics.put("teff", star.get(9).isNull() ? null : star.get(9).asDouble());
            metrics.put("logg", star.get(10).isNull() ? null : star.get(10).asDouble());
        
            return metrics;
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Failed to process star data");
        }
    }
    public Map<String, Object> getStarMetricsByName(String name) {
        try {
            String json = queryGaiaByName(name);
            
            // Check if we got an error response
            if (json.contains("\"error\"")) {
                System.out.println("Gaia API failed, trying fallback for: " + name);
                // Try fallback service
                if (fallbackService.hasMockData(name)) {
                    return fallbackService.getMockStarData(name);
                }
                return Map.of("error", "Star not found and no fallback data available");
            }
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json).get("data");
            if (root.isEmpty()) {
                // Try fallback service
                if (fallbackService.hasMockData(name)) {
                    return fallbackService.getMockStarData(name);
                }
                return Map.of("error", "Star not found in Gaia");
            }
        
            JsonNode star = root.get(0);
            double parallax = star.get(3).asDouble();
            double distance_ly = 1000.0 / parallax * 3.26156;
            double pmra = star.get(4).asDouble();
            double pmdec = star.get(5).asDouble();
            double totalProperMotion = Math.sqrt(pmra*pmra + pmdec*pmdec);
        
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("name", name);
            metrics.put("sourceId", star.get(0).asLong());
            metrics.put("ra", star.get(1).asDouble());
            metrics.put("dec", star.get(2).asDouble());
            metrics.put("parallax", parallax);
            metrics.put("pmra", pmra);
            metrics.put("pmdec", pmdec);
            metrics.put("radialVelocity", star.get(6).isNull() ? null : star.get(6).asDouble());
            metrics.put("distanceLy", distance_ly);
            metrics.put("totalProperMotion", totalProperMotion);
            metrics.put("gMagnitude", star.get(7).isNull() ? null : star.get(7).asDouble());
            metrics.put("bpRp", star.get(8).isNull() ? null : star.get(8).asDouble());
            metrics.put("teff", star.get(9).isNull() ? null : star.get(9).asDouble());
            metrics.put("logg", star.get(10).isNull() ? null : star.get(10).asDouble());
            metrics.put("isMockData", false);
        
            return metrics;
        
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in getStarMetricsByName, trying fallback for: " + name);
            // Try fallback service
            if (fallbackService.hasMockData(name)) {
                return fallbackService.getMockStarData(name);
            }
            return Map.of("error", "Failed to fetch star metrics: " + e.getMessage());
        }
    }


}

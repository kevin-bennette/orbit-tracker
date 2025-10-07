package com.gaiaorbittracker.orbittracker.service;

import com.gaiaorbittracker.orbittracker.dto.PredictionResultDto;
import com.gaiaorbittracker.orbittracker.dto.StarInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Orbital mechanics calculator for stellar motion prediction using Gaia DR3 data
 */
@Service
public class OrbitalCalculator {

    @Autowired
    private GaiaService gaiaService;

    // Constants
    private static final double AU_TO_PC = 4.8481368e-6; // Astronomical units to parsecs
    private static final double PC_TO_AU = 1.0 / AU_TO_PC;
    private static final double MAS_TO_RAD = Math.PI / (180.0 * 3600.0 * 1000.0); // milliarcseconds to radians
    private static final double KM_S_TO_AU_YR = 0.210945; // km/s to AU/year

    public PredictionResultDto computePrediction(StarInput input, String gaiaApiKey) throws Exception {
        Map<String, Object> starData;
        
        // Get star data from Gaia if Gaia ID is provided, otherwise use input data
        if (input.getGaiaId() != null) {
            starData = gaiaService.getStarMetricsByName(input.getGaiaId());
            if (starData.containsKey("error")) {
                throw new Exception("Failed to fetch Gaia data: " + starData.get("error"));
            }
        } else {
            // Use provided astrometric data
            starData = new HashMap<>();
            starData.put("ra", input.getRa());
            starData.put("dec", input.getDec());
            starData.put("parallax", input.getParallax());
            starData.put("pmra", input.getPmra());
            starData.put("pmdec", input.getPmdec());
            starData.put("radialVelocity", input.getRadialVelocity());
        }

        // Validate required data
        validateStarData(starData);

        // Set default time period if not provided
        double timePeriodYears = input.getTimePeriodYears() != null ? input.getTimePeriodYears() : 100.0;
        int timeSteps = input.getTimeSteps() != null ? input.getTimeSteps() : 50;

        // Calculate orbital predictions
        List<Map<String, Object>> predictions = calculateOrbitalMotion(starData, timePeriodYears, timeSteps);
        
        // Calculate summary statistics
        Map<String, Object> summary = calculateSummaryStats(predictions, starData);

        Map<String, Object> result = new HashMap<>();
        result.put("starData", starData);
        result.put("predictions", predictions);
        result.put("summary", summary);
        result.put("timePeriodYears", timePeriodYears);
        result.put("timeSteps", timeSteps);

        String summaryText = String.format("Orbital prediction for %s over %.1f years with %d time steps", 
            input.getGaiaId() != null ? input.getGaiaId() : "provided coordinates", 
            timePeriodYears, timeSteps);

        return new PredictionResultDto(summaryText, result);
    }

    private void validateStarData(Map<String, Object> starData) throws Exception {
        if (starData.get("ra") == null || starData.get("dec") == null) {
            throw new Exception("RA and Dec are required");
        }
        if (starData.get("parallax") == null || (Double) starData.get("parallax") <= 0) {
            throw new Exception("Valid parallax is required");
        }
        if (starData.get("pmra") == null || starData.get("pmdec") == null) {
            throw new Exception("Proper motions are required");
        }
    }

    private List<Map<String, Object>> calculateOrbitalMotion(Map<String, Object> starData, 
                                                           double timePeriodYears, int timeSteps) {
        List<Map<String, Object>> predictions = new ArrayList<>();

        // Extract initial conditions
        double ra0 = Math.toRadians((Double) starData.get("ra"));
        double dec0 = Math.toRadians((Double) starData.get("dec"));
        double parallax = (Double) starData.get("parallax"); // in mas
        double pmra = (Double) starData.get("pmra"); // in mas/yr
        double pmdec = (Double) starData.get("pmdec"); // in mas/yr
        Double radialVelocity = (Double) starData.get("radialVelocity"); // in km/s
        
        // Check if this star has orbital motion
        boolean hasOrbitalMotion = starData.containsKey("hasOrbitalMotion") && 
                                 (Boolean) starData.get("hasOrbitalMotion");
        double orbitalPeriod = hasOrbitalMotion ? (Double) starData.get("orbitalPeriod") : 0.0;
        double eccentricity = hasOrbitalMotion ? (Double) starData.get("eccentricity") : 0.0;
        double inclination = hasOrbitalMotion ? (Double) starData.get("inclination") : 0.0;

        // Convert to physical units
        double distance_pc = 1000.0 / parallax; // distance in parsecs
        double distance_au = distance_pc * PC_TO_AU; // distance in AU
        
        // Convert proper motions to radians per year
        double pmra_rad_yr = pmra * MAS_TO_RAD;
        double pmdec_rad_yr = pmdec * MAS_TO_RAD;
        
        // Convert radial velocity to AU/year
        double rv_au_yr = radialVelocity != null ? radialVelocity * KM_S_TO_AU_YR : 0.0;

        // Calculate time steps
        double dt = timePeriodYears / timeSteps;

        for (int i = 0; i <= timeSteps; i++) {
            double t = i * dt; // time in years
            
            double ra_deg, dec_deg, current_distance_ly;
            
            if (hasOrbitalMotion && orbitalPeriod > 0) {
                // Calculate orbital motion
                Map<String, Double> orbitalPosition = calculateOrbitalPosition(
                    ra0, dec0, distance_au, orbitalPeriod, eccentricity, inclination, t
                );
                ra_deg = orbitalPosition.get("ra");
                dec_deg = orbitalPosition.get("dec");
                current_distance_ly = orbitalPosition.get("distance") * 3.26156;
            } else {
                // Simple linear motion model for single stars
                double ra = ra0 + pmra_rad_yr * t;
                double dec = dec0 + pmdec_rad_yr * t;
                
                ra_deg = Math.toDegrees(ra);
                dec_deg = Math.toDegrees(dec);
                
                // Calculate current distance (assuming constant radial velocity)
                double current_distance_au = distance_au + rv_au_yr * t;
                current_distance_ly = current_distance_au * 3.26156;
            }
            
            // Calculate tangential velocity (proper motion * distance)
            double current_distance_au = current_distance_ly / 3.26156;
            double tangential_velocity_km_s = Math.sqrt(
                Math.pow(pmra_rad_yr * current_distance_au, 2) + 
                Math.pow(pmdec_rad_yr * current_distance_au, 2)
            ) * (1.0 / KM_S_TO_AU_YR);
            
            // Calculate total velocity
            double total_velocity_km_s = Math.sqrt(
                Math.pow(tangential_velocity_km_s, 2) + 
                Math.pow(rv_au_yr * (1.0 / KM_S_TO_AU_YR), 2)
            );
            
            // Calculate angular separation from initial position
            double angular_separation_arcsec = calculateAngularSeparation(
                Math.toDegrees(ra0), Math.toDegrees(dec0), ra_deg, dec_deg
            ) * 3600.0; // convert to arcseconds

            // Calculate position relative to solar system barycenter
            Map<String, Object> solarSystemPosition = calculateSolarSystemPosition(
                ra_deg, dec_deg, current_distance_ly, t
            );

            Map<String, Object> prediction = new HashMap<>();
            prediction.put("time", t);
            prediction.put("ra", ra_deg);
            prediction.put("dec", dec_deg);
            prediction.put("distanceLy", current_distance_ly);
            prediction.put("tangentialVelocityKmS", tangential_velocity_km_s);
            prediction.put("radialVelocityKmS", radialVelocity != null ? radialVelocity : 0.0);
            prediction.put("totalVelocityKmS", total_velocity_km_s);
            prediction.put("angularSeparationArcsec", angular_separation_arcsec);
            prediction.put("pmra", pmra);
            prediction.put("pmdec", pmdec);
            prediction.put("hasOrbitalMotion", hasOrbitalMotion);
            prediction.put("orbitalPeriod", orbitalPeriod);
            
            // Add solar system relative positions
            prediction.putAll(solarSystemPosition);
            
            predictions.add(prediction);
        }

        return predictions;
    }
    
    private Map<String, Double> calculateOrbitalPosition(double ra0, double dec0, double distance_au,
                                                       double orbitalPeriod, double eccentricity, 
                                                       double inclination, double timeYears) {
        Map<String, Double> result = new HashMap<>();
        
        // Calculate mean anomaly
        double meanAnomaly = 2.0 * Math.PI * (timeYears % orbitalPeriod) / orbitalPeriod;
        
        // Solve Kepler's equation for eccentric anomaly (simplified)
        double eccentricAnomaly = meanAnomaly + eccentricity * Math.sin(meanAnomaly);
        
        // Calculate true anomaly
        double trueAnomaly = 2.0 * Math.atan(Math.sqrt((1.0 + eccentricity) / (1.0 - eccentricity)) * 
                                            Math.tan(eccentricAnomaly / 2.0));
        
        // Calculate orbital radius
        double orbitalRadius = distance_au * (1.0 - eccentricity * eccentricity) / 
                              (1.0 + eccentricity * Math.cos(trueAnomaly));
        
        // Calculate orbital motion in the orbital plane
        double orbitalX = orbitalRadius * Math.cos(trueAnomaly);
        double orbitalY = orbitalRadius * Math.sin(trueAnomaly) * Math.cos(Math.toRadians(inclination));
        double orbitalZ = orbitalRadius * Math.sin(trueAnomaly) * Math.sin(Math.toRadians(inclination));
        
        // Convert back to RA/Dec (simplified transformation)
        double ra_offset = Math.toDegrees(orbitalX / (distance_au * Math.cos(Math.toRadians(Math.toDegrees(dec0)))));
        double dec_offset = Math.toDegrees(orbitalY / distance_au);
        
        // Use orbitalZ for more accurate 3D transformation
        double dec_offset_3d = Math.toDegrees(orbitalZ / distance_au);
        
        result.put("ra", Math.toDegrees(ra0) + ra_offset);
        result.put("dec", Math.toDegrees(dec0) + dec_offset + dec_offset_3d);
        result.put("distance", orbitalRadius);
        
        return result;
    }
    
    private Map<String, Object> calculateSolarSystemPosition(double ra, double dec, double distanceLy, double timeYears) {
        Map<String, Object> position = new HashMap<>();
        
        // Convert to Cartesian coordinates relative to solar system
        double ra_rad = Math.toRadians(ra);
        double dec_rad = Math.toRadians(dec);
        
        // Convert to parsecs for calculations
        double distance_pc = distanceLy / 3.26156;
        
        // Calculate Cartesian coordinates (X, Y, Z) in parsecs
        double x = distance_pc * Math.cos(dec_rad) * Math.cos(ra_rad);
        double y = distance_pc * Math.cos(dec_rad) * Math.sin(ra_rad);
        double z = distance_pc * Math.sin(dec_rad);
        
        // Calculate distance from solar system barycenter
        double distanceFromSun = Math.sqrt(x*x + y*y + z*z);
        
        // Calculate galactic coordinates (simplified)
        double galacticL = Math.toDegrees(Math.atan2(y, x)) + 180.0; // Galactic longitude
        double galacticB = Math.toDegrees(Math.asin(z / distanceFromSun)); // Galactic latitude
        
        // Calculate motion relative to solar system
        double motionRelativeToSun = distanceFromSun * 0.00474; // km/s (simplified)
        
        position.put("xParsecs", x);
        position.put("yParsecs", y);
        position.put("zParsecs", z);
        position.put("distanceFromSunPc", distanceFromSun);
        position.put("distanceFromSunLy", distanceFromSun * 3.26156);
        position.put("galacticLongitude", galacticL);
        position.put("galacticLatitude", galacticB);
        position.put("motionRelativeToSunKmS", motionRelativeToSun);
        
        // Add reference information
        position.put("referenceSystem", "Solar System Barycenter");
        position.put("epoch", "J2000.0");
        
        return position;
    }

    private double calculateAngularSeparation(double ra1, double dec1, double ra2, double dec2) {
        // Convert to radians
        double ra1_rad = Math.toRadians(ra1);
        double dec1_rad = Math.toRadians(dec1);
        double ra2_rad = Math.toRadians(ra2);
        double dec2_rad = Math.toRadians(dec2);
        
        // Calculate angular separation using spherical trigonometry
        double cos_sep = Math.sin(dec1_rad) * Math.sin(dec2_rad) + 
                        Math.cos(dec1_rad) * Math.cos(dec2_rad) * Math.cos(ra2_rad - ra1_rad);
        
        // Handle numerical precision issues
        cos_sep = Math.max(-1.0, Math.min(1.0, cos_sep));
        
        return Math.acos(cos_sep); // returns in radians
    }

    private Map<String, Object> calculateSummaryStats(List<Map<String, Object>> predictions, 
                                                    Map<String, Object> starData) {
        Map<String, Object> summary = new HashMap<>();
        
        if (predictions.isEmpty()) {
            return summary;
        }
        
        // Initial and final positions
        Map<String, Object> initial = predictions.get(0);
        Map<String, Object> final_pos = predictions.get(predictions.size() - 1);
        
        summary.put("initialRa", initial.get("ra"));
        summary.put("initialDec", initial.get("dec"));
        summary.put("finalRa", final_pos.get("ra"));
        summary.put("finalDec", final_pos.get("dec"));
        
        // Calculate total displacement
        double ra_displacement = (Double) final_pos.get("ra") - (Double) initial.get("ra");
        double dec_displacement = (Double) final_pos.get("dec") - (Double) initial.get("dec");
        double total_displacement_arcsec = Math.sqrt(ra_displacement * ra_displacement + dec_displacement * dec_displacement) * 3600.0;
        
        summary.put("totalDisplacementArcsec", total_displacement_arcsec);
        summary.put("raDisplacementArcsec", ra_displacement * 3600.0);
        summary.put("decDisplacementArcsec", dec_displacement * 3600.0);
        
        // Velocity statistics
        double avgTangentialVelocity = predictions.stream()
            .mapToDouble(p -> (Double) p.get("tangentialVelocityKmS"))
            .average().orElse(0.0);
        
        double maxTangentialVelocity = predictions.stream()
            .mapToDouble(p -> (Double) p.get("tangentialVelocityKmS"))
            .max().orElse(0.0);
        
        summary.put("averageTangentialVelocityKmS", avgTangentialVelocity);
        summary.put("maxTangentialVelocityKmS", maxTangentialVelocity);
        
        // Distance change
        double initialDistance = (Double) initial.get("distanceLy");
        double finalDistance = (Double) final_pos.get("distanceLy");
        summary.put("distanceChangeLy", finalDistance - initialDistance);
        summary.put("initialDistanceLy", initialDistance);
        summary.put("finalDistanceLy", finalDistance);
        
        return summary;
    }
}

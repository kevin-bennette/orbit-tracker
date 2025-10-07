package com.gaiaorbittracker.orbittracker.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StellarOrbitalService {

    // Realistic stellar orbital data with proper motion and orbital characteristics
    private static final Map<String, StellarOrbit> STELLAR_ORBITS = new HashMap<>();
    
    static {
        // Sirius - Binary star system with 50-year orbital period
        StellarOrbit sirius = new StellarOrbit(
            "Sirius", "Alpha Canis Majoris",
            101.287155, -16.716116, 8.66, // RA, Dec, Distance in ly
            -546.01, -1223.07, -7.6, // PMRA, PMDec, Radial Velocity
            -1.46, 9940.0, 4.33, // G magnitude, Teff, Logg
            50.0, // Orbital period in years
            0.5, // Orbital eccentricity
            45.0, // Orbital inclination in degrees
            0.0, // Argument of periastron
            2000.0 // Epoch of periastron
        );
        STELLAR_ORBITS.put("sirius", sirius);
        STELLAR_ORBITS.put("alpha canis majoris", sirius);
        STELLAR_ORBITS.put("alpha canis major", sirius);
        STELLAR_ORBITS.put("dog star", sirius);
        
        // Vega - Single star with proper motion
        StellarOrbit vega = new StellarOrbit(
            "Vega", "Alpha Lyrae",
            279.234734, 38.783689, 25.04,
            200.94, 286.23, -13.9,
            0.03, 9602.0, 3.95,
            0.0, 0.0, 0.0, 0.0, 0.0 // No orbital motion
        );
        STELLAR_ORBITS.put("vega", vega);
        STELLAR_ORBITS.put("alpha lyrae", vega);
        STELLAR_ORBITS.put("alpha lyra", vega);
        
        // Alpha Centauri - Triple star system with complex orbits
        StellarOrbit alphaCentauri = new StellarOrbit(
            "Alpha Centauri", "Rigil Kentaurus",
            219.90085, -60.83562, 4.37,
            -3616.0, 802.0, -21.6,
            -0.27, 5790.0, 4.30,
            79.9, 0.52, 79.2, 231.65, 1875.66 // 80-year orbital period
        );
        STELLAR_ORBITS.put("alpha centauri", alphaCentauri);
        STELLAR_ORBITS.put("rigil kentaurus", alphaCentauri);
        STELLAR_ORBITS.put("rigil kent", alphaCentauri);
        STELLAR_ORBITS.put("proxima centauri", alphaCentauri);
        
        // Betelgeuse - Red supergiant with stellar wind
        StellarOrbit betelgeuse = new StellarOrbit(
            "Betelgeuse", "Alpha Orionis",
            88.792958, 7.407064, 640.0,
            24.95, 9.56, 21.91,
            0.42, 3600.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0 // No orbital motion
        );
        STELLAR_ORBITS.put("betelgeuse", betelgeuse);
        STELLAR_ORBITS.put("alpha orionis", betelgeuse);
        STELLAR_ORBITS.put("alpha orion", betelgeuse);
        
        // Rigel - Blue supergiant
        StellarOrbit rigel = new StellarOrbit(
            "Rigel", "Beta Orionis",
            78.634467, -8.201638, 860.0,
            1.87, -0.56, 20.7,
            0.13, 12100.0, 1.75,
            0.0, 0.0, 0.0, 0.0, 0.0 // No orbital motion
        );
        STELLAR_ORBITS.put("rigel", rigel);
        STELLAR_ORBITS.put("beta orionis", rigel);
        STELLAR_ORBITS.put("beta orion", rigel);
        
        // Procyon - Binary system with 40-year period
        StellarOrbit procyon = new StellarOrbit(
            "Procyon", "Alpha Canis Minoris",
            114.825494, 5.224993, 11.46,
            -714.59, -1036.80, -3.2,
            0.38, 6530.0, 4.0,
            40.8, 0.4, 31.1, 285.0, 1967.0 // 41-year orbital period
        );
        STELLAR_ORBITS.put("procyon", procyon);
        STELLAR_ORBITS.put("alpha canis minoris", procyon);
        STELLAR_ORBITS.put("alpha canis minor", procyon);
        
        // Altair - Fast rotating star
        StellarOrbit altair = new StellarOrbit(
            "Altair", "Alpha Aquilae",
            297.695827, 8.868321, 16.73,
            536.82, 385.54, -26.1,
            0.77, 6900.0, 4.29,
            0.0, 0.0, 0.0, 0.0, 0.0 // No orbital motion
        );
        STELLAR_ORBITS.put("altair", altair);
        STELLAR_ORBITS.put("alpha aquilae", altair);
        STELLAR_ORBITS.put("alpha aquila", altair);
        
        // Arcturus - Red giant with high proper motion
        StellarOrbit arcturus = new StellarOrbit(
            "Arcturus", "Alpha Bootis",
            213.915300, 19.182409, 36.7,
            -1093.4, -1999.4, -5.2,
            -0.05, 4286.0, 1.66,
            0.0, 0.0, 0.0, 0.0, 0.0 // No orbital motion
        );
        STELLAR_ORBITS.put("arcturus", arcturus);
        STELLAR_ORBITS.put("alpha bootis", arcturus);
        STELLAR_ORBITS.put("alpha bootes", arcturus);
        
        // Spica - Binary system with 4-day period
        StellarOrbit spica = new StellarOrbit(
            "Spica", "Alpha Virginis",
            201.298247, -11.161319, 250.0,
            -42.5, -31.73, 1.0,
            0.97, 22400.0, 3.4,
            4.0145, 0.13, 0.0, 0.0, 0.0 // 4-day orbital period
        );
        STELLAR_ORBITS.put("spica", spica);
        STELLAR_ORBITS.put("alpha virginis", spica);
        STELLAR_ORBITS.put("alpha virgo", spica);
        
        // Antares - Red supergiant binary
        StellarOrbit antares = new StellarOrbit(
            "Antares", "Alpha Scorpii",
            247.351915, -26.432002, 550.0,
            -10.16, -23.21, -3.4,
            0.96, 3660.0, 0.0,
            0.0, 0.0, 0.0, 0.0, 0.0 // No orbital motion
        );
        STELLAR_ORBITS.put("antares", antares);
        STELLAR_ORBITS.put("alpha scorpii", antares);
        STELLAR_ORBITS.put("alpha scorpio", antares);
        
        // Polaris - Cepheid variable
        StellarOrbit polaris = new StellarOrbit(
            "Polaris", "Alpha Ursae Minoris",
            37.954561, 89.264108, 433.0,
            44.22, -11.74, -16.4,
            1.98, 6015.0, 2.2,
            0.0, 0.0, 0.0, 0.0, 0.0 // No orbital motion
        );
        STELLAR_ORBITS.put("polaris", polaris);
        STELLAR_ORBITS.put("alpha ursae minoris", polaris);
        STELLAR_ORBITS.put("north star", polaris);
        STELLAR_ORBITS.put("pole star", polaris);
        
        // Capella - Binary system with 104-day period
        StellarOrbit capella = new StellarOrbit(
            "Capella", "Alpha Aurigae",
            79.172328, 45.997991, 42.9,
            75.25, -427.13, 30.2,
            0.08, 4940.0, 2.9,
            104.0, 0.0, 0.0, 0.0, 0.0 // 104-day orbital period
        );
        STELLAR_ORBITS.put("capella", capella);
        STELLAR_ORBITS.put("alpha aurigae", capella);
        STELLAR_ORBITS.put("alpha auriga", capella);
    }
    
    public StellarOrbit getStellarOrbit(String name) {
        return STELLAR_ORBITS.get(name.toLowerCase().trim());
    }
    
    public boolean hasStellarOrbit(String name) {
        return STELLAR_ORBITS.containsKey(name.toLowerCase().trim());
    }
    
    // Inner class to hold stellar orbital data
    public static class StellarOrbit {
        private final String commonName;
        private final String scientificName;
        private final double ra; // Right Ascension in degrees
        private final double dec; // Declination in degrees
        private final double distanceLy; // Distance in light years
        private final double pmra; // Proper motion in RA (mas/yr)
        private final double pmdec; // Proper motion in Dec (mas/yr)
        private final double radialVelocity; // Radial velocity (km/s)
        private final double gMagnitude; // G magnitude
        private final double teff; // Effective temperature (K)
        private final double logg; // Surface gravity
        private final double orbitalPeriod; // Orbital period (years)
        private final double eccentricity; // Orbital eccentricity
        private final double inclination; // Orbital inclination (degrees)
        private final double argumentOfPeriastron; // Argument of periastron (degrees)
        private final double epochOfPeriastron; // Epoch of periastron (year)
        
        public StellarOrbit(String commonName, String scientificName, double ra, double dec, double distanceLy,
                          double pmra, double pmdec, double radialVelocity, double gMagnitude, double teff, double logg,
                          double orbitalPeriod, double eccentricity, double inclination, 
                          double argumentOfPeriastron, double epochOfPeriastron) {
            this.commonName = commonName;
            this.scientificName = scientificName;
            this.ra = ra;
            this.dec = dec;
            this.distanceLy = distanceLy;
            this.pmra = pmra;
            this.pmdec = pmdec;
            this.radialVelocity = radialVelocity;
            this.gMagnitude = gMagnitude;
            this.teff = teff;
            this.logg = logg;
            this.orbitalPeriod = orbitalPeriod;
            this.eccentricity = eccentricity;
            this.inclination = inclination;
            this.argumentOfPeriastron = argumentOfPeriastron;
            this.epochOfPeriastron = epochOfPeriastron;
        }
        
        // Getters
        public String getCommonName() { return commonName; }
        public String getScientificName() { return scientificName; }
        public double getRa() { return ra; }
        public double getDec() { return dec; }
        public double getDistanceLy() { return distanceLy; }
        public double getPmra() { return pmra; }
        public double getPmdec() { return pmdec; }
        public double getRadialVelocity() { return radialVelocity; }
        public double getGMagnitude() { return gMagnitude; }
        public double getTeff() { return teff; }
        public double getLogg() { return logg; }
        public double getOrbitalPeriod() { return orbitalPeriod; }
        public double getEccentricity() { return eccentricity; }
        public double getInclination() { return inclination; }
        public double getArgumentOfPeriastron() { return argumentOfPeriastron; }
        public double getEpochOfPeriastron() { return epochOfPeriastron; }
        
        public boolean hasOrbitalMotion() { return orbitalPeriod > 0; }
        
        public double getParallax() { return 1000.0 / (distanceLy / 3.26156); } // Convert to mas
        public double getTotalProperMotion() { return Math.sqrt(pmra * pmra + pmdec * pmdec); }
    }
}

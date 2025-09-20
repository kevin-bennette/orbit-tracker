package com.gaiaorbittracker.orbittracker.dto;

/**
 * Input DTO for prediction requests.
 * Units: RA/Dec in degrees, proper motions in mas/yr, parallax in mas, rv in km/s
 */
public class StarInput {
    private String gaiaId;

    private Double ra;
    private Double dec;
    private Double pmra;
    private Double pmdec;
    private Double parallax;
    private Double radialVelocity;

    public StarInput() {}

    // getters & setters
    public String getGaiaId() { return gaiaId; }
    public void setGaiaId(String gaiaId) { this.gaiaId = gaiaId; }

    public Double getRa() { return ra; }
    public void setRa(Double ra) { this.ra = ra; }

    public Double getDec() { return dec; }
    public void setDec(Double dec) { this.dec = dec; }

    public Double getPmra() { return pmra; }
    public void setPmra(Double pmra) { this.pmra = pmra; }

    public Double getPmdec() { return pmdec; }
    public void setPmdec(Double pmdec) { this.pmdec = pmdec; }

    public Double getParallax() { return parallax; }
    public void setParallax(Double parallax) { this.parallax = parallax; }

    public Double getRadialVelocity() { return radialVelocity; }
    public void setRadialVelocity(Double radialVelocity) { this.radialVelocity = radialVelocity; }
}

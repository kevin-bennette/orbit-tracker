package com.gaiaorbittracker.orbittracker.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Resolver")
public class SimbadResponse {
    private double jradeg;
    private double jdedeg;

    @XmlElement
    public double getJradeg() {
        return jradeg;
    }

    public void setJradeg(double jradeg) {
        this.jradeg = jradeg;
    }

    @XmlElement
    public double getJdedeg() {
        return jdedeg;
    }

    public void setJdedeg(double jdedeg) {
        this.jdedeg = jdedeg;
    }
}

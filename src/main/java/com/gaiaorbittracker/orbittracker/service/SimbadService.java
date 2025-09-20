package com.gaiaorbittracker.orbittracker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SimbadService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String SIMBAD_URL = "http://simbad.u-strasbg.fr/simbad/sim-id";

    public String getStarByName(String name) {
        try {
            // Returns basic HTML, can parse specific info if needed
            String url = SIMBAD_URL + "?output.format=VOTable&Ident=" + name;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"Star not found\"}";
        }
    }
}

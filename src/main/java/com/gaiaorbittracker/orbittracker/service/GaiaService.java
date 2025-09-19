package com.gaiaorbittracker.orbittracker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GaiaService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GAIA_TAP_URL = "https://gea.esac.esa.int/tap-server/tap/sync";

    public String getStars(int limit) {
        String query = "SELECT TOP " + limit + " source_id, ra, dec, parallax, pmra, pmdec FROM gaiadr3.gaia_source";
        String requestUrl = GAIA_TAP_URL +
                "?REQUEST=doQuery&LANG=ADQL&FORMAT=json&QUERY=" + query.replace(" ", "+");

        return restTemplate.getForObject(requestUrl, String.class);
    }
}

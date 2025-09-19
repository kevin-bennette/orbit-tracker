package com.gaiaorbittracker.orbittracker.controller;

import com.gaiaorbittracker.orbittracker.service.GaiaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GaiaController {

    private final GaiaService gaiaService;

    public GaiaController(GaiaService gaiaService) {
        this.gaiaService = gaiaService;
    }

    @GetMapping("/stars")
    public String getStars(@RequestParam(defaultValue = "5") int limit) {
        return gaiaService.getStars(limit);
    }
}

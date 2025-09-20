package com.gaiaorbittracker.orbittracker.controller;

import com.gaiaorbittracker.orbittracker.service.SimbadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StarController {

    private final SimbadService simbadService;

    public StarController(SimbadService simbadService) {
        this.simbadService = simbadService;
    }

    @GetMapping("/star")
    public String getStar(@RequestParam String name) {
        return simbadService.getStarByName(name);
    }
}

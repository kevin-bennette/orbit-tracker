package com.gaiaorbittracker.orbittracker.repository;

import com.gaiaorbittracker.orbittracker.model.PredictionJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PredictionJobRepository extends JpaRepository<PredictionJob, UUID> {
}

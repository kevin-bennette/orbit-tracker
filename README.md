# Orbit Tracker

Orbit Tracker is a Spring Boot application that extracts stellar parameters from Gaia DR3 and SIMBAD, predicts stellar motion, and renders interactive charts in the browser. All computations run on the backend; the frontend is a lightweight HTML/JavaScript client using Chart.js.

## Features

- Gaia DR3 and SIMBAD integration for star lookup and metrics
- Orbital motion prediction with uncertainty bands (Monte Carlo)
- Interactive charts: position, velocity components, angular displacement, distance over time, galactic coordinates
- Single-input workflow (e.g., enter “Sirius”); photometry and metrics included in the results
- CSV export of prediction table

## Quick Start

Prerequisites
- Java 17+
- Maven 3.6+

Build and run
```bash
mvn clean install
mvn spring-boot:run
```

Open `http://localhost:8080` in your browser.

## Using the App

1) Enter a star name (e.g., Sirius, Vega, Alpha Centauri).
2) Set Time Period (years) and Resolution Steps.
3) Click Analyze Stellar Motion.
4) Review Star Information and the following charts:
   - Position over time (RA/Dec)
   - Velocity components (tangential, radial, total)
   - Angular displacement
   - Distance over time
   - Galactic coordinates
   - Uncertainty bands (computed even if Gaia uncertainties are missing using sensible defaults)
5) Export the predictions table as CSV if needed.

## API Endpoints

Star data
- `GET /api/star?name={starName}` – Raw SIMBAD VOTable data
- `GET /api/star/simbad?name={starName}` – Parsed SIMBAD data
- `GET /api/star/gaia?name={starName}` – Gaia DR3 metrics by name
- `GET /api/star/coordinates?ra={ra}&dec={dec}` – Gaia DR3 by coordinates

Prediction
- `POST /api/star/predict` – Orbital prediction for a single star (body: StarInput)

Optional (left available for programmatic use)
- `POST /api/predict` – Submit async prediction job
- `GET /api/status/{jobId}` – Poll job status
- `GET /api/predictions` – List jobs
- `GET /api/predictions/export` – Export jobs CSV
- `POST /api/predict/batch` – Submit multiple prediction jobs
- `POST /api/catalog/metrics` – Fetch metrics for a list of star names

Example
```bash
curl "http://localhost:8080/api/star/gaia?name=Vega"

curl -X POST "http://localhost:8080/api/star/predict" \
  -H "Content-Type: application/json" \
  -d '{
    "gaiaId": "Vega",
    "timePeriodYears": 100,
    "timeSteps": 50
  }'
```

## Technical Notes

Backend
- `GaiaService` queries Gaia DR3 TAP (ADQL) and returns metrics with uncertainties and photometry when available.
- `SimbadService` extracts coordinates from SIMBAD when needed.
- `OrbitalCalculator` computes motion and derives uncertainty bands via Monte Carlo (with default uncertainties if missing).

Frontend
- Static HTML/JS served from `src/main/resources/static` with Chart.js for visualization.
- Single page: `index.html`.

Configuration
```properties
# Thread pool for predictions
prediction.pool.size=4
```

## Troubleshooting

- Star not found: verify the name; known aliases are supported for many bright stars.
- Empty uncertainty bands: defaults are applied; if still empty, check browser console logs and backend responses.
- Charts not rendering: confirm the Chart.js CDN is reachable in your network.

## License

MIT License. See LICENSE for details.

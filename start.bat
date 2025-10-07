@echo off
echo Starting Orbit Tracker Application...
echo.
echo This application provides stellar motion prediction using Gaia DR3 and SIMBAD data.
echo.
echo If external APIs are unavailable, the application will use mock data for demonstration.
echo.
echo Available test endpoints:
echo - http://localhost:8080/test/sirius
echo - http://localhost:8080/test/vega
echo - http://localhost:8080/test/fallback
echo.
echo Main application: http://localhost:8080
echo.
echo Starting Spring Boot application...
echo.

mvn spring-boot:run

pause

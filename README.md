# 🌌 Orbit Tracker

A comprehensive stellar motion prediction application using Gaia DR3 and SIMBAD data sources. Track the orbital motion of stars over time with beautiful visualizations and detailed astrometric calculations.

## ✨ Features

- **Gaia DR3 Integration**: Query the European Space Agency's Gaia DR3 database for precise astrometric data
- **SIMBAD Integration**: Access the SIMBAD astronomical database for star identification and basic data
- **Orbital Mechanics**: Calculate stellar motion predictions using proper motion and radial velocity data
- **Interactive Charts**: Visualize position, velocity, and angular separation over time using Chart.js
- **Modern UI**: Beautiful, responsive web interface with glassmorphism design
- **RESTful API**: Complete API endpoints for programmatic access

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Internet connection (for Gaia DR3 and SIMBAD API access)

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd orbit-tracker
```

2. Build the application:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Open your browser and navigate to:
```
http://localhost:8080
```

## 📊 API Endpoints

### Star Data Endpoints

- `GET /api/star?name={starName}` - Get raw SIMBAD VOTable data
- `GET /api/star/simbad?name={starName}` - Get parsed SIMBAD data
- `GET /api/star/gaia?name={starName}` - Get Gaia DR3 data by star name
- `GET /api/star/coordinates?ra={ra}&dec={dec}` - Get Gaia DR3 data by coordinates

### Prediction Endpoints

- `POST /api/star/predict` - Calculate orbital motion prediction
- `POST /api/predict` - Submit prediction job (async)
- `GET /api/status/{jobId}` - Get prediction job status
- `GET /api/predictions` - List all prediction jobs

### Example API Usage

#### Get Star Data from Gaia DR3
```bash
curl "http://localhost:8080/api/star/gaia?name=Vega"
```

#### Predict Star Motion
```bash
curl -X POST "http://localhost:8080/api/star/predict" \
  -H "Content-Type: application/json" \
  -d '{
    "gaiaId": "Vega",
    "timePeriodYears": 100,
    "timeSteps": 50
  }'
```

## 🔬 Scientific Background

### Data Sources

- **Gaia DR3**: The third data release from the European Space Agency's Gaia mission, providing precise astrometric measurements for over 1.8 billion stars
- **SIMBAD**: The Set of Identifications, Measurements, and Bibliography for Astronomical Data

### Orbital Mechanics

The application uses a linear motion model for stellar motion prediction, which is appropriate for distant stars where gravitational effects are negligible. The calculations include:

- **Proper Motion**: Angular motion across the sky (mas/year)
- **Radial Velocity**: Motion along the line of sight (km/s)
- **Parallax**: Distance measurement (mas)
- **Tangential Velocity**: Physical velocity perpendicular to line of sight
- **Angular Separation**: Total displacement from initial position

### Coordinate Systems

- **RA/Dec**: Right Ascension and Declination in degrees
- **Proper Motion**: Milliarcseconds per year
- **Distance**: Light years (calculated from parallax)
- **Velocity**: Kilometers per second

## 🎨 User Interface

The web interface provides:

- **Star Search**: Enter star names (e.g., "Vega", "Sirius", "Alpha Centauri")
- **Time Controls**: Set prediction period (1-10,000 years) and resolution
- **Interactive Charts**: 
  - Position over time (RA/Dec)
  - Velocity components (tangential, radial, total)
  - Angular separation from initial position
- **Star Information**: Display key astrometric parameters

## 🛠️ Technical Architecture

### Backend (Spring Boot)

- **Controllers**: REST API endpoints
- **Services**: 
  - `GaiaService`: Gaia DR3 TAP query interface
  - `SimbadService`: SIMBAD VOTable parsing
  - `OrbitalCalculator`: Stellar motion calculations
  - `PredictionService`: Async job processing
- **DTOs**: Data transfer objects for API communication
- **Models**: JPA entities for job persistence

### Frontend (Vanilla JavaScript)

- **Chart.js**: Interactive data visualization
- **Modern CSS**: Glassmorphism design with responsive layout
- **Fetch API**: RESTful communication with backend

### Database

- **H2**: In-memory database for development
- **JPA**: Object-relational mapping for job persistence

## 📈 Example Results

For a typical star like Vega (α Lyrae):

- **Distance**: ~25 light years
- **Proper Motion**: ~200 mas/year
- **Prediction**: Over 100 years, Vega will move approximately 20 arcseconds across the sky
- **Velocity**: Tangential velocity of ~15 km/s

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server port
server.port=8080

# Database configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true

# Gaia API key (optional)
gaia.api.key=your-api-key-here
```

### Customization

- **Time Steps**: Adjust prediction resolution (10-200 steps)
- **Time Period**: Set prediction duration (1-10,000 years)
- **Chart Colors**: Modify CSS variables for different themes
- **API Rate Limits**: Configure timeout and retry policies

## 🐛 Troubleshooting

### Common Issues

1. **Star Not Found**: Ensure star names are spelled correctly and exist in Gaia DR3
2. **API Timeouts**: Check internet connection and Gaia DR3 service status
3. **Chart Not Displaying**: Verify Chart.js CDN is accessible
4. **Build Errors**: Ensure Java 17+ and Maven 3.6+ are installed

### Debug Mode

Enable debug logging:

```properties
logging.level.com.gaiaorbittracker=DEBUG
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **European Space Agency**: For providing the Gaia DR3 dataset
- **CDS Strasbourg**: For maintaining the SIMBAD database
- **Spring Boot Team**: For the excellent framework
- **Chart.js**: For beautiful data visualization

## 📚 References

- [Gaia DR3 Documentation](https://www.cosmos.esa.int/web/gaia/dr3)
- [SIMBAD Astronomical Database](http://simbad.u-strasbg.fr/simbad/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Chart.js Documentation](https://www.chartjs.org/docs/)

---

**Built with ❤️ for astronomy enthusiasts and researchers**

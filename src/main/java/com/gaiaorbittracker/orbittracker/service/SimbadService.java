package com.gaiaorbittracker.orbittracker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Object> getStarDataByName(String name) {
        try {
            String url = SIMBAD_URL + "?output.format=VOTable&Ident=" + name;
            String votableXml = restTemplate.getForObject(url, String.class);
            
            return parseVOTable(votableXml);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch SIMBAD data: " + e.getMessage());
            return error;
        }
    }

    private Map<String, Object> parseVOTable(String votableXml) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(votableXml.getBytes()));
            
            // Parse VOTable structure
            NodeList resources = doc.getElementsByTagName("RESOURCE");
            if (resources.getLength() > 0) {
                Element resource = (Element) resources.item(0);
                NodeList tables = resource.getElementsByTagName("TABLE");
                
                if (tables.getLength() > 0) {
                    Element table = (Element) tables.item(0);
                    NodeList data = table.getElementsByTagName("DATA");
                    
                    if (data.getLength() > 0) {
                        Element dataElement = (Element) data.item(0);
                        NodeList tabularData = dataElement.getElementsByTagName("TABLEDATA");
                        
                        if (tabularData.getLength() > 0) {
                            Element tabularDataElement = (Element) tabularData.item(0);
                            NodeList rows = tabularDataElement.getElementsByTagName("TR");
                            
                            if (rows.getLength() > 0) {
                                Element firstRow = (Element) rows.item(0);
                                NodeList cells = firstRow.getElementsByTagName("TD");
                                
                                // Extract basic information
                                if (cells.getLength() >= 4) {
                                    result.put("name", cells.item(0).getTextContent().trim());
                                    result.put("ra", parseCoordinate(cells.item(1).getTextContent().trim()));
                                    result.put("dec", parseCoordinate(cells.item(2).getTextContent().trim()));
                                    result.put("parallax", parseParallax(cells.item(3).getTextContent().trim()));
                                }
                            }
                        }
                    }
                }
            }
            
            if (result.isEmpty()) {
                result.put("error", "No data found in VOTable response");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", "Failed to parse VOTable: " + e.getMessage());
        }
        
        return result;
    }

    private Double parseCoordinate(String coordStr) {
        try {
            if (coordStr == null || coordStr.trim().isEmpty()) {
                return null;
            }
            
            // Handle different coordinate formats (HH:MM:SS.ss or decimal degrees)
            if (coordStr.contains(":")) {
                // Parse sexagesimal format
                String[] parts = coordStr.split(":");
                if (parts.length >= 2) {
                    double hours = Double.parseDouble(parts[0]);
                    double minutes = Double.parseDouble(parts[1]);
                    double seconds = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0;
                    
                    // Convert to decimal degrees
                    return hours * 15.0 + minutes * 0.25 + seconds * 0.004166666666666667;
                }
            } else {
                // Parse decimal format
                return Double.parseDouble(coordStr);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private Double parseParallax(String parallaxStr) {
        try {
            if (parallaxStr == null || parallaxStr.trim().isEmpty()) {
                return null;
            }
            
            // Remove any units and parse
            String cleanStr = parallaxStr.replaceAll("[^0-9.-]", "");
            return Double.parseDouble(cleanStr);
        } catch (Exception e) {
            return null;
        }
    }
}

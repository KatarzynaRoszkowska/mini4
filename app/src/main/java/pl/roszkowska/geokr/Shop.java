package pl.roszkowska.geokr;


public class Shop {
    private String name;
    private String description;
    private Double r;
    private Double lat;
    private Double lon;

    public Shop(String name, String description, Double r, Double lat, Double lon) {
        this.name = name;
        this.description = description;
        this.r = r;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getR() {
        return r;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setR(Double r) {
        this.r = r;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}


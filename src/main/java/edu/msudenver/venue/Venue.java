package edu.msudenver.venue;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.msudenver.city.City;
import edu.msudenver.country.Country;

import javax.persistence.*;

@Entity
@Table(name = "venue")
public class Venue {
    @Id
    @Column(name = "venue_id", columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long venueId;

    @Column(name= "name")
    private String name;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name= "type")
    private String type;

    @ManyToOne()
    @JoinColumn(name= "postal_code", referencedColumnName = "postal_code", insertable = false, updatable = false)
    private City city;

    @Column(name = "postal_code")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String postalCode;

    @Column(name = "country_code")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String countryCode;

    @Column(name= "active")
    private boolean active;

    private Venue(Long venueId, String name, String streetAddress, String type, City city, boolean active){
        this.venueId = venueId;
        this.name = name;
        this.streetAddress = streetAddress;
        this.type = type;
        this.city = city;
        this.active = active;
    }
    public Venue(){
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

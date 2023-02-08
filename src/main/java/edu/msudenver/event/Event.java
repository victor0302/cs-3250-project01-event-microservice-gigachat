package edu.msudenver.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.msudenver.venue.Venue;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "event_id", columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(name = "title")
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date starts;

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date ends;

    @OneToOne()
    @JoinColumn(name = "venue_id", referencedColumnName = "venue_id", insertable = false, updatable = false)
    private Venue venue;

    @Column(name = "venue_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long venueId;

    public Event(Long eventId, String title, Date starts, Date ends, Long venueId) {
        this.eventId = eventId;
        this.title = title;
        this.starts = starts;
        this.ends = ends;
        this.venueId = venueId;
    }

    public Event() {

    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStarts() {
        return starts;
    }

    public void setStarts(Date starts) {
        this.starts = starts;
    }

    public Date getEnds() {
        return ends;
    }

    public void setEnds(Date ends) {
        this.ends = ends;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }
}

package com.chihping.almostthere.bus.events;

public class EventSelected {

    private String eventId;

    public EventSelected(String eventId){
        this.eventId = eventId;

    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}

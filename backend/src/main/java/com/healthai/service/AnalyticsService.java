package com.healthai.service;

public interface AnalyticsService {

    /**
     * Report an event and increment its count
     * @param eventId the event ID
     * @param timestamp the event timestamp
     */
    void reportEvent(String eventId, Long timestamp);
}

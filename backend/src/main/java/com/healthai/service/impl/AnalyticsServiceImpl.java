package com.healthai.service.impl;

import com.healthai.model.EventCount;
import com.healthai.repository.EventCountRepository;
import com.healthai.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    private final EventCountRepository eventCountRepository;

    @Override
    @Transactional
    public void reportEvent(String eventId, Long timestamp) {
        logger.debug("Reporting event: {} at timestamp: {}", eventId, timestamp);
        
        // Find existing event count or create new one
        EventCount eventCount = eventCountRepository.findByEventId(eventId)
                .orElseGet(() -> {
                    EventCount newEventCount = new EventCount();
                    newEventCount.setEventId(eventId);
                    return newEventCount;
                });
        
        // Increment count
        eventCount.setCount(eventCount.getCount() + 1);
        
        // Save updated event count
        eventCountRepository.save(eventCount);
        
        logger.debug("Event count updated: {} = {}", eventId, eventCount.getCount());
    }
}

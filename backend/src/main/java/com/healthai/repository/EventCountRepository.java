package com.healthai.repository;

import com.healthai.model.EventCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventCountRepository extends JpaRepository<EventCount, Long> {

    Optional<EventCount> findByEventId(String eventId);
}

package com.healthai.controller;

import com.healthai.model.request.ReportEventRequest;
import com.healthai.model.response.ApiResponse;
import com.healthai.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping("/report")
    public ApiResponse<Void> reportEvent(@RequestBody ReportEventRequest request) {
        log.debug("Received event report: event_id={}, timestamp={}", 
                request.getEvent_id(), request.getTimestamp());
        
        // Validate required fields
        if (request.getEvent_id() == null || request.getEvent_id().isEmpty()) {
            return ApiResponse.error(10001, "无效请求参数");
        }
        
        // Report the event
        analyticsService.reportEvent(request.getEvent_id(), request.getTimestamp());
        
        return ApiResponse.success(null);
    }
}

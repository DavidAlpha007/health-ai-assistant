package com.healthai.model.request;

import lombok.Data;

@Data
public class ReportEventRequest {

    private String event_id;
    private Long timestamp;
}

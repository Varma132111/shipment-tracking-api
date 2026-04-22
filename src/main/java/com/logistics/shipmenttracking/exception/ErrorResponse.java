package com.logistics.shipmenttracking.exception;

import java.time.OffsetDateTime;
import java.util.Map;

public class ErrorResponse {
    private String code;
    private String message;
    private Map<String, String> details;
    private OffsetDateTime timestamp;

    public ErrorResponse(String code, String message, Map<String, String> details) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = OffsetDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}

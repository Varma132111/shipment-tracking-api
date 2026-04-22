package com.logistics.shipmenttracking.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class CreateWebhookRequest {
    @NotBlank
    @Pattern(regexp = "^(https?://).+", message = "targetUrl must start with http:// or https://")
    private String targetUrl;
    private String secret;

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

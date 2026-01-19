package com.cofecode.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseModel {
    private String message;
    private int code;
    private String timestamp;

    public ResponseModel(String message, int code) {
        this.message = message;
        this.code = code;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.timestamp = now.format(formatter);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

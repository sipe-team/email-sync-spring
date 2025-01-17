package com.sipe.mailSync.message;

public class MessageRequest {

    public String message;
    public String from;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }
}

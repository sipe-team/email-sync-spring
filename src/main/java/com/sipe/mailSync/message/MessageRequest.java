package com.sipe.mailSync.message;

import lombok.Data;

@Data
public class MessageRequest {
    public String email;
    public String message;
}

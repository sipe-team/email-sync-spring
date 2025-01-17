package com.sipe.mailSync.message;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/message")
@RestController
public class MessageController {

    @PostMapping
    public void message(@RequestBody MessageRequest request) {
        System.out.println(request);
    }
}

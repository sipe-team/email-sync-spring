package com.sipe.mailSync.message;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/message")
@RestController
@RequiredArgsConstructor
public class MessageController {
    private final SummaryService summaryService;

    @PostMapping
    public void message(@RequestBody MessageRequest request) {
        String content = summaryService.getSummary(request.getMessage());
        System.out.println(content);

        //TODO KAKAO API 호출
    }
}

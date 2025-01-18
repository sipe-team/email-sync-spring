package com.sipe.mailSync.oauth2;

import lombok.Data;

import java.util.List;

@Data
public class WatchRequest {
    private String topicName;
    private List<String> labelIds;
    private String labelFilterBehavior;

    public WatchRequest(final String topicName, final List<String> labelIds, final String labelFilterBehavior) {
        this.topicName = topicName;
        this.labelIds = labelIds;
        this.labelFilterBehavior = labelFilterBehavior;
    }

}

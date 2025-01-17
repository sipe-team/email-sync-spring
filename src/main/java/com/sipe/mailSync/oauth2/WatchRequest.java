package com.sipe.mailSync.oauth2;

import java.util.List;

public class WatchRequest {
    private String topicName;
    private List<String> labelIds;
    private String labelFilterBehavior;

    public WatchRequest(final String topicName, final List<String> labelIds, final String labelFilterBehavior) {
        this.topicName = topicName;
        this.labelIds = labelIds;
        this.labelFilterBehavior = labelFilterBehavior;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(final String topicName) {
        this.topicName = topicName;
    }

    public List<String> getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(final List<String> labelIds) {
        this.labelIds = labelIds;
    }

    public String getLabelFilterBehavior() {
        return labelFilterBehavior;
    }

    public void setLabelFilterBehavior(final String labelFilterBehavior) {
        this.labelFilterBehavior = labelFilterBehavior;
    }
}

package com.navercorp.pinpoint.common.server.bo.stat;

import com.navercorp.pinpoint.common.util.Assert;

import java.util.Objects;

public class RequestsStatSummaryBo implements AgentStatDataPoint  {

    private String agentId;
    private long startTimestamp;
    private long timestamp;

    private final String url;
    private final int status;
    private final int count;
    private final double avgTime;
    private final long maxTime;

    public RequestsStatSummaryBo(String url, int status, int count, double avgTime, long maxTime) {
        this.url = Objects.requireNonNull(url, "url");
        this.status = status;

        Assert.isTrue(count >= 0, "'count' >= 0");
        this.count = count;

        Assert.isTrue(avgTime >= 0, "'avgTime' >= 0");
        this.avgTime = avgTime;

        Assert.isTrue(maxTime >= 0, "'maxTime' >= 0");
        this.maxTime = maxTime;
    }

    public String getUrl() {
        return url;
    }

    public int getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public long getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public AgentStatType getAgentStatType() {
        return AgentStatType.REQUESTS_SUMMARY;
    }


    @Override
    public String toString() {
        return "RequestsStatSummaryBo{" +
            "agentId='" + agentId + '\'' +
            ", startTimestamp=" + startTimestamp +
            ", timestamp=" + timestamp +
            ", url='" + url + '\'' +
            ", status=" + status +
            ", count=" + count +
            ", avgTime=" + avgTime +
            ", maxTime=" + maxTime +
            '}';
    }
}

package com.navercorp.pinpoint.common.server.bo.metric;

import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;

import java.util.Objects;

public abstract class CustomMetricBo<T extends Number> implements AgentStatDataPoint {

    private String agentId;
    private long startTimestamp;
    private long timestamp;

    private AgentStatType agentStatType;

    private T value;

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

    public void setAgentStatType(AgentStatType agentStatType) {
        this.agentStatType = agentStatType;
    }

    @Override
    public AgentStatType getAgentStatType() {
        return agentStatType;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomMetricBo<?> that = (CustomMetricBo<?>) o;
        return startTimestamp == that.startTimestamp &&
                timestamp == that.timestamp &&
                Objects.equals(agentId, that.agentId) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId, startTimestamp, timestamp, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CustomMetricBo{");
        sb.append("agentId='").append(agentId).append('\'');
        sb.append(", startTimestamp=").append(startTimestamp);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }

}

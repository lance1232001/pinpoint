package com.navercorp.pinpoint.common.server.bo.metric;

import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;

import java.util.Objects;

public abstract class CustomMetricBo<T extends Number> implements AgentStatDataPoint {

    private String agentId;
    private long startTimestamp;
    private long timestamp;

    private String name;
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

    @Override
    public AgentStatType getAgentStatType() {
        return AgentStatType.DATASOURCE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
            Objects.equals(name, that.name) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId, startTimestamp, timestamp, name, value);
    }

    @Override
    public String toString() {
        return "CustomMetricBo{" +
            "agentId='" + agentId + '\'' +
            ", startTimestamp=" + startTimestamp +
            ", timestamp=" + timestamp +
            ", name='" + name + '\'' +
            ", value=" + value +
            '}';
    }

}

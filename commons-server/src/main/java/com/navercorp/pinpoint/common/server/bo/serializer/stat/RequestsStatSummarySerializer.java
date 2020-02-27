package com.navercorp.pinpoint.common.server.bo.serializer.stat;

import com.navercorp.pinpoint.common.server.bo.codec.stat.FileDescriptorEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.RequestsStatSummaryEncoder;
import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestsStatSummarySerializer extends AgentStatSerializer<RequestsStatSummaryBo> {

    @Autowired
    public RequestsStatSummarySerializer(RequestsStatSummaryEncoder encoder) {
        super(encoder);
    }

}

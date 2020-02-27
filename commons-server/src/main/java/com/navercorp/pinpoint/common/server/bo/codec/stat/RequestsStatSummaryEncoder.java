package com.navercorp.pinpoint.common.server.bo.codec.stat;

import com.navercorp.pinpoint.common.server.bo.stat.FileDescriptorBo;
import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RequestsStatSummaryEncoder extends AgentStatEncoder<RequestsStatSummaryBo> {

    @Autowired
    private RequestsStatSummaryEncoder(@Qualifier("requestsStatSummaryCodecV2") AgentStatCodec<RequestsStatSummaryBo> codec) {
        super(codec);
    }

}

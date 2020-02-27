package com.navercorp.pinpoint.common.server.bo.codec.stat;

import com.navercorp.pinpoint.common.server.bo.stat.JvmGcDetailedBo;
import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestsStatSummaryDecoder extends AgentStatDecoder<RequestsStatSummaryBo> {

    @Autowired
    private RequestsStatSummaryDecoder(List<AgentStatCodec<RequestsStatSummaryBo>> codecs) {
        super(codecs);
    }

}


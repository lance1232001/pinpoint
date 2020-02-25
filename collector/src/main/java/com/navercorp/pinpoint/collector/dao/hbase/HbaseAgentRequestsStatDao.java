/*
 * Copyright 2020 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.collector.dao.hbase;

import com.navercorp.pinpoint.collector.dao.AgentRequestsStatDao;
import com.navercorp.pinpoint.common.buffer.AutomaticBuffer;
import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.hbase.HbaseColumnFamily;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.CodecFactory;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedLongEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.stat.v2.JvmGcCodecV2;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatDecodingContext;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatRowKeyComponent;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatRowKeyEncoder;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatUtils;
import com.navercorp.pinpoint.common.server.bo.stat.AgentRequestsStatBo;
import com.navercorp.pinpoint.common.server.bo.stat.AgentRequestsStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.common.server.bo.stat.JvmGcBo;
import com.navercorp.pinpoint.common.server.bo.stat.StartAndElapsedTime;

import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Put;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author Taejin Koo
 */
@Repository
public class HbaseAgentRequestsStatDao implements AgentRequestsStatDao {


//    @Autowired
//    private HbaseOperations2 hbaseTemplate;
//
//    @Autowired
//    private TableNameProvider tableNameProvider;
//
//    @Autowired
//    private AgentStatHbaseOperationFactory agentStatHbaseOperationFactory;
//
//    @Autowired
//    private JvmGcSerializer jvmGcSerializer;
//
//    @Override
//    public void insert(String agentId, List<JvmGcBo> jvmGcBos) {
//        if (agentId == null) {
//            throw new NullPointerException("agentId");
//        }
//        if (CollectionUtils.isEmpty(jvmGcBos)) {
//            return;
//        }
//        List<Put> jvmGcBoPuts = this.agentStatHbaseOperationFactory.createPuts(agentId, AgentStatType.JVM_GC, jvmGcBos, this.jvmGcSerializer);
//        if (!jvmGcBoPuts.isEmpty()) {
//            TableName agentStatTableName = tableNameProvider.getTableName(HbaseTable.AGENT_STAT_VER2);
//            List<Put> rejectedPuts = this.hbaseTemplate.asyncPut(agentStatTableName, jvmGcBoPuts);
//            if (CollectionUtils.isNotEmpty(rejectedPuts)) {
//                this.hbaseTemplate.put(agentStatTableName, rejectedPuts);
//            }
//        }
//    }


    private static final byte VERSION = 2;

    @Autowired
    private HbaseOperations2 hbaseTemplate;

    @Autowired
    @Qualifier("applicationTraceIndexDistributor")
    private AbstractRowKeyDistributor rowKeyDistributor;

    @Autowired
    private AgentStatRowKeyEncoder agentStatRowKeyEncoder;

    @Autowired
    private TableNameProvider tableNameProvider;

    @Override
    public void insert(AgentRequestsStatBo agentRequestsStatBo) {
        String agentId = agentRequestsStatBo.getAgentId();


        List<Put> putList = createPutList(agentRequestsStatBo);


        System.out.println("com.navercorp.pinpoint.collector.dao.hbase.HbaseAgentRequestsStatDao.insert : " + agentRequestsStatBo);
    }


//    protected AgentStatSerializer(AgentStatEncoder<T> encoder) {
//        this.encoder = Objects.requireNonNull(encoder, "encoder");
//    }
//
//    @Override
//    public void serialize(List<T> agentStatBos, Put put, SerializationContext context) {
//        if (CollectionUtils.isEmpty(agentStatBos)) {
//            throw new IllegalArgumentException("agentStatBos should not be empty");
//        }
//        long initialTimestamp = agentStatBos.get(0).getTimestamp();
//        long baseTimestamp = AgentStatUtils.getBaseTimestamp(initialTimestamp);
//        long timestampDelta = initialTimestamp - baseTimestamp;
//        ByteBuffer qualifierBuffer = this.encoder.encodeQualifier(timestampDelta);
//        ByteBuffer valueBuffer = this.encoder.encodeValue(agentStatBos);
//        put.addColumn(HbaseColumnFamily.AGENT_STAT_STATISTICS.getName(), qualifierBuffer, HConstants.LATEST_TIMESTAMP, valueBuffer);
//    }


    private List<Put> createPutList(AgentRequestsStatBo agentRequestsStatBo) {
        Map<Long, AgentRequestsStatBo> slot = slot(agentRequestsStatBo);
        List<Put> puts = new ArrayList<Put>();

        System.out.println("SLLLLLLLOT============== " + slot);

        for (Map.Entry<Long, AgentRequestsStatBo> longAgentRequestsStatBoEntry : slot.entrySet()) {
            final AgentStatRowKeyComponent rowKeyComponent = new AgentStatRowKeyComponent(agentRequestsStatBo.getAgentId(), AgentStatType.REQUESTS, agentRequestsStatBo.getTimestamp());
            byte[] rowKey = this.agentStatRowKeyEncoder.encodeRowKey(rowKeyComponent);
            byte[] distributedRowKey = this.rowKeyDistributor.getDistributedKey(rowKey);

            Put put = new Put(distributedRowKey);

            AgentRequestsStatBo agentRequestsStatBo3 = longAgentRequestsStatBoEntry.getValue();
            long fastestEndTimeStamp = agentRequestsStatBo3.getTimestamp();
            long baseTimestamp = AgentStatUtils.getBaseTimestamp(fastestEndTimeStamp);
            long timestampDelta = fastestEndTimeStamp - baseTimestamp;

            ByteBuffer qualifier = encodeQualifier(timestampDelta);
            ByteBuffer value = encodeValue(agentRequestsStatBo3);


//            ByteBuffer valueBuffer = this.encoder.encodeValue(agentStatBos);
//            put.addColumn(HbaseColumnFamily.AGENT_STAT_STATISTICS.getName(), qualifierBuffer, HConstants.LATEST_TIMESTAMP, valueBuffer);



//            public ByteBuffer encodeValue(List<T> agentStatDataPoints) {
//                Buffer valueBuffer = new AutomaticBuffer();
//                valueBuffer.putByte(this.codec.getVersion());
//                codec.encodeValues(valueBuffer, agentStatDataPoints);
//                return valueBuffer.wrapByteBuffer();
//            }

//            agentStatSerializer.serialize(slottedAgentStatDataPoints, put, null);


//            longAgentRequestsStatBoEntry.


        }

        //        for (Map.Entry<Long, List<T>> timeslot : timeslots.entrySet()) {
//            long baseTimestamp = timeslot.getKey();
//            List<T> slottedAgentStatDataPoints = timeslot.getValue();
//
//            final AgentStatRowKeyComponent rowKeyComponent = new AgentStatRowKeyComponent(agentId, agentStatType, baseTimestamp);
//            byte[] rowKey = this.rowKeyEncoder.encodeRowKey(rowKeyComponent);
//            byte[] distributedRowKey = this.rowKeyDistributor.getDistributedKey(rowKey);
//
//            Put put = new Put(distributedRowKey);
//            agentStatSerializer.serialize(slottedAgentStatDataPoints, put, null);
//            puts.add(put);
//        }
//        return puts;


        return null;


    }

    public ByteBuffer encodeQualifier(long timestampDelta) {
        // Variable-length encoding of 5 minutes (300000 ms) takes up max 3 bytes
        Buffer qualifierBuffer = new AutomaticBuffer(3);
        qualifierBuffer.putVLong(timestampDelta);
        return qualifierBuffer.wrapByteBuffer();
    }

    public ByteBuffer encodeValue(AgentRequestsStatBo agentRequestsStatBo) {
        Buffer valueBuffer = new AutomaticBuffer();
        valueBuffer.putByte(VERSION);
        encodeValue0(valueBuffer, agentRequestsStatBo);
        return valueBuffer.wrapByteBuffer();
    }

    private void encodeValue0(Buffer valueBuffer, AgentRequestsStatBo agentRequestsStatBo) {




//        if (CollectionUtils.isEmpty(jvmGcBos)) {
//            throw new IllegalArgumentException("jvmGcBos must not be empty");
//        }
//        final int gcTypeCode = jvmGcBos.get(0).getGcType().getTypeCode();
//        valueBuffer.putVInt(gcTypeCode);
//        final int numValues = jvmGcBos.size();
//        valueBuffer.putVInt(numValues);
//
//        List<Long> startTimestamps = new ArrayList<>(numValues);
//        List<Long> timestamps = new ArrayList<>(numValues);
//        JvmGcCodecV2.JvmGcCodecEncoder jvmGcCodecEncoder = new JvmGcCodecV2.JvmGcCodecEncoder(codec);
//        for (JvmGcBo jvmGcBo : jvmGcBos) {
//            startTimestamps.add(jvmGcBo.getStartTimestamp());
//            timestamps.add(jvmGcBo.getTimestamp());
//            jvmGcCodecEncoder.addValue(jvmGcBo);
//        }
//
//        this.codec.encodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, startTimestamps);
//        this.codec.encodeTimestamps(valueBuffer, timestamps);
//        jvmGcCodecEncoder.encode(valueBuffer);



    }


    private Map<Long, AgentRequestsStatBo> slot(com.navercorp.pinpoint.common.server.bo.stat.AgentRequestsStatBo agentRequestsStatBo) {
        Map<Long, AgentRequestsStatBo> timeslots = new TreeMap<Long, AgentRequestsStatBo>();

        Collection<String> urlList = agentRequestsStatBo.getUrlList();
        for (String url : urlList) {
            AgentRequestsStatDataPoint agentRequestsStatDataPointList = agentRequestsStatBo.getAgentRequestsStatDataPointList(url);

            Collection<Integer> statuses = agentRequestsStatDataPointList.getStatuses();
            for (Integer status : statuses) {
                Collection<StartAndElapsedTime> requestList = agentRequestsStatDataPointList.getRequestList(status);

                for (StartAndElapsedTime startAndElapsedTime : requestList) {
                    long endTime = startAndElapsedTime.getStartTime() + startAndElapsedTime.getElapsedTime();
                    long timeslot = AgentStatUtils.getBaseTimestamp(endTime);

                    AgentRequestsStatBo agentRequestsStatBo1 = timeslots.get(timeslot);
                    if (agentRequestsStatBo1 == null) {
                        agentRequestsStatBo1 = new AgentRequestsStatBo();
                        agentRequestsStatBo1.setAgentId(agentRequestsStatBo.getAgentId());
                        agentRequestsStatBo1.setStartTimestamp(agentRequestsStatBo.getStartTimestamp());
                        agentRequestsStatBo1.setTimestamp(Long.MAX_VALUE);
                        timeslots.put(timeslot, agentRequestsStatBo1);
                    }

                    agentRequestsStatBo1.put(url, status, startAndElapsedTime.getStartTime(), startAndElapsedTime.getElapsedTime());

                    long endTime2 = startAndElapsedTime.getStartTime() + startAndElapsedTime.getElapsedTime();
                    long timestamp = agentRequestsStatBo1.getTimestamp();
                    if (timestamp > endTime2) {
                        agentRequestsStatBo1.setTimestamp(endTime2);

                    }
                }

            }

        }

        return timeslots;
    }




//    private final CodecFactory<T> codecFactory;
//
//    public AgentStatCodecV2(final CodecFactory<T> codecFactory) {
//        this.codecFactory = Objects.requireNonNull(codecFactory, "codecFactory");
//    }
//
//    @Override
//    public byte getVersion() {
//        return VERSION;
//    }
//
//    @Override
//    public void encodeValues(Buffer valueBuffer, List<T> statDataPointList) {
//        Assert.isTrue(!CollectionUtils.isEmpty(statDataPointList), "statDataPointList must not be empty");
//
//        final int numValues = statDataPointList.size();
//        valueBuffer.putVInt(numValues);
//
//        List<Long> startTimestamps = new ArrayList<Long>(numValues);
//        List<Long> timestamps = new ArrayList<Long>(numValues);
//
//        AgentStatCodec.CodecEncoder<T> encoder = codecFactory.createCodecEncoder();
//        for (T statDataPoint : statDataPointList) {
//            startTimestamps.add(statDataPoint.getStartTimestamp());
//            timestamps.add(statDataPoint.getTimestamp());
//            encoder.addValue(statDataPoint);
//        }
//
//        final AgentStatDataPointCodec codec = codecFactory.getCodec();
//        codec.encodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, startTimestamps);
//        codec.encodeTimestamps(valueBuffer, timestamps);
//        encoder.encode(valueBuffer);
//    }
//
//    @Override
//    public List<T> decodeValues(Buffer valueBuffer, AgentStatDecodingContext decodingContext) {
//        final String agentId = decodingContext.getAgentId();
//        final long baseTimestamp = decodingContext.getBaseTimestamp();
//        final long timestampDelta = decodingContext.getTimestampDelta();
//        final long initialTimestamp = baseTimestamp + timestampDelta;
//
//        int numValues = valueBuffer.readVInt();
//        final AgentStatDataPointCodec codec = codecFactory.getCodec();
//        List<Long> startTimestamps = codec.decodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, numValues);
//        List<Long> timestamps = codec.decodeTimestamps(initialTimestamp, valueBuffer, numValues);
//
//        AgentStatCodec.CodecDecoder<T> codecDecoder = codecFactory.createCodecDecoder();
//
//        // decode headers
//        final byte[] header = valueBuffer.readPrefixedBytes();
//        AgentStatHeaderDecoder headerDecoder = new BitCountingHeaderDecoder(header);
//
//        codecDecoder.decode(valueBuffer, headerDecoder, numValues);
//
//        List<T> result = new ArrayList<T>(numValues);
//        for (int i = 0; i < numValues; i++) {
//            T newObject = codecDecoder.getValue(i);
//            newObject.setAgentId(agentId);
//            newObject.setStartTimestamp(startTimestamps.get(i));
//            newObject.setTimestamp(timestamps.get(i));
//            result.add(newObject);
//        }
//
//        return result;
//    }


}

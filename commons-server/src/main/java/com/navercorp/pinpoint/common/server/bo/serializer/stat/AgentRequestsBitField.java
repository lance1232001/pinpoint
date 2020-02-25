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

package com.navercorp.pinpoint.common.server.bo.serializer.stat;

import com.navercorp.pinpoint.common.profiler.encoding.BitFieldUtils;
import com.navercorp.pinpoint.common.server.bo.SpanBo;
import com.navercorp.pinpoint.common.server.bo.SpanEventBo;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.bitfield.DepthEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.bitfield.SequenceEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.bitfield.ServiceTypeEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.bitfield.SpanBitFiled;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.bitfield.SpanEventBitField;
import com.navercorp.pinpoint.common.server.bo.serializer.trace.v2.bitfield.StartElapsedTimeEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.stat.AgentRequestsStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.StartAndElapsedTime;
import com.navercorp.pinpoint.common.trace.LoggingInfo;
import com.navercorp.pinpoint.common.util.Assert;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author Taejin Koo
 */
public class AgentRequestsBitField {

    // 1bit
    public static final int SET_URL = 0;
    public static final int SET_STATUS = 1;

    private byte bitField = 0;

    private byte urlNotSetbitFiled = (byte) BitFieldUtils.setBit(0, SET_URL, true);




//    public static SpanEventBitField build(SpanEventBo spanEventBo, SpanEventBo prevSpanEventBo) {
//        if (spanEventBo == null) {
//            throw new NullPointerException("spanEventBo");
//        }
//        if (prevSpanEventBo == null) {
//            throw new NullPointerException("prevSpanEventBo");
//        }
//
//        final SpanEventBitField bitFiled = buildFirst(spanEventBo);
//
//        if (spanEventBo.getStartElapsed() == prevSpanEventBo.getStartElapsed()) {
//            bitFiled.setStartElapsedEncodingStrategy(StartElapsedTimeEncodingStrategy.PREV_EQUALS);
//        } else {
//            bitFiled.setStartElapsedEncodingStrategy(StartElapsedTimeEncodingStrategy.PREV_DELTA);
//        }
//
//        // sequence prev: 5 current: 6 = 6 - 5= delta 1
//        final short sequenceDelta = (short) (spanEventBo.getSequence() - prevSpanEventBo.getSequence());
//        if (sequenceDelta == 1) {
//            bitFiled.setSequenceEncodingStrategy(SequenceEncodingStrategy.PREV_ADD1);
//        } else {
//            bitFiled.setSequenceEncodingStrategy(SequenceEncodingStrategy.PREV_DELTA);
//        }
//
//        if (spanEventBo.getDepth() == prevSpanEventBo.getDepth()) {
//            bitFiled.setDepthEncodingStrategy(DepthEncodingStrategy.PREV_EQUALS);
//        } else {
//            bitFiled.setDepthEncodingStrategy(DepthEncodingStrategy.RAW);
//        }
//
//
//        if (prevSpanEventBo.getServiceType() == spanEventBo.getServiceType()) {
//            bitFiled.setServiceTypeEncodingStrategy(ServiceTypeEncodingStrategy.PREV_EQUALS);
//        } else {
//            bitFiled.setServiceTypeEncodingStrategy(ServiceTypeEncodingStrategy.RAW);
//        }
//
//
//        return bitFiled;
//    }

    public static AgentRequestsBitField build(int status, StartAndElapsedTime time) {
        Assert.requireNonNull(time, "time");


    }


    public static AgentRequestsBitField build(StartAndElapsedTime time) {
        Assert.requireNonNull(time, "time");
    }

    public static AgentRequestsBitField build(Agent spanBo) {
        if (spanBo == null) {
            throw new NullPointerException("spanBo");
        }
        final SpanBitFiled spanBitFiled = new SpanBitFiled();


        if (spanBo.getServiceType() == spanBo.getApplicationServiceType()) {
            spanBitFiled.setApplicationServiceTypeEncodingStrategy(ServiceTypeEncodingStrategy.PREV_EQUALS);
        } else {
            spanBitFiled.setApplicationServiceTypeEncodingStrategy(ServiceTypeEncodingStrategy.RAW);
        }

        if (spanBo.getParentSpanId() == ROOT_PARENT_SPAN_ID) {
            spanBitFiled.setRoot(true);
        }
        if (spanBo.getErrCode() != 0) {
            spanBitFiled.setErrorCode(true);
        }

        if (spanBo.hasException()) {
            spanBitFiled.setHasException(true);
        }

        if (spanBo.getFlag() != 0) {
            spanBitFiled.setFlag(true);
        }

        if (spanBo.getLoggingTransactionInfo() != LoggingInfo.NOT_LOGGED.getCode()) {
            spanBitFiled.setLoggingTransactionInfo(true);
        }
        if (CollectionUtils.isNotEmpty(spanBo.getAnnotationBoList())) {
            spanBitFiled.setAnnotation(true);
        }

        return spanBitFiled;
    }

    public SpanBitFiled() {
    }

    public SpanBitFiled(byte bitField) {
        this.bitField = bitField;
    }

    public byte getBitField() {
        return bitField;
    }

    // for test
    void maskAll() {
        bitField = -1;
    }

    private void setBit(int position, boolean value) {
        this.bitField = BitFieldUtils.setBit(bitField, position, value);
    }

    private boolean testBit(int position) {
        return BitFieldUtils.testBit(bitField, position);
    }

    private int getBit(int position) {
        return BitFieldUtils.getBit(bitField, position);
    }

    public ServiceTypeEncodingStrategy getApplicationServiceTypeEncodingStrategy() {
        final int set = getBit(SET_APPLICATION_SERVICE_TYPE_ENCODING_STRATEGY);
        switch (set) {
            case 0:
                return ServiceTypeEncodingStrategy.PREV_EQUALS;
            case 1:
                return ServiceTypeEncodingStrategy.RAW;
            default:
                throw new IllegalArgumentException("SET_APPLICATION_SERVICE_TYPE_ENCODING_STRATEGY");
        }
    }

    void setApplicationServiceTypeEncodingStrategy(ServiceTypeEncodingStrategy strategy) {
        switch (strategy) {
            case PREV_EQUALS:
                setBit(SET_APPLICATION_SERVICE_TYPE_ENCODING_STRATEGY, false);
                break;
            case RAW:
                setBit(SET_APPLICATION_SERVICE_TYPE_ENCODING_STRATEGY, true);
                break;
            default:
                throw new IllegalArgumentException("SET_APPLICATION_SERVICE_TYPE_ENCODING_STRATEGY");
        }

    }


    public boolean isRoot() {
        return testBit(SET_ROOT);
    }

    // for test
    void setRoot(boolean root) {
        setBit(SET_ROOT, root);
    }


    public boolean isSetErrorCode() {
        return testBit(SET_ERROR_CODE);
    }


    // for test
    void setErrorCode(boolean errorCode) {
        setBit(SET_ERROR_CODE, errorCode);
    }

    public boolean isSetHasException() {
        return testBit(SET_HAS_EXCEPTION);
    }

    // for test
    void setHasException(boolean hasException) {
        setBit(SET_HAS_EXCEPTION, hasException);
    }


    public boolean isSetFlag() {
        return testBit(SET_FLAG);
    }

    // for test
    void setFlag(boolean flag) {
        setBit(SET_FLAG, flag);
    }


    public boolean isSetLoggingTransactionInfo() {
        return testBit(SET_LOGGING_TRANSACTION_INFO);
    }

    // for test
    void setLoggingTransactionInfo(boolean loggingTransactionInfo) {
        setBit(SET_LOGGING_TRANSACTION_INFO, loggingTransactionInfo);
    }

    public boolean isSetAnnotation() {
        return testBit(SET_ANNOTATION);
    }


    public void setAnnotation(boolean annotation) {
        setBit(SET_ANNOTATION, annotation);
    }



}

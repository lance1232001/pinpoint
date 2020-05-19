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

package com.navercorp.pinpoint.profiler.context.monitor.metric;

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.CustomMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.DoubleGaugeMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntCountMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntGaugeMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongCountMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongGaugeMetric;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Random;

/**
 * @author Taejin Koo
 */
public class CustomMetricRegistryServiceTest {

    private static final String DEFAULT_TEST_METRIC_NAME = "groupName/metricName/label";

    private final Random random = new Random(System.currentTimeMillis());

    @Test
    public void intCountMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        int value = random.nextInt(100);

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, DEFAULT_TEST_METRIC_NAME, value, IntCountMetric.class);
        Assert.assertNotNull(customMetric);

        Map<String, CustomMetricWrapper> customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(1, customMetricMap.size());

        CustomMetricWrapper customMetricWrapper = customMetricMap.get(DEFAULT_TEST_METRIC_NAME);
        Assert.assertTrue(customMetricWrapper instanceof IntCountMetricWrapper);

        IntCountMetricWrapper intCountMetricWrapper = (IntCountMetricWrapper) customMetricWrapper;
        Assert.assertEquals(value, intCountMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((IntCountMetric) customMetric);
        Assert.assertTrue(unregister);

        customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(0, customMetricMap.size());
    }

    @Test
    public void longCountMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        long value = random.nextLong();

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, DEFAULT_TEST_METRIC_NAME, value, LongCountMetric.class);
        Assert.assertNotNull(customMetric);

        Map<String, CustomMetricWrapper> customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(1, customMetricMap.size());

        CustomMetricWrapper customMetricWrapper = customMetricMap.get(DEFAULT_TEST_METRIC_NAME);
        Assert.assertTrue(customMetricWrapper instanceof LongCountMetricWrapper);

        LongCountMetricWrapper longCountMetricWrapper = (LongCountMetricWrapper) customMetricWrapper;
        Assert.assertEquals(value, longCountMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((LongCountMetric) customMetric);
        Assert.assertTrue(unregister);

        customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(0, customMetricMap.size());
    }


    @Test
    public void intGaugeMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        int value = random.nextInt(100);

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, DEFAULT_TEST_METRIC_NAME, value, IntGaugeMetric.class);
        Assert.assertNotNull(customMetric);

        Map<String, CustomMetricWrapper> customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(1, customMetricMap.size());

        CustomMetricWrapper customMetricWrapper = customMetricMap.get(DEFAULT_TEST_METRIC_NAME);
        Assert.assertTrue(customMetricWrapper instanceof IntGaugeMetricWrapper);

        IntGaugeMetricWrapper intGaugeMetricWrapper = (IntGaugeMetricWrapper) customMetricWrapper;
        Assert.assertEquals(value, intGaugeMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((IntGaugeMetric) customMetric);
        Assert.assertTrue(unregister);

        customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(0, customMetricMap.size());
    }


    @Test
    public void longGaugeMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        long value = random.nextLong();

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, DEFAULT_TEST_METRIC_NAME, value, LongGaugeMetric.class);
        Assert.assertNotNull(customMetric);

        Map<String, CustomMetricWrapper> customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(1, customMetricMap.size());

        CustomMetricWrapper customMetricWrapper = customMetricMap.get(DEFAULT_TEST_METRIC_NAME);
        Assert.assertTrue(customMetricWrapper instanceof LongGaugeMetricWrapper);

        LongGaugeMetricWrapper longGaugeMetricWrapper = (LongGaugeMetricWrapper) customMetricWrapper;
        Assert.assertEquals(value, longGaugeMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((LongGaugeMetric) customMetric);
        Assert.assertTrue(unregister);

        customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(0, customMetricMap.size());
    }

    @Test
    public void doubleGaugeMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        double value = random.nextDouble();

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, DEFAULT_TEST_METRIC_NAME, value, DoubleGaugeMetric.class);
        Assert.assertNotNull(customMetric);

        Map<String, CustomMetricWrapper> customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(1, customMetricMap.size());

        CustomMetricWrapper customMetricWrapper = customMetricMap.get(DEFAULT_TEST_METRIC_NAME);
        Assert.assertTrue(customMetricWrapper instanceof DoubleGaugeMetricWrapper);

        DoubleGaugeMetricWrapper doubleGaugeMetricWrapper = (DoubleGaugeMetricWrapper) customMetricWrapper;
        Assert.assertTrue(value == doubleGaugeMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((DoubleGaugeMetric) customMetric);
        Assert.assertTrue(unregister);

        customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(0, customMetricMap.size());
    }

    @Test
    public void mixedCustomMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        int intValue = random.nextInt(100);
        final String intMetricName = "groupName/metricName/int";
        CustomMetric intCountMetric = createAndAddFixedValueMetric(customMetricRegistryService, intMetricName, intValue, IntCountMetric.class);
        Assert.assertNotNull(intCountMetric);

        long longValue = random.nextLong();
        final String longMetricName = "groupName/metricName/long";
        CustomMetric longGaugeMetric = createAndAddFixedValueMetric(customMetricRegistryService, longMetricName, longValue, LongGaugeMetric.class);
        Assert.assertNotNull(longGaugeMetric);

        Map<String, CustomMetricWrapper> customMetricMap = customMetricRegistryService.getCustomMetricMap();
        Assert.assertEquals(2, customMetricMap.size());

        IntCountMetricWrapper intCountMetricWrapper = (IntCountMetricWrapper) customMetricMap.get(intMetricName);
        Assert.assertEquals(intValue, intCountMetricWrapper.getValue());

        LongGaugeMetricWrapper longGaugeMetricWrapper = (LongGaugeMetricWrapper) customMetricMap.get(longMetricName);
        Assert.assertEquals(longValue, longGaugeMetricWrapper.getValue());

        customMetricRegistryService.unregister((IntCountMetric) intCountMetric);
        Assert.assertEquals(1, customMetricMap.size());

        customMetricRegistryService.unregister((LongGaugeMetric) longGaugeMetric);
        Assert.assertEquals(0, customMetricMap.size());
    }

    @Test
    public void limitNumberMetricTest() {
        int limitIdNumber = 10;

        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(limitIdNumber);

        int value = random.nextInt(100);

        for (int i = 0; i < limitIdNumber + 10; i++) {
            CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "groupName/metricName/label" + i, value, DoubleGaugeMetric.class);
            if (i < limitIdNumber) {
                Assert.assertNotNull(customMetric);
            } else {
                Assert.assertNull(customMetric);
            }
        }
    }

    @Test
    public void illegalMetricNameTest() {
        int limitIdNumber = 10;

        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(limitIdNumber);

        int value = random.nextInt(100);
        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "abcde", value, DoubleGaugeMetric.class);
        Assert.assertNull(customMetric);

        customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "abcde/abcde", value, DoubleGaugeMetric.class);
        Assert.assertNull(customMetric);

        customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "a@bcd@e/abcde/abcde", value, DoubleGaugeMetric.class);
        Assert.assertNull(customMetric);

        customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "a@bcd@e//abcde", value, DoubleGaugeMetric.class);
        Assert.assertNull(customMetric);
    }


    private CustomMetric createAndAddFixedValueMetric(CustomMetricRegistryService customMetricRegistryService, final String metricName, final Number fixedValue, Class clazz) {
        CustomMetric fixedValueMetric = createFixedValueMetric(metricName, fixedValue, clazz);
        boolean register = false;
        if (clazz == IntCountMetric.class) {
            register = customMetricRegistryService.register((IntCountMetric) fixedValueMetric);
        } else if (clazz == LongCountMetric.class) {
            register = customMetricRegistryService.register((LongCountMetric) fixedValueMetric);
        } else if (clazz == IntGaugeMetric.class) {
            register = customMetricRegistryService.register((IntGaugeMetric) fixedValueMetric);
        } else if (clazz == LongGaugeMetric.class) {
            register = customMetricRegistryService.register((LongGaugeMetric) fixedValueMetric);
        } else if (clazz == DoubleGaugeMetric.class) {
            register = customMetricRegistryService.register((DoubleGaugeMetric) fixedValueMetric);
        } else {
            throw new IllegalArgumentException("unsupported clazz");
        }

        if (register) {
            return fixedValueMetric;
        } else {
            return null;
        }
    }

    //
    private CustomMetric createFixedValueMetric(final String metricName, final Number fixedValue, Class clazz) {
        if (clazz == IntCountMetric.class) {
            return new IntCountMetric() {
                @Override
                public int getValue() {
                    return (Integer) fixedValue;
                }

                @Override
                public String getName() {
                    return metricName;
                }
            };
        }
        if (clazz == LongCountMetric.class) {
            return new LongCountMetric() {
                @Override
                public long getValue() {
                    return (Long) fixedValue;
                }

                @Override
                public String getName() {
                    return metricName;
                }
            };
        }
        if (clazz == IntGaugeMetric.class) {
            return new IntGaugeMetric() {
                @Override
                public int getValue() {
                    return (Integer) fixedValue;
                }

                @Override
                public String getName() {
                    return metricName;
                }
            };
        }
        if (clazz == LongGaugeMetric.class) {
            return new LongGaugeMetric() {
                @Override
                public long getValue() {
                    return (Long) fixedValue;
                }

                @Override
                public String getName() {
                    return metricName;
                }
            };
        }
        if (clazz == DoubleGaugeMetric.class) {
            return new DoubleGaugeMetric() {
                @Override
                public double getValue() {
                    return (Double) fixedValue;
                }

                @Override
                public String getName() {
                    return metricName;
                }
            };
        }
        throw new IllegalArgumentException("unsupported clazz");
    }

}

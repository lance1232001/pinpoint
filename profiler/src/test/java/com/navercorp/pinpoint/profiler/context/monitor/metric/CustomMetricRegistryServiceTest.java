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

import java.util.List;
import java.util.Random;

/**
 * @author Taejin Koo
 */
public class CustomMetricRegistryServiceTest {

    private final Random random = new Random(System.currentTimeMillis());

    @Test
    public void intCountMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        int value = random.nextInt(100);

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "groupName/metricName/label", value, IntCountMetric.class);
        Assert.assertNotNull(customMetric);

        List<IntCountMetricWrapper> metricWrapperList = customMetricRegistryService.getIntCountMetricWrapperList();
        Assert.assertEquals(1, metricWrapperList.size());

        IntCountMetricWrapper customMetricWrapper = metricWrapperList.get(0);
        Assert.assertEquals(value, customMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((IntCountMetric) customMetric);
        Assert.assertTrue(unregister);

        metricWrapperList = customMetricRegistryService.getIntCountMetricWrapperList();
        Assert.assertEquals(0, metricWrapperList.size());
    }

    @Test
    public void longCountMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        long value = random.nextLong();

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "groupName/metricName/label", value, LongCountMetric.class);
        Assert.assertNotNull(customMetric);

        List<LongCountMetricWrapper> metricWrapperList = customMetricRegistryService.getLongCountMetricWrapperList();
        Assert.assertEquals(1, metricWrapperList.size());

        LongCountMetricWrapper customMetricWrapper = metricWrapperList.get(0);
        Assert.assertEquals(value, customMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((LongCountMetric) customMetric);
        Assert.assertTrue(unregister);

        metricWrapperList = customMetricRegistryService.getLongCountMetricWrapperList();
        Assert.assertEquals(0, metricWrapperList.size());
    }

    @Test
    public void intGaugeMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        int value = random.nextInt(100);

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "groupName/metricName/label", value, IntGaugeMetric.class);
        Assert.assertNotNull(customMetric);

        List<IntGaugeMetricWrapper> metricWrapperList = customMetricRegistryService.getIntGaugeMetricWrapperList();
        Assert.assertEquals(1, metricWrapperList.size());

        IntGaugeMetricWrapper customMetricWrapper = metricWrapperList.get(0);
        Assert.assertEquals(value, customMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((IntGaugeMetric) customMetric);
        Assert.assertTrue(unregister);

        metricWrapperList = customMetricRegistryService.getIntGaugeMetricWrapperList();
        Assert.assertEquals(0, metricWrapperList.size());
    }

    @Test
    public void longGaugeMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        long value = random.nextLong();

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "groupName/metricName/label", value, LongGaugeMetric.class);
        Assert.assertNotNull(customMetric);

        List<LongGaugeMetricWrapper> metricWrapperList = customMetricRegistryService.getLongGaugeMetricWrapperList();
        Assert.assertEquals(1, metricWrapperList.size());

        LongGaugeMetricWrapper customMetricWrapper = metricWrapperList.get(0);
        Assert.assertEquals(value, customMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((LongGaugeMetric) customMetric);
        Assert.assertTrue(unregister);

        metricWrapperList = customMetricRegistryService.getLongGaugeMetricWrapperList();
        Assert.assertEquals(0, metricWrapperList.size());
    }

    @Test
    public void doubleGaugeMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        double value = random.nextDouble();

        CustomMetric customMetric = createAndAddFixedValueMetric(customMetricRegistryService, "groupName/metricName/label", value, DoubleGaugeMetric.class);
        Assert.assertNotNull(customMetric);

        List<DoubleGaugeMetricWrapper> metricWrapperList = customMetricRegistryService.getDoubleGaugeMetricWrapperList();
        Assert.assertEquals(1, metricWrapperList.size());

        DoubleGaugeMetricWrapper customMetricWrapper = metricWrapperList.get(0);
        Assert.assertTrue(value == customMetricWrapper.getValue());

        boolean unregister = customMetricRegistryService.unregister((DoubleGaugeMetric) customMetric);
        Assert.assertTrue(unregister);

        metricWrapperList = customMetricRegistryService.getDoubleGaugeMetricWrapperList();
        Assert.assertEquals(0, metricWrapperList.size());
    }

    @Test
    public void mixedCustomMetricTest() {
        CustomMetricRegistryService customMetricRegistryService = new DefaultCustomMetricRegistryService(10);

        int intValue = random.nextInt(100);
        CustomMetric intCountMetric = createAndAddFixedValueMetric(customMetricRegistryService, "groupName/metricName/int", intValue, IntCountMetric.class);
        Assert.assertNotNull(intCountMetric);

        long longValue = random.nextLong();
        CustomMetric longGaugeMetric = createAndAddFixedValueMetric(customMetricRegistryService, "groupName/metricName/long", longValue, LongGaugeMetric.class);
        Assert.assertNotNull(longGaugeMetric);

        List<IntCountMetricWrapper> intCountMetricWrapperList = customMetricRegistryService.getIntCountMetricWrapperList();
        Assert.assertEquals(1, intCountMetricWrapperList.size());
        Assert.assertEquals(intValue, intCountMetricWrapperList.get(0).getValue());

        List<LongGaugeMetricWrapper> longGaugeMetricWrapperList = customMetricRegistryService.getLongGaugeMetricWrapperList();
        Assert.assertEquals(1, longGaugeMetricWrapperList.size());
        Assert.assertEquals(longValue, longGaugeMetricWrapperList.get(0).getValue());

        customMetricRegistryService.unregister((IntCountMetric) intCountMetric);
        intCountMetricWrapperList = customMetricRegistryService.getIntCountMetricWrapperList();
        Assert.assertEquals(0, intCountMetricWrapperList.size());
        longGaugeMetricWrapperList = customMetricRegistryService.getLongGaugeMetricWrapperList();
        Assert.assertEquals(1, longGaugeMetricWrapperList.size());

        customMetricRegistryService.unregister((LongGaugeMetric) longGaugeMetric);
        intCountMetricWrapperList = customMetricRegistryService.getIntCountMetricWrapperList();
        Assert.assertEquals(0, intCountMetricWrapperList.size());
        longGaugeMetricWrapperList = customMetricRegistryService.getLongGaugeMetricWrapperList();
        Assert.assertEquals(0, longGaugeMetricWrapperList.size());
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

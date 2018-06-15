/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.aws.dynamodb;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class DynamoDBPredicateTest {

    @Mock
    private Metric metric;

    @Mock
    private Dimension dimension;

    @Test
    public void testNullIncludeTableNamesShouldReturnFalse() {
        List<String> includeTableNames = null;
        DynamoDBPredicate classUnderTest = new DynamoDBPredicate(includeTableNames);
        Assert.assertFalse(classUnderTest.apply(metric));
    }

    @Test
    public void testEmptyIncludeTableNamesShouldReturnFalse() {
        List<String> includeTableNames = new ArrayList<>();
        DynamoDBPredicate classUnderTest = new DynamoDBPredicate(includeTableNames);
        Assert.assertFalse(classUnderTest.apply(metric));
    }

    @Test
    public void testWildCardIncludeTableNamesShouldReturnTrue() {
        List<String> includeTableNames = Lists.newArrayList(".*");
        DynamoDBPredicate classUnderTest = new DynamoDBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Dashboard");
        Assert.assertTrue(classUnderTest.apply(metric));
    }

    @Test
    public void testIncludeTableNamesMatchingShouldReturnTrue() {
        List<String> includeTableNames = Lists.newArrayList("^Dashboards$");
        DynamoDBPredicate classUnderTest = new DynamoDBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Dashboards");
        Assert.assertTrue(classUnderTest.apply(metric));
    }

    @Test
    public void testIncludeTableNamesContainsShouldReturnTrue() {
        List<String> includeTableNames = Lists.newArrayList("Dash.*");
        DynamoDBPredicate classUnderTest = new DynamoDBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Dashboards");
        Assert.assertTrue(classUnderTest.apply(metric));
    }

    @Test
    public void testIncludeTableNamesMatching1ShouldReturnTrue() {
        List<String> includeTableNames = Lists.newArrayList("Dashboards", "test", "");
        DynamoDBPredicate classUnderTest = new DynamoDBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Dashboards");
        Assert.assertTrue(classUnderTest.apply(metric));
    }

    @Test
    public void testIncludeTableNamesNotMatchingShouldReturnFalse() {
        List<String> includeTableNames = Lists.newArrayList("Dashboards", "test");
        DynamoDBPredicate classUnderTest = new DynamoDBPredicate(includeTableNames);
        when(metric.getDimensions()).thenReturn(Lists.newArrayList(dimension));
        when(dimension.getValue()).thenReturn("Hammer");
        Assert.assertFalse(classUnderTest.apply(metric));
    }
}

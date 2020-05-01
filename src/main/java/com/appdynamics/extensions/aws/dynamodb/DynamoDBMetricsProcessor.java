/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.aws.dynamodb;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.DimensionFilter;
import com.appdynamics.extensions.aws.config.IncludeMetric;
import com.appdynamics.extensions.aws.dto.AWSMetric;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.StatisticType;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.google.common.collect.Lists;
import org.slf4j.Logger;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Florencio Sarmiento
 *
 */
public class DynamoDBMetricsProcessor implements MetricsProcessor {

	private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(DynamoDBMetricsProcessor.class);
	
	private static final String NAMESPACE = "AWS/DynamoDB";
	
	private static final String DIMENSIONS = "TableName";
	
	private List<IncludeMetric> includeMetrics;
	private List<String> includeTableNames;
	
	public DynamoDBMetricsProcessor(List<IncludeMetric> includeMetrics, List<String> includeTableNames) {
		this.includeMetrics = includeMetrics;
		this.includeTableNames = includeTableNames;
	}

	public List<AWSMetric> getMetrics(AmazonCloudWatch awsCloudWatch, String accountName, LongAdder awsRequestsCounter) {
		List<DimensionFilter> dimensionFilters = getDimensionFilters();

		DynamoDBPredicate dynamoDBPredicate = new DynamoDBPredicate(includeTableNames);

		return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch, awsRequestsCounter,
				NAMESPACE, 
				includeMetrics,
				dimensionFilters, dynamoDBPredicate);
	}

	private List<DimensionFilter> getDimensionFilters() {
		List<DimensionFilter> dimensionFilters = Lists.newArrayList();
		DimensionFilter dbDimensionFilter = new DimensionFilter();
		dbDimensionFilter.withName(DIMENSIONS);
		dimensionFilters.add(dbDimensionFilter);
		return dimensionFilters;
	}

	public StatisticType getStatisticType(AWSMetric metric) {
		return MetricsProcessorHelper.getStatisticType(metric.getIncludeMetric(), includeMetrics);
	}

	public List<com.appdynamics.extensions.metrics.Metric> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {
		Map<String, String> dimensionToMetricPathNameDictionary = new HashMap<String, String>();
		dimensionToMetricPathNameDictionary.put(DIMENSIONS, "TableName");
		
		return MetricsProcessorHelper.createMetricStatsMapForUpload(namespaceMetricStats, 
				dimensionToMetricPathNameDictionary, false);
	}
	
	public String getNamespace() {
		return NAMESPACE;
	}

}

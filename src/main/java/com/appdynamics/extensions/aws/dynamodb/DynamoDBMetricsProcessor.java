/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.dynamodb;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.appdynamics.extensions.aws.config.IncludeMetric;
import com.appdynamics.extensions.aws.dto.AWSMetric;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.StatisticType;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Florencio Sarmiento
 *
 */
public class DynamoDBMetricsProcessor implements MetricsProcessor {

	private static final Logger LOGGER = Logger.getLogger(DynamoDBMetricsProcessor.class);
	
	private static final String NAMESPACE = "AWS/DynamoDB";
	
	private static final String[] DIMENSIONS = {"TableName"};
	
	private List<IncludeMetric> includeMetrics;
	
	public DynamoDBMetricsProcessor(List<IncludeMetric> includeMetrics) {
		this.includeMetrics = includeMetrics;
	}

	public List<AWSMetric> getMetrics(AmazonCloudWatch awsCloudWatch, String accountName, LongAdder awsRequestsCounter) {
		return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch, awsRequestsCounter,
				NAMESPACE, 
				includeMetrics,
				DIMENSIONS);
	}
	
	public StatisticType getStatisticType(AWSMetric metric) {
		return MetricsProcessorHelper.getStatisticType(metric.getIncludeMetric(), includeMetrics);
	}

	public List<com.appdynamics.extensions.metrics.Metric> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {
		Map<String, String> dimensionToMetricPathNameDictionary = new HashMap<String, String>();
		dimensionToMetricPathNameDictionary.put(DIMENSIONS[0], "Table Name");
		
		return MetricsProcessorHelper.createMetricStatsMapForUpload(namespaceMetricStats, 
				dimensionToMetricPathNameDictionary, false);
	}
	
	public String getNamespace() {
		return NAMESPACE;
	}

}

/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.dynamodb;

import static com.appdynamics.extensions.aws.Constants.METRIC_PATH_SEPARATOR;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.appdynamics.extensions.aws.SingleNamespaceCloudwatchMonitor;
import com.appdynamics.extensions.aws.collectors.NamespaceMetricStatisticsCollector;
import com.appdynamics.extensions.aws.config.Configuration;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;

/**
 * @author Florencio Sarmiento
 *
 */
public class DynamoDBMonitor extends SingleNamespaceCloudwatchMonitor<Configuration> {
	
	private static final Logger LOGGER = Logger.getLogger("com.singularity.extensions.aws.DynamoDBMonitor");

	private static final String DEFAULT_METRIC_PREFIX = String.format("%s%s%s%s", 
			"Custom Metrics", METRIC_PATH_SEPARATOR, "Amazon DynamoDB", METRIC_PATH_SEPARATOR);
	
	public DynamoDBMonitor() {
		super(Configuration.class);
		LOGGER.info(String.format("Using AWS DynamoDB Monitor Version [%s]", 
				this.getClass().getPackage().getImplementationTitle()));
	}

	@Override
	protected NamespaceMetricStatisticsCollector getNamespaceMetricsCollector(
			Configuration config) {
		MetricsProcessor metricsProcessor = createMetricsProcessor(config);

		return new NamespaceMetricStatisticsCollector
				.Builder(config.getAccounts(),
						config.getConcurrencyConfig(), 
						config.getMetricsConfig(),
						metricsProcessor)
				.withCredentialsEncryptionConfig(config.getCredentialsDecryptionConfig())
				.withProxyConfig(config.getProxyConfig())
				.build();
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}
	
	@Override
	protected String getMetricPrefix(Configuration config) {
		return StringUtils.isNotBlank(config.getMetricPrefix()) ? 
				config.getMetricPrefix() : DEFAULT_METRIC_PREFIX;
	}

	private MetricsProcessor createMetricsProcessor(Configuration config) {
		return new DynamoDBMetricsProcessor(
				config.getMetricsConfig().getMetricTypes(), 
				config.getMetricsConfig().getExcludeMetrics());
	}

}

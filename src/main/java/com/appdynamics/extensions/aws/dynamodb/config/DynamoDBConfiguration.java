/*
 * Copyright [YEAR]. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.aws.dynamodb.config;

import com.appdynamics.extensions.aws.config.Configuration;

import java.util.List;

public class DynamoDBConfiguration extends Configuration {

    private List<String> includeTableNames;

    public List<String> getIncludeTableNames() {
        return includeTableNames;
    }

    public void setIncludeTableNames(List<String> includeTableNames) {
        this.includeTableNames = includeTableNames;
    }
}

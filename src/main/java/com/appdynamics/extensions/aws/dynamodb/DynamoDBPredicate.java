/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.aws.dynamodb;


import com.amazonaws.services.cloudwatch.model.Metric;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.util.List;


public class DynamoDBPredicate implements Predicate<Metric> {

    private static final Logger LOGGER = Logger.getLogger(DynamoDBPredicate.class);

    private List<String> includeTableNames;
    private Predicate<CharSequence> patternPredicate;

    public DynamoDBPredicate(List<String> includeTableNames) {
        this.includeTableNames = includeTableNames;
        build();
    }

    private void build() {
        if (includeTableNames != null && !includeTableNames.isEmpty()) {
            for (String pattern : includeTableNames) {
                if (!Strings.isNullOrEmpty(pattern)) {
                    Predicate<CharSequence> charSequencePredicate = Predicates.containsPattern(pattern);
                    if (patternPredicate == null) {
                        patternPredicate = charSequencePredicate;
                    } else {
                        patternPredicate = Predicates.or(patternPredicate, charSequencePredicate);
                    }
                }
            }
        } else {
            LOGGER.warn("includeTableNames in config.yml not configured, hence not monitoring any tableNames");
        }
    }

    @Override
    public boolean apply(Metric metric) {
        if (patternPredicate == null) {
            return false;
        }
        String tableName = metric.getDimensions().get(0).getValue();
        return patternPredicate.apply(tableName);
    }
}

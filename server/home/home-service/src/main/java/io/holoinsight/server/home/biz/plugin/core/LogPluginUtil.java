/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.common.MonitorException;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric.AfterFilter;
import io.holoinsight.server.home.dal.model.dto.conf.CollectMetric.Metric;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.SplitCol;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf.SpmCols;
import io.holoinsight.server.home.dal.model.dto.conf.FilterType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jsy1001de
 * @version 1.0: LogPluginUtil.java, Date: 2024-01-19 Time: 15:18
 */
public class LogPluginUtil {


  public static void addSpmCols(CustomPluginConf conf) {

    Boolean spm = checkSpmConditions(conf);
    Set<String> spmSets =
        new HashSet<>(Arrays.asList("total", "success", "fail", "cost", "successPercent"));

    List<CollectMetric> newCollectMetrics = new ArrayList<>();
    if (!CollectionUtils.isEmpty(conf.collectMetrics)) {
      for (CollectMetric collectMetric : conf.collectMetrics) {
        if (null == collectMetric.spm || Boolean.FALSE == collectMetric.spm) {
          if (spm == Boolean.TRUE && spmSets.contains(collectMetric.tableName)) {
            spmSets.remove(collectMetric.tableName);
            continue;
          }
          newCollectMetrics.add(collectMetric);
          continue;
        }

        if (spm == Boolean.FALSE) {
          continue;
        }
        spmSets.remove(collectMetric.tableName);
        newCollectMetrics.add(collectMetric);
      }
    }

    // add spm metrics
    if (spm == Boolean.TRUE && !spmSets.isEmpty()) {
      SpmCols spmCols = conf.spmCols;

      List<String> tags = new ArrayList<>();
      List<SplitCol> splitCols = conf.splitCols;
      if (!CollectionUtils.isEmpty(splitCols)) {
        splitCols.forEach(splitCol -> {
          if (splitCol.colType.equalsIgnoreCase("DIM")) {
            tags.add(splitCol.name);
          }
        });
      }

      for (String apmMetric : spmSets) {
        switch (apmMetric) {
          case "total":
            newCollectMetrics.add(genTotalCollectMetric(spmCols, tags));
            break;
          case "success":
            newCollectMetrics.add(genSuccessCollectMetric(spmCols, tags));
            break;
          case "fail":
            newCollectMetrics.add(genFailCollectMetric(spmCols, tags));
            break;
          case "cost":
            newCollectMetrics.add(genCostCollectMetric(spmCols, tags));
            break;
          case "successPercent":
            newCollectMetrics.add(genSuccessPercentCollectMetric(tags));
            break;
        }
      }
    }
    conf.setCollectMetrics(newCollectMetrics);
  }


  private static Boolean checkSpmConditions(CustomPluginConf conf) {
    if (null == conf || null == conf.spm) {
      return Boolean.FALSE;
    } else if (Boolean.FALSE == conf.spm) {
      return Boolean.FALSE;
    }

    if (null == conf.spmCols) {
      throw new MonitorException("spmCols is null");
    }

    SpmCols spmCols = conf.spmCols;
    if (StringUtils.isBlank(spmCols.resultKey)) {
      throw new MonitorException("resultCols is null");
    }
    if (CollectionUtils.isEmpty(spmCols.successValue)) {
      throw new MonitorException("successValue is null");
    }
    if (StringUtils.isBlank(spmCols.costKey)) {
      throw new MonitorException("costCols is null");
    }

    return Boolean.TRUE;
  }

  private static CollectMetric genTotalCollectMetric(SpmCols spmCols, List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("total");
    collectMetric.setMetricType("count");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("count");
    metric.setName("value");
    if (StringUtils.isNotBlank(spmCols.countKey)) {
      collectMetric.setMetricType("select");
      metric.setFunc("sum");
      metric.setName(spmCols.countKey);
    }
    collectMetric.setMetrics(Collections.singletonList(metric));
    return collectMetric;
  }

  private static CollectMetric genSuccessCollectMetric(SpmCols spmCols, List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("success");
    collectMetric.setMetricType("count");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("count");
    metric.setName("value");
    if (StringUtils.isNotBlank(spmCols.countKey)) {
      collectMetric.setMetricType("select");
      metric.setFunc("sum");
      metric.setName(spmCols.countKey);
    }
    collectMetric.setMetrics(Collections.singletonList(metric));

    AfterFilter afterFilter = new AfterFilter();
    afterFilter.setName(spmCols.resultKey);
    afterFilter.setFilterType(FilterType.IN);
    afterFilter.setValues(spmCols.successValue);
    collectMetric.setAfterFilters(Collections.singletonList(afterFilter));

    return collectMetric;
  }

  private static CollectMetric genFailCollectMetric(SpmCols spmCols, List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("fail");
    collectMetric.setMetricType("count");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("count");
    metric.setName("value");
    if (StringUtils.isNotBlank(spmCols.countKey)) {
      collectMetric.setMetricType("select");
      metric.setFunc("sum");
      metric.setName(spmCols.countKey);
    }
    collectMetric.setMetrics(Collections.singletonList(metric));

    AfterFilter afterFilter = new AfterFilter();
    afterFilter.setName(spmCols.resultKey);
    afterFilter.setFilterType(FilterType.NOT_IN);
    afterFilter.setValues(spmCols.successValue);
    collectMetric.setAfterFilters(Collections.singletonList(afterFilter));

    return collectMetric;
  }

  private static CollectMetric genCostCollectMetric(SpmCols spmCols, List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("cost");
    collectMetric.setMetricType("select");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("avg");
    metric.setName(spmCols.costKey);
    collectMetric.setMetrics(Collections.singletonList(metric));

    return collectMetric;
  }

  private static CollectMetric genSuccessPercentCollectMetric(List<String> tags) {
    CollectMetric collectMetric = new CollectMetric();
    collectMetric.setTableName("successPercent");
    collectMetric.setMetricType("count");
    collectMetric.setTags(tags);
    collectMetric.setSpm(true);
    Metric metric = new Metric();
    metric.setFunc("count");
    metric.setName("value");
    collectMetric.setMetrics(Collections.singletonList(metric));


    return collectMetric;
  }
}

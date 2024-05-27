/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model.integration.alicloud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zzhb101
 * @version : AlicloudMetrics.java, v 0.1 2022年11月17日 15:53 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlicloudMetric {
  private String aliyunMetricName;
  private String convertedMetricName;
  private List<String> dimensions;
  private List<String> aggregations;
}

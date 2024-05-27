/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author zzhb101
 * @version 1.0: CollectMetricConf.java, v 0.1 2022年11月21日 下午12:02 jinsong.yjs Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectMetricConf {
  public String name;
  public String metricType;
}

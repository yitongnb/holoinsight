/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity.dto;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.dto.alarm.AlarmRuleConf;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.DataSource;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.Filter;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.Trigger;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author wangsiyuan
 * @date 2022/4/12 9:38 下午
 */
@Data
public class AlarmRuleDTO {

  /**
   * id
   */
  private Long id;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  /**
   * 规则名称
   */
  private String ruleName;

  /**
   * 规则类型（ai、rule、pql）
   */
  private String ruleType;

  /**
   * 创建者
   */
  private String creator;

  /**
   * 修改者
   */
  private String modifier;

  /**
   * 告警级别
   */
  private String alarmLevel;

  /**
   * 规则描述
   */
  private String ruleDescribe;

  /**
   * 规则是否生效
   */
  private Byte status;

  /**
   * 合并是否开启
   */
  private Byte isMerge;

  /**
   * 合并方式
   */
  private String mergeType;

  /**
   * 恢复通知是否开启
   */
  private Byte recover;

  /**
   * 通知方式
   */
  private String noticeType;

  /**
   * 触发方式简述
   */
  private List<String> alarmContent;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  /**
   * 告警规则
   */
  private Map<String, Object> rule;

  /**
   * 生效时间
   */
  private Map<String, Object> timeFilter;

  /**
   * 屏蔽id
   */
  private Long blockId;

  /**
   * 来源类型
   */
  private String sourceType;

  /**
   * 来源id
   */
  private Long sourceId;

  /**
   * 额外信息
   */
  private AlertRuleExtra extra;

  /**
   * 环境类型
   */
  private String envType;

  /**
   * pql
   */
  private String pql;

  private Long alertNotificationTemplateId;

  /**
   * 告警模板UUID
   */
  private String alertTemplateUuid;

  public static void tryParseLink(AlarmRuleDTO alarmRuleDTO, String domain,
      Map<String /* metric */, Map<String /* type */, String /* page */>> systemMetrics,
      String parentId) {
    if (alarmRuleDTO.isCreate()) {
      if (StringUtils.isNotEmpty(alarmRuleDTO.sourceType)) {
        if (alarmRuleDTO.extra == null) {
          alarmRuleDTO.extra = new AlertRuleExtra();
        }
        switch (alarmRuleDTO.sourceType) {
          case "log":
            alarmRuleDTO.extra.sourceLink =
                domain + "log/metric/" + alarmRuleDTO.sourceId + "?tenant=" + alarmRuleDTO.tenant;
            if (StringUtils.isNotEmpty(parentId)) {
              alarmRuleDTO.extra.sourceLink =
                  alarmRuleDTO.extra.sourceLink + "&parentId=" + parentId;
            }
            break;
          case "dashboard":
            alarmRuleDTO.extra.sourceLink = domain + "m/dashboard/preview/" + alarmRuleDTO.sourceId;
            break;
          default:
            if (alarmRuleDTO.sourceType.startsWith("apm_")) {
              alarmRuleDTO.extra.sourceLink = getApmLink(alarmRuleDTO, domain, systemMetrics);
            }
        }
      }
    }
  }

  private boolean isCreate() {
    return id == null;
  }

  protected static String getApmLink(AlarmRuleDTO alarmRuleDTO, String domain,
      Map<String /* metric */, Map<String /* type */, String /* page */>> systemMetrics) {
    String app = alarmRuleDTO.sourceType.split("_")[1];
    List<String> metrics = alarmRuleDTO.getMetric();
    if (CollectionUtils.isEmpty(metrics)) {
      return StringUtils.EMPTY;
    }
    String metric = metrics.get(0);
    boolean isServer = alarmRuleDTO.checkIsServer();
    if (StringUtils.isEmpty(metric) || CollectionUtils.isEmpty(systemMetrics)) {
      return StringUtils.EMPTY;
    }
    Map<String /* type */, String /* page */> pages = systemMetrics.get(metric);
    if (CollectionUtils.isEmpty(pages)) {
      return StringUtils.EMPTY;
    }
    String type = isServer ? "server" : "app";
    String page = pages.get(type);
    return domain + page + "&app=" + app;
  }

  private boolean checkIsServer() {
    if (CollectionUtils.isEmpty(this.rule)) {
      return false;
    }
    List<Map<String, Object>> triggers = (List<Map<String, Object>>) this.rule.get("triggers");
    Map<String, Object> trigger = triggers.get(0);
    List<Map<String, Object>> datasources = (List<Map<String, Object>>) trigger.get("datasources");
    if (CollectionUtils.isEmpty(datasources)) {
      return false;
    }
    Map<String, Object> datasource = datasources.get(0);
    List<String> groupBy = (List<String>) datasource.get("groupBy");
    return CollectionUtils.isEmpty(groupBy) || groupBy.contains("hostname")
        || groupBy.contains("pod") || groupBy.contains("ip");
  }

  public List<String> getMetric() {
    if (CollectionUtils.isEmpty(this.rule)) {
      return Collections.emptyList();
    }
    List<Map<String, Object>> triggers = (List<Map<String, Object>>) this.rule.get("triggers");
    if (CollectionUtils.isEmpty(triggers)) {
      return Collections.emptyList();
    }
    List<String> metrics = new ArrayList<>();
    for (Map<String, Object> trigger : triggers) {
      List<Map<String, Object>> datasources =
          (List<Map<String, Object>>) trigger.get("datasources");
      if (CollectionUtils.isEmpty(datasources)) {
        return Collections.emptyList();
      }

      for (Map<String, Object> datasource : datasources) {
        String metric = (String) datasource.get("metric");
        if (StringUtils.isEmpty(metric)) {
          continue;
        }
        metrics.add(metric);
      }
    }

    return metrics;
  }

  public Map<String /* tagk */, List<Object> /* tagvs */> getFilters(String metric) {
    Map<String, List<Object>> filters = new HashMap<>();
    if (CollectionUtils.isEmpty(this.rule)) {
      return Collections.emptyMap();
    }
    AlarmRuleConf rule =
        J.fromJson(J.toJson(this.rule), new TypeToken<AlarmRuleConf>() {}.getType());

    if (CollectionUtils.isEmpty(rule.getTriggers())) {
      return Collections.emptyMap();
    }
    for (Trigger trigger : rule.getTriggers()) {
      if (CollectionUtils.isEmpty(trigger.getDatasources())) {
        continue;
      }
      for (DataSource dataSource : trigger.getDatasources()) {
        if (CollectionUtils.isEmpty(dataSource.getFilters())) {
          continue;
        }
        if (!StringUtils.equals(metric, dataSource.getMetric())) {
          continue;
        }
        for (Filter filter : dataSource.getFilters()) {
          String key = filter.getName();
          String type = filter.getType();
          List<Object> values = new ArrayList<>();
          switch (type) {
            case "literal_or":
            case "not_literal_or":
              values.addAll(Arrays.asList(filter.getValue().split("\\|")));
              break;
            default:
              values.add(filter.getValue());
              break;
          }
          List<Object> filterValues = filters.computeIfAbsent(key, k -> new ArrayList<>());
          filterValues.addAll(values);
        }
      }
    }
    return filters;
  }
}

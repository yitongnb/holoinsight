/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.emuns.FunctionEnum;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerAIResult;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerDataResult;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerResult;
import io.holoinsight.server.home.alert.model.compute.algorithm.anomaly.AlgorithmConfig;
import io.holoinsight.server.home.alert.model.compute.algorithm.anomaly.AnomalyAlgorithmRequest;
import io.holoinsight.server.home.alert.model.compute.algorithm.anomaly.AnomalyAlgorithmResponse;
import io.holoinsight.server.home.alert.model.compute.algorithm.anomaly.RuleConfig;
import io.holoinsight.server.home.alert.model.function.FunctionConfigAIParam;
import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.alert.model.function.FunctionLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/10/11 9:12 pm
 */
@Service
public class AnomalyUpAbnormalDetect implements FunctionLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(AnomalyUpAbnormalDetect.class);

  @Value("${holoinsight.alert.algorithm.url}")
  private String url;

  @Override
  public FunctionEnum getFunc() {
    return FunctionEnum.AnomalyUp;
  }

  @Override
  public TriggerResult invoke(TriggerDataResult triggerDataResult,
      FunctionConfigParam functionConfigParam) {
    FunctionConfigAIParam functionConfigAIParam = (FunctionConfigAIParam) functionConfigParam;
    AnomalyAlgorithmRequest algorithmRequest = new AnomalyAlgorithmRequest();
    AlgorithmConfig algorithmConfig = new AlgorithmConfig();
    RuleConfig ruleConfig = new RuleConfig();
    ruleConfig.setDefaultDuration(1);
    ruleConfig.setCustomChangeRate(0.1);
    algorithmConfig.setAlgorithmType("up");
    algorithmConfig.setSensitivity("mid");
    algorithmRequest.setIntervalTime(60000);
    algorithmRequest.setDetectTime(functionConfigAIParam.getPeriod());
    algorithmRequest.setAlgorithmConfig(algorithmConfig);
    algorithmRequest.setRuleConfig(ruleConfig);
    Map<String, Double> inputTimeSeries = new HashMap<>();
    triggerDataResult.getPoints().forEach((k, v) -> inputTimeSeries.put(k.toString(), v));
    algorithmRequest.setInputTimeSeries(inputTimeSeries);

    TriggerAIResult triggerAIResult = new TriggerAIResult();
    // Set the name of the algorithm interface
    String algoUrl = url + "/anomaly_detect";
    // Call algorithm interface
    String abnormalResult = AlgorithmHttp.invokeAlgorithm(algoUrl, J.toJson(algorithmRequest),
        functionConfigParam.getTraceId());
    AnomalyAlgorithmResponse anomalyAlgorithmResponse =
        J.fromJson(abnormalResult, AnomalyAlgorithmResponse.class);
    if (anomalyAlgorithmResponse != null && anomalyAlgorithmResponse.getIsSuccessful()
        && anomalyAlgorithmResponse.getIsException()) {
      triggerAIResult.setHit(true);
      triggerAIResult.setCurrentValue(
          triggerDataResult.getPoints().get(anomalyAlgorithmResponse.getDetectTime()));
    }
    return triggerAIResult;
  }
}

/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerDataResult;
import io.holoinsight.server.common.dao.emuns.FunctionEnum;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.CompareParam;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerResult;
import org.springframework.stereotype.Service;

/**
 * @author masaimu
 * @version 2023-03-21 14:37:00
 */
@Service
public class PeriodRate extends BaseFunction {
  @Override
  public FunctionEnum getFunc() {
    return FunctionEnum.PeriodRate;
  }

  @Override
  public TriggerResult invoke(TriggerDataResult triggerDataResult,
      FunctionConfigParam functionConfigParam) {
    return doInvoke(triggerDataResult, functionConfigParam);
  }

  @Override
  protected double getValue(CompareParam cmp) {
    double value = cmp.getCmpValue() == null ? 0d : cmp.getCmpValue();
    value = value / 100;
    return value;
  }

  @Override
  protected Double getComparedValue(Double current, Double past) {
    if (current == null || past == null) {
      return null;
    }
    return rate(current, past);
  }
}

/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.service.RequestContextAdapter;
import io.holoinsight.server.common.scope.MonitorScope;
import io.holoinsight.server.common.scope.MonitorUser;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.dao.mapper.AlarmDingDingRobotMapper;
import io.holoinsight.server.common.dao.entity.AlarmDingDingRobot;
import io.holoinsight.server.common.dao.entity.dto.AlarmDingDingRobotDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheck;
import io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult;
import io.holoinsight.server.home.web.security.LevelAuthorizationMetaData;
import io.holoinsight.server.home.web.security.ParameterSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static io.holoinsight.server.home.web.controller.AlarmDingDingRobotFacadeImpl.dingdingUrlPrefix;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.failCheckResult;
import static io.holoinsight.server.home.web.security.LevelAuthorizationCheckResult.successCheckResult;

/**
 * @author masaimu
 * @version 2024-01-29 11:32:00
 */
@Slf4j
@Service
public class AlarmDingDingRobotFacadeImplChecker
    implements AbstractResourceChecker, LevelAuthorizationCheck {

  @Autowired
  private AlarmDingDingRobotMapper alarmDingDingRobotMapper;
  @Autowired
  private ParameterSecurityService parameterSecurityService;

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @Override
  public LevelAuthorizationCheckResult check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation) {
    MonitorScope ms = RequestContext.getContext().ms;
    String workspace = this.requestContextAdapter.getWorkspace(true);
    String tenant = ms.getTenant();

    List<String> parameters = levelAuthMetaData.getParameters();
    String methodName = methodInvocation.getMethod().getName();
    return checkParameters(methodName, parameters, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkParameters(String methodName, List<String> parameters,
      String tenant, String workspace) {
    switch (methodName) {
      case "create":
      case "update":
        return checkAlarmDingDingRobotDTO(methodName, parameters, tenant, workspace);
      case "queryById":
        return checkIdNotNull(parameters);
      case "deleteById":
        return checkIdExists(parameters, tenant, workspace);
      case "pageQuery":
        return checkPageRequest(methodName, parameters, tenant, workspace);
      default:
        return successCheckResult();
    }
  }



  private LevelAuthorizationCheckResult checkPageRequest(String methodName, List<String> parameters,
      String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    String parameter = parameters.get(0);
    MonitorPageRequest<AlarmDingDingRobotDTO> pageRequest = J.fromJson(parameter,
        new TypeToken<MonitorPageRequest<AlarmDingDingRobotDTO>>() {}.getType());

    if (pageRequest.getFrom() != null && pageRequest.getTo() != null) {
      if (pageRequest.getFrom() > pageRequest.getTo()) {
        return failCheckResult("fail to check time range for start %d larger than end %d",
            pageRequest.getFrom(), pageRequest.getTo());
      }
    }

    AlarmDingDingRobotDTO target = pageRequest.getTarget();
    if (target == null) {
      return failCheckResult("fail to check target, target can not be null");
    }
    return checkAlarmDingDingRobotDTO(methodName, target, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkAlarmDingDingRobotDTO(String methodName,
      List<String> parameters, String tenant, String workspace) {
    if (CollectionUtils.isEmpty(parameters) || StringUtils.isBlank(parameters.get(0))) {
      return failCheckResult("parameters is empty");
    }
    log.info("checkParameters {} parameter {}", methodName, parameters.get(0));
    AlarmDingDingRobotDTO dto = J.fromJson(parameters.get(0), AlarmDingDingRobotDTO.class);
    return checkAlarmDingDingRobotDTO(methodName, dto, tenant, workspace);
  }

  private LevelAuthorizationCheckResult checkAlarmDingDingRobotDTO(String methodName,
      AlarmDingDingRobotDTO dto, String tenant, String workspace) {
    if (methodName.equals("create")) {
      if (dto.getId() != null) {
        return failCheckResult("fail to check %s for id is not null", methodName);
      }
      if (StringUtils.isBlank(dto.getGroupName())) {
        return failCheckResult("group name can not be blank.");
      }
    }

    if (methodName.equals("update")) {
      if (dto.getId() == null) {
        return failCheckResult("fail to check %s for id is null", methodName);
      }
      LevelAuthorizationCheckResult checkResult = checkIdExists(dto.getId(), tenant, workspace);
      if (!checkResult.isSuccess()) {
        return checkResult;
      }
    }

    if (StringUtils.isNotEmpty(dto.getRobotUrl())
        && !dto.getRobotUrl().startsWith(dingdingUrlPrefix)) {
      return failCheckResult("invalid robotUrl %s", dto.getRobotUrl());
    }

    if (StringUtils.isNotEmpty(dto.getCreator()) && !checkSqlField(dto.getCreator())) {
      return failCheckResult("fail to check %s for invalid creator %s", methodName,
          dto.getCreator());
    }

    if (StringUtils.isNotEmpty(dto.getModifier()) && !checkSqlField(dto.getModifier())) {
      return failCheckResult("fail to check %s for invalid modifier %s", methodName,
          dto.getModifier());
    }

    if (StringUtils.isNotEmpty(dto.getGroupName()) && !checkSqlName(dto.getGroupName())) {
      return failCheckResult("fail to check %s for invalid group name %s", methodName,
          dto.getGroupName());
    }

    if (StringUtils.isNotEmpty(dto.getTenant()) && !StringUtils.equals(dto.getTenant(), tenant)) {
      return failCheckResult("fail to check %s for invalid tenant %s for valid tenant %s",
          methodName, dto.getTenant(), tenant);
    }

    if (StringUtils.isNotEmpty(dto.getWorkspace())
        && !StringUtils.equals(dto.getWorkspace(), workspace)) {
      return failCheckResult("fail to check %s for invalid workspace %s for valid workspace %s",
          methodName, dto.getWorkspace(), workspace);
    }

    if (StringUtils.isNotEmpty(dto.getExtra()) && !checkUserIds(dto.getExtra())) {
      return failCheckResult("fail to check %s that userIds in extra %s", methodName,
          dto.getExtra());
    }

    return successCheckResult();
  }

  private boolean checkUserIds(String extra) {
    Map<String, Object> extraMap = J.toMap(extra);
    List<String> userIds = (List<String>) extraMap.get("userIds");
    if (CollectionUtils.isEmpty(userIds)) {
      return true;
    }
    MonitorUser mu = RequestContext.getContext().mu;
    for (String uid : userIds) {
      if (!this.parameterSecurityService.checkUserTenantAndWorkspace(uid, mu)) {
        log.error("fail to check uid {}", uid);
        return false;
      }
    }
    return true;
  }

  @Override
  public LevelAuthorizationCheckResult checkIdExists(Long id, String tenant, String workspace) {
    QueryWrapper<AlarmDingDingRobot> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("id", id);
    this.requestContextAdapter.queryWrapperTenantAdapt(queryWrapper, tenant, workspace);

    List<AlarmDingDingRobot> exist = this.alarmDingDingRobotMapper.selectList(queryWrapper);
    if (CollectionUtils.isEmpty(exist)) {
      return failCheckResult("fail to check id for no existed %d %s %s", id, tenant, workspace);
    }
    return successCheckResult();
  }
}

/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.service.AlarmHistoryService;
import io.holoinsight.server.common.ManageCallback;
import io.holoinsight.server.common.scope.AuthTargetType;
import io.holoinsight.server.common.scope.PowerConstants;
import io.holoinsight.server.common.dao.entity.dto.AlarmHistoryDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.home.web.security.LevelAuthorizationAccess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jsy1001de
 * @version 1.0: AlarmHistoryFacadeImpl.java, v 0.1 2022年04月08日 2:56 下午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/alarmHistory")
public class AlarmHistoryFacadeImpl extends BaseFacade {

  @Autowired
  private AlarmHistoryService alarmHistoryService;

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!id"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmHistoryFacadeImplChecker")
  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlarmHistoryDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<AlarmHistoryDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        AlarmHistoryDTO save = alarmHistoryService.queryById(id, tenant(), workspace());
        JsonResult.createSuccessResult(result, save);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!id"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmHistoryFacadeImplChecker")
  @DeleteMapping("/delete/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<Boolean> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        boolean rtn = false;
        AlarmHistoryDTO alarmHistoryDTO = alarmHistoryService.queryById(id, tenant(), workspace());
        if (alarmHistoryDTO != null) {
          rtn = alarmHistoryService.deleteById(id);
        }

        JsonResult.createSuccessResult(result, rtn);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!pageRequest"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlarmHistoryFacadeImplChecker")
  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlarmHistoryDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlarmHistoryDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlarmHistoryDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        String tenant = tenant();
        String workspace = workspace();
        if (StringUtils.isNotEmpty(tenant)) {
          pageRequest.getTarget().setTenant(tenant);
        }

        if (StringUtils.isNotEmpty(workspace)) {
          pageRequest.getTarget().setWorkspace(workspace);
        }
        JsonResult.createSuccessResult(result,
            alarmHistoryService.getListByPage(pageRequest, pageRequest.getTarget().getUniqueIds()));
      }
    });

    return result;
  }
}

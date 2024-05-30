/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.service.AlertNotifyRecordService;
import io.holoinsight.server.common.scope.AuthTargetType;
import io.holoinsight.server.common.scope.MonitorScope;
import io.holoinsight.server.common.scope.PowerConstants;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.common.dao.entity.dto.AlertNotifyRecordDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author limengyang
 * @date 2023/7/17 17:08
 */
@Slf4j
@RestController
@RequestMapping("/webapi/alertNotifyRecord")
public class AlertNotifyRecordController extends BaseFacade {

  @Autowired
  private AlertNotifyRecordService alertNotifyRecordService;

  @GetMapping("/queryByHistoryDetailId/{historyDetailId}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlertNotifyRecordDTO> queryByHistoryDetailId(
      @PathVariable("historyDetailId") Long historyDetailId) {
    final JsonResult<AlertNotifyRecordDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(historyDetailId, "historyDetailId");
      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        AlertNotifyRecordDTO save =
            alertNotifyRecordService.queryByHistoryDetailId(historyDetailId, tenant(), workspace());
        JsonResult.createSuccessResult(result, save);
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlertNotifyRecordDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlertNotifyRecordDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlertNotifyRecordDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(pageRequest.getTarget(), "target");
      }

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
        JsonResult.createSuccessResult(result, alertNotifyRecordService.getListByPage(pageRequest));
      }
    });

    return result;
  }
}

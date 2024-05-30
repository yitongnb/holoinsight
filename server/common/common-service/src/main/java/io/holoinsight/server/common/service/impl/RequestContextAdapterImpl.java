/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.service.RequestContextAdapter;
import io.holoinsight.server.common.scope.MonitorScope;
import io.holoinsight.server.common.RequestContext;
import io.holoinsight.server.query.grpc.QueryProto;
import org.apache.commons.lang3.StringUtils;

/**
 * @author masaimu
 * @version 2023-06-09 17:31:00
 */
public class RequestContextAdapterImpl implements RequestContextAdapter {
  @Override
  public QueryProto.QueryRequest requestAdapte(QueryProto.QueryRequest request) {
    return request;
  }

  @Override
  public QueryProto.PqlRangeRequest requestAdapte(QueryProto.PqlRangeRequest request) {
    return request;
  }

  @Override
  public <T> void queryWrapperTenantAdapt(QueryWrapper<T> queryWrapper, String tenant,
      String workspace) {
    if (queryWrapper != null) {
      if (StringUtils.isNotBlank(tenant)) {
        queryWrapper.eq("tenant", tenant);
      }
      if (StringUtils.isNotBlank(workspace)) {
        queryWrapper.eq("workspace", workspace);
      }
    }
  }

  @Override
  public <T> void queryWrapperTenantAdapt(QueryWrapper<T> queryWrapper, String tenant) {
    if (queryWrapper != null && StringUtils.isNotBlank(tenant)) {
      queryWrapper.eq("tenant", tenant);
    }
  }

  @Override
  public <T> void queryWrapperWorkspaceAdapt(QueryWrapper<T> queryWrapper, String workspace) {
    if (queryWrapper != null && StringUtils.isNotBlank(workspace)) {
      queryWrapper.eq("workspace", workspace);
    }
  }

  @Override
  public String getWorkspace(boolean cross) {
    MonitorScope ms = RequestContext.getContext().ms;
    return ms.getWorkspace();
  }

  @Override
  public String getLoginName() {
    return StringUtils.EMPTY;
  }

  @Override
  public String getTenantFromContext(RequestContext.Context context) {
    return context.ms.tenant;
  }

  @Override
  public String getWorkspaceFromContext(RequestContext.Context context) {
    return context.ms.workspace;
  }

  @Override
  public String getSimpleWorkspaceFromContext(RequestContext.Context context) {
    return getWorkspaceFromContext(context);
  }
}

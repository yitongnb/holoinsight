/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.dao.converter.AlarmDingDingRobotConverter;
import io.holoinsight.server.common.dao.mapper.AlarmDingDingRobotMapper;
import io.holoinsight.server.common.dao.entity.AlarmDingDingRobot;
import io.holoinsight.server.common.dao.entity.dto.AlarmDingDingRobotDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.common.service.AlertDingDingRobotService;
import io.holoinsight.server.common.service.RequestContextAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author wangsiyuan
 * @date 2022/4/1 10:44 上午
 */
@Service
public class AlertDingDingRobotServiceImpl extends
    ServiceImpl<AlarmDingDingRobotMapper, AlarmDingDingRobot> implements AlertDingDingRobotService {

  @Resource
  private AlarmDingDingRobotConverter alarmDingDingRobotConverter;

  @Autowired
  private RequestContextAdapter requestContextAdapter;

  @Override
  public Long save(AlarmDingDingRobotDTO alarmDingDingRobotDTO) {
    AlarmDingDingRobot alarmDingDingRobot =
        alarmDingDingRobotConverter.dtoToDO(alarmDingDingRobotDTO);
    this.save(alarmDingDingRobot);
    return alarmDingDingRobot.getId();
  }

  @Override
  public Boolean updateById(AlarmDingDingRobotDTO alarmDingDingRobotDTO) {
    AlarmDingDingRobot alarmDingDingRobot =
        alarmDingDingRobotConverter.dtoToDO(alarmDingDingRobotDTO);
    return this.updateById(alarmDingDingRobot);
  }

  @Override
  public AlarmDingDingRobotDTO queryById(Long id, String tenant) {
    QueryWrapper<AlarmDingDingRobot> wrapper = new QueryWrapper<>();
    requestContextAdapter.queryWrapperTenantAdapt(wrapper, tenant,
        requestContextAdapter.getWorkspace(true));
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    AlarmDingDingRobot alarmDingDingRobot = this.getOne(wrapper);
    return alarmDingDingRobotConverter.doToDTO(alarmDingDingRobot);
  }

  @Override
  public MonitorPageResult<AlarmDingDingRobotDTO> getListByPage(
      MonitorPageRequest<AlarmDingDingRobotDTO> pageRequest) {
    if (pageRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<AlarmDingDingRobot> wrapper = new QueryWrapper<>();

    AlarmDingDingRobot alarmDingDingRobot =
        alarmDingDingRobotConverter.dtoToDO(pageRequest.getTarget());

    requestContextAdapter.queryWrapperTenantAdapt(wrapper, alarmDingDingRobot.getTenant(),
        alarmDingDingRobot.getWorkspace());

    if (null != alarmDingDingRobot.getId()) {
      wrapper.eq("id", alarmDingDingRobot.getId());
    }

    if (StringUtils.isNotBlank(alarmDingDingRobot.getGroupName())) {
      wrapper.like("group_name", alarmDingDingRobot.getGroupName().trim());
    }

    wrapper.orderByDesc("id");

    Page<AlarmDingDingRobot> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<AlarmDingDingRobotDTO> alarmDingDingRobots = new MonitorPageResult<>();

    List<AlarmDingDingRobotDTO> alarmDingDingRobotList =
        alarmDingDingRobotConverter.dosToDTOs(page.getRecords());

    alarmDingDingRobots.setItems(alarmDingDingRobotList);
    alarmDingDingRobots.setPageNum(pageRequest.getPageNum());
    alarmDingDingRobots.setPageSize(pageRequest.getPageSize());
    alarmDingDingRobots.setTotalCount(page.getTotal());
    alarmDingDingRobots.setTotalPage(page.getPages());

    return alarmDingDingRobots;
  }
}

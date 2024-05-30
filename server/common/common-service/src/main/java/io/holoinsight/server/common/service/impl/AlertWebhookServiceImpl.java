/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.dao.converter.AlarmWebhookConverter;
import io.holoinsight.server.common.dao.mapper.AlarmWebhookMapper;
import io.holoinsight.server.common.dao.entity.AlarmWebhook;
import io.holoinsight.server.common.dao.entity.dto.AlarmWebhookDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.common.service.AlertWebhookService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/1 10:44 上午
 */
@Service
public class AlertWebhookServiceImpl extends ServiceImpl<AlarmWebhookMapper, AlarmWebhook>
    implements AlertWebhookService {

  @Resource
  private AlarmWebhookConverter alarmWebhookConverter;

  @Override
  public AlarmWebhookDTO save(AlarmWebhookDTO alarmWebhookDTO) {
    AlarmWebhook alarmWebhook = alarmWebhookConverter.dtoToDO(alarmWebhookDTO);
    this.save(alarmWebhook);
    return alarmWebhookConverter.doToDTO(alarmWebhook);
  }

  @Override
  public Boolean updateById(AlarmWebhookDTO alarmWebhookDTO) {
    AlarmWebhook alarmWebhook = alarmWebhookConverter.dtoToDO(alarmWebhookDTO);
    return this.updateById(alarmWebhook);
  }

  @Override
  public AlarmWebhookDTO queryById(Long id, String tenant) {
    QueryWrapper<AlarmWebhook> wrapper = new QueryWrapper<>();
    wrapper.eq("tenant", tenant);
    wrapper.eq("id", id);
    wrapper.last("LIMIT 1");
    AlarmWebhook alarmWebhook = this.getOne(wrapper);

    return alarmWebhookConverter.doToDTO(alarmWebhook);
  }

  @Override
  public MonitorPageResult<AlarmWebhookDTO> getListByPage(
      MonitorPageRequest<AlarmWebhookDTO> pageRequest) {
    if (pageRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<AlarmWebhook> wrapper = new QueryWrapper<>();

    AlarmWebhook alarmWebhook = alarmWebhookConverter.dtoToDO(pageRequest.getTarget());

    if (null != alarmWebhook.getId()) {
      wrapper.eq("id", alarmWebhook.getId());
    }

    if (StringUtils.isNotBlank(alarmWebhook.getTenant())) {
      wrapper.eq("tenant", alarmWebhook.getTenant().trim());
    }

    if (null != alarmWebhook.getWebhookName()) {
      wrapper.like("webhook_name", alarmWebhook.getWebhookName());
    }

    wrapper.orderByDesc("id");

    Page<AlarmWebhook> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<AlarmWebhookDTO> alarmWebhooks = new MonitorPageResult<>();

    List<AlarmWebhookDTO> alarmWebhookList = alarmWebhookConverter.dosToDTOs(page.getRecords());

    alarmWebhooks.setItems(alarmWebhookList);
    alarmWebhooks.setPageNum(pageRequest.getPageNum());
    alarmWebhooks.setPageSize(pageRequest.getPageSize());
    alarmWebhooks.setTotalCount(page.getTotal());
    alarmWebhooks.setTotalPage(page.getPages());

    return alarmWebhooks;
  }

  @Override
  public List<AlarmWebhookDTO> getListByKeyword(String keyword, String tenant) {
    QueryWrapper<AlarmWebhook> wrapper = new QueryWrapper<>();
    if (StringUtils.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    wrapper.like("id", keyword).or().like("rule_name", keyword);
    Page<AlarmWebhook> page = new Page<>(1, 20);
    page = page(page, wrapper);

    return alarmWebhookConverter.dosToDTOs(page.getRecords());
  }
}

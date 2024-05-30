/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.dao.converter.AlarmSubscribeConverter;
import io.holoinsight.server.common.dao.entity.AlarmSubscribe;
import io.holoinsight.server.common.dao.entity.dto.AlarmSubscribeDTO;
import io.holoinsight.server.common.dao.entity.dto.AlarmSubscribeInfo;
import io.holoinsight.server.common.dao.mapper.AlarmSubscribeMapper;
import io.holoinsight.server.common.service.AlertSubscribeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author wangsiyuan
 * @date 2022/4/1 10:44 上午
 */
@Service
public class AlertSubscribeServiceImpl extends ServiceImpl<AlarmSubscribeMapper, AlarmSubscribe>
    implements AlertSubscribeService {

  @Autowired
  private AlarmSubscribeConverter alarmSubscribeConverter;


  public Boolean saveDataBatch(AlarmSubscribeDTO alarmSubscribeDTO, String creator, String tenant,
      String workspace) {

    Map<String, Object> conditions = new HashMap<>();
    conditions.put("unique_id", alarmSubscribeDTO.getUniqueId());
    conditions.put("tenant", tenant);
    if (StringUtils.isNotBlank(workspace)) {
      conditions.put("workspace", workspace);
    }

    List<AlarmSubscribe> alarmSubscribes = listByMap(conditions);
    List<Long> ids =
        alarmSubscribes.stream().map(AlarmSubscribe::getId).collect(Collectors.toList());
    List<AlarmSubscribeInfo> alarmSubscribeInfo = alarmSubscribeDTO.getAlarmSubscribe();
    List<Long> updateIds =
        alarmSubscribeInfo.stream().map(AlarmSubscribeInfo::getId).collect(Collectors.toList());
    ids.removeAll(updateIds);
    // 删除
    if (!CollectionUtils.isEmpty(ids)) {
      ids.forEach(id -> this.baseMapper.deleteById(id));
    }

    List<AlarmSubscribe> alarmSubscribeList = new ArrayList<>();
    alarmSubscribeDTO.getAlarmSubscribe().forEach(e -> {
      AlarmSubscribe alarmSubscribe = alarmSubscribeConverter.dtoToDO(e);
      if (e.getId() == null) {
        alarmSubscribe.setGmtCreate(new Date());
        alarmSubscribe.setCreator(creator);
      } else {
        alarmSubscribe.setGmtModified(new Date());
        alarmSubscribe.setModifier(creator);
      }
      alarmSubscribe.setUniqueId(alarmSubscribeDTO.getUniqueId());
      alarmSubscribe.setTenant(tenant);
      if (StringUtils.isNotBlank(workspace)) {
        alarmSubscribe.setWorkspace(workspace);
      }
      alarmSubscribe.setStatus((byte) 1);
      alarmSubscribe.setEnvType(alarmSubscribeDTO.getEnvType());
      alarmSubscribeList.add(alarmSubscribe);
    });
    if (alarmSubscribeList.isEmpty()) {
      return true;
    }
    return this.saveOrUpdateBatch(alarmSubscribeList);
  }

  @Override
  public AlarmSubscribeDTO queryByUniqueId(QueryWrapper<AlarmSubscribe> queryWrapper,
      String uniqueId) {
    AlarmSubscribeDTO alarmSubscribeDTO = new AlarmSubscribeDTO();
    alarmSubscribeDTO.setUniqueId(uniqueId);
    List<AlarmSubscribe> list = this.baseMapper.selectList(queryWrapper);
    if (!CollectionUtils.isEmpty(list)) {
      alarmSubscribeDTO.setEnvType(list.get(0).getEnvType());
    }
    alarmSubscribeDTO.setAlarmSubscribe(alarmSubscribeConverter.dosToDTOs(list));
    return alarmSubscribeDTO;
  }

  @Override
  public List<AlarmSubscribeInfo> queryByMap(QueryWrapper<AlarmSubscribe> queryWrapper) {
    List<AlarmSubscribe> list = this.baseMapper.selectList(queryWrapper);
    return alarmSubscribeConverter.dosToDTOs(list);
  }

  @Override
  public Long save(AlarmSubscribeInfo alarmSubscribeInfo) {
    AlarmSubscribe alarmSubscribe = alarmSubscribeConverter.dtoToDO(alarmSubscribeInfo);
    this.save(alarmSubscribe);
    return alarmSubscribe.getId();
  }

  @Override
  public Boolean updateById(AlarmSubscribeInfo alarmSubscribeInfo) {
    AlarmSubscribe alarmSubscribe = alarmSubscribeConverter.dtoToDO(alarmSubscribeInfo);
    return this.updateById(alarmSubscribe);
  }
}

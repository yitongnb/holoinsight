/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.EventBusHolder;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.MonitorPageResult;
import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.dal.converter.IntegrationProductConverter;
import io.holoinsight.server.home.dal.mapper.IntegrationProductMapper;
import io.holoinsight.server.home.dal.model.IntegrationProduct;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xiangwanpeng
 * @version 1.0: IntegrationProductServiceImpl.java, v 0.1 2022年06月08日 7:32 下午 xiangwanpeng Exp $
 */
@Service
public class IntegrationProductServiceImpl extends
    ServiceImpl<IntegrationProductMapper, IntegrationProduct> implements IntegrationProductService {

  @Autowired
  private IntegrationProductConverter IntegrationProductConverter;

  @Override
  public IntegrationProductDTO findById(Long id) {
    IntegrationProduct model = getById(id);
    if (model == null) {
      return null;
    }
    return IntegrationProductConverter.doToDTO(model);
  }

  @Override
  public IntegrationProductDTO findByName(String name) {
    QueryWrapper<IntegrationProduct> wrapper = new QueryWrapper<>();
    wrapper.eq("name", name);
    wrapper.last("LIMIT 1");

    IntegrationProduct model = this.getOne(wrapper);
    if (model == null) {
      return null;
    }
    return IntegrationProductConverter.doToDTO(model);
  }

  @Override
  public List<IntegrationProductDTO> findByMap(Map<String, Object> columnMap) {
    List<IntegrationProduct> models = listByMap(columnMap);

    return IntegrationProductConverter.dosToDTOs(models);
  }

  @Override
  public IntegrationProductDTO create(IntegrationProductDTO IntegrationProductDTO) {
    IntegrationProductDTO.setGmtCreate(new Date());
    IntegrationProductDTO.setGmtModified(new Date());
    IntegrationProduct model = IntegrationProductConverter.dtoToDO(IntegrationProductDTO);
    save(model);
    IntegrationProductDTO customPluginDTOId = IntegrationProductConverter.doToDTO(model);
    EventBusHolder.post(customPluginDTOId);
    return customPluginDTOId;
  }

  @Override
  public void deleteById(Long id) {
    IntegrationProduct IntegrationProduct = getById(id);
    if (null == IntegrationProduct) {
      return;
    }
    removeById(id);
    EventBusHolder.post(IntegrationProduct);
  }

  @Override
  public IntegrationProductDTO updateByRequest(IntegrationProductDTO IntegrationProductDTO) {
    IntegrationProductDTO.setGmtModified(new Date());
    IntegrationProduct model = IntegrationProductConverter.dtoToDO(IntegrationProductDTO);
    saveOrUpdate(model);
    IntegrationProductDTO save = IntegrationProductConverter.doToDTO(model);
    EventBusHolder.post(save);
    return save;
  }

  @Override
  public MonitorPageResult<IntegrationProductDTO> getListByPage(
      MonitorPageRequest<IntegrationProductDTO> pageRequest) {
    if (pageRequest.getTarget() == null) {
      return null;
    }

    QueryWrapper<IntegrationProduct> wrapper = new QueryWrapper<>();

    IntegrationProductDTO integrationProductDTO = pageRequest.getTarget();

    if (null != integrationProductDTO.getGmtCreate()) {
      wrapper.ge("gmt_create", integrationProductDTO.getGmtCreate());
    }
    if (null != integrationProductDTO.getGmtModified()) {
      wrapper.le("gmt_modified", integrationProductDTO.getGmtCreate());
    }

    if (StringUtils.isNotBlank(integrationProductDTO.getCreator())) {
      wrapper.eq("creator", integrationProductDTO.getCreator().trim());
    }

    if (StringUtils.isNotBlank(integrationProductDTO.getModifier())) {
      wrapper.eq("modifier", integrationProductDTO.getModifier().trim());
    }

    if (null != integrationProductDTO.getId()) {
      wrapper.eq("id", integrationProductDTO.getId());
    }

    if (StringUtils.isNotBlank(integrationProductDTO.getName())) {
      wrapper.like("name", integrationProductDTO.getName().trim());
    }

    wrapper.orderByDesc("gmt_modified");

    if (null != integrationProductDTO.getStatus()) {
      wrapper.eq("status", integrationProductDTO.getStatus());
    }
    wrapper.select(IntegrationProduct.class,
        info -> !info.getColumn().equals("creator") && !info.getColumn().equals("modifier"));

    Page<IntegrationProduct> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

    page = page(page, wrapper);

    MonitorPageResult<IntegrationProductDTO> customPluginDTOs = new MonitorPageResult<>();

    customPluginDTOs.setItems(IntegrationProductConverter.dosToDTOs(page.getRecords()));
    customPluginDTOs.setPageNum(pageRequest.getPageNum());
    customPluginDTOs.setPageSize(pageRequest.getPageSize());
    customPluginDTOs.setTotalCount(page.getTotal());
    customPluginDTOs.setTotalPage(page.getPages());

    return customPluginDTOs;
  }

  @Override
  public List<IntegrationProductDTO> getListByKeyword(String keyword, String tenant) {
    QueryWrapper<IntegrationProduct> wrapper = new QueryWrapper<>();
    if (StringUtils.isNotBlank(tenant)) {
      wrapper.eq("tenant", tenant);
    }
    wrapper.eq("status", 1);
    wrapper.like("id", keyword).or().like("name", keyword);
    Page<IntegrationProduct> page = new Page<>(1, 20);
    page = page(page, wrapper);

    return IntegrationProductConverter.dosToDTOs(page.getRecords());
  }

  @Override
  public List<IntegrationProductDTO> getListByNameLike(String name, String tenant) {
    QueryWrapper<IntegrationProduct> wrapper = new QueryWrapper<>();
    wrapper.select().like("name", name);
    wrapper.eq("status", 1);
    List<IntegrationProduct> customPlugins = baseMapper.selectList(wrapper);
    return IntegrationProductConverter.dosToDTOs(customPlugins);
  }

  @Override
  public List<IntegrationProductDTO> queryByRows() {
    QueryWrapper<IntegrationProduct> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("status", 1);
    queryWrapper.select("id", "name", "type", "version", "form", "configuration");
    return IntegrationProductConverter.dosToDTOs(baseMapper.selectList(queryWrapper));
  }

  @Override
  public List<IntegrationProductDTO> queryNames() {
    QueryWrapper<IntegrationProduct> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("status", 1);
    queryWrapper.select("name", "profile");
    return IntegrationProductConverter.dosToDTOs(baseMapper.selectList(queryWrapper));
  }
}

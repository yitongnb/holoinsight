/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author jsy1001de
 * @version 1.0: AggTaskV1DTO.java, Date: 2023-12-06 Time: 15:34
 */
@Data
public class AggTaskV1DTO {
  /**
   * id
   */
  private Long id;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  private String aggId;

  private Long version;

  private String json;

  public Boolean deleted;
  public String refId;
}

/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: gateway-for-agent.proto

package io.holoinsight.server.gateway.grpc;

public interface PointOrBuilder extends
    // @@protoc_insertion_point(interface_extends:io.holoinsight.server.gateway.grpc.Point)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string metric_name = 1;</code>
   * 
   * @return The metricName.
   */
  java.lang.String getMetricName();

  /**
   * <code>string metric_name = 1;</code>
   * 
   * @return The bytes for metricName.
   */
  com.google.protobuf.ByteString getMetricNameBytes();

  /**
   * <pre>
   * 秒级时间戳
   * </pre>
   *
   * <code>int64 timestamp = 2;</code>
   * 
   * @return The timestamp.
   */
  long getTimestamp();

  /**
   * <code>map&lt;string, string&gt; tags = 3;</code>
   */
  int getTagsCount();

  /**
   * <code>map&lt;string, string&gt; tags = 3;</code>
   */
  boolean containsTags(java.lang.String key);

  /**
   * Use {@link #getTagsMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String> getTags();

  /**
   * <code>map&lt;string, string&gt; tags = 3;</code>
   */
  java.util.Map<java.lang.String, java.lang.String> getTagsMap();

  /**
   * <code>map&lt;string, string&gt; tags = 3;</code>
   */

  java.lang.String getTagsOrDefault(java.lang.String key, java.lang.String defaultValue);

  /**
   * <code>map&lt;string, string&gt; tags = 3;</code>
   */

  java.lang.String getTagsOrThrow(java.lang.String key);

  /**
   * <pre>
   * ceresdb 支持2种values: float/string
   * 别用Any/OneOf, 那样效率太低, 直接分成两个, 并且规定一旦同名的话 number 优先级 &gt; string
   * </pre>
   *
   * <code>map&lt;string, double&gt; number_values = 4;</code>
   */
  int getNumberValuesCount();

  /**
   * <pre>
   * ceresdb 支持2种values: float/string
   * 别用Any/OneOf, 那样效率太低, 直接分成两个, 并且规定一旦同名的话 number 优先级 &gt; string
   * </pre>
   *
   * <code>map&lt;string, double&gt; number_values = 4;</code>
   */
  boolean containsNumberValues(java.lang.String key);

  /**
   * Use {@link #getNumberValuesMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.Double> getNumberValues();

  /**
   * <pre>
   * ceresdb 支持2种values: float/string
   * 别用Any/OneOf, 那样效率太低, 直接分成两个, 并且规定一旦同名的话 number 优先级 &gt; string
   * </pre>
   *
   * <code>map&lt;string, double&gt; number_values = 4;</code>
   */
  java.util.Map<java.lang.String, java.lang.Double> getNumberValuesMap();

  /**
   * <pre>
   * ceresdb 支持2种values: float/string
   * 别用Any/OneOf, 那样效率太低, 直接分成两个, 并且规定一旦同名的话 number 优先级 &gt; string
   * </pre>
   *
   * <code>map&lt;string, double&gt; number_values = 4;</code>
   */

  double getNumberValuesOrDefault(java.lang.String key, double defaultValue);

  /**
   * <pre>
   * ceresdb 支持2种values: float/string
   * 别用Any/OneOf, 那样效率太低, 直接分成两个, 并且规定一旦同名的话 number 优先级 &gt; string
   * </pre>
   *
   * <code>map&lt;string, double&gt; number_values = 4;</code>
   */

  double getNumberValuesOrThrow(java.lang.String key);

  /**
   * <code>map&lt;string, string&gt; string_values = 5;</code>
   */
  int getStringValuesCount();

  /**
   * <code>map&lt;string, string&gt; string_values = 5;</code>
   */
  boolean containsStringValues(java.lang.String key);

  /**
   * Use {@link #getStringValuesMap()} instead.
   */
  @java.lang.Deprecated
  java.util.Map<java.lang.String, java.lang.String> getStringValues();

  /**
   * <code>map&lt;string, string&gt; string_values = 5;</code>
   */
  java.util.Map<java.lang.String, java.lang.String> getStringValuesMap();

  /**
   * <code>map&lt;string, string&gt; string_values = 5;</code>
   */

  java.lang.String getStringValuesOrDefault(java.lang.String key, java.lang.String defaultValue);

  /**
   * <code>map&lt;string, string&gt; string_values = 5;</code>
   */

  java.lang.String getStringValuesOrThrow(java.lang.String key);
}

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/ai/generativelanguage/v1beta2/safety.proto

package com.google.ai.generativelanguage.v1beta2;

public interface ContentFilterOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.ai.generativelanguage.v1beta2.ContentFilter)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The reason content was blocked during request processing.
   * </pre>
   *
   * <code>.google.ai.generativelanguage.v1beta2.ContentFilter.BlockedReason reason = 1;</code>
   * @return The enum numeric value on the wire for reason.
   */
  int getReasonValue();
  /**
   * <pre>
   * The reason content was blocked during request processing.
   * </pre>
   *
   * <code>.google.ai.generativelanguage.v1beta2.ContentFilter.BlockedReason reason = 1;</code>
   * @return The reason.
   */
  com.google.ai.generativelanguage.v1beta2.ContentFilter.BlockedReason getReason();

  /**
   * <pre>
   * A string that describes the filtering behavior in more detail.
   * </pre>
   *
   * <code>optional string message = 2;</code>
   * @return Whether the message field is set.
   */
  boolean hasMessage();
  /**
   * <pre>
   * A string that describes the filtering behavior in more detail.
   * </pre>
   *
   * <code>optional string message = 2;</code>
   * @return The message.
   */
  java.lang.String getMessage();
  /**
   * <pre>
   * A string that describes the filtering behavior in more detail.
   * </pre>
   *
   * <code>optional string message = 2;</code>
   * @return The bytes for message.
   */
  com.google.protobuf.ByteString
      getMessageBytes();
}

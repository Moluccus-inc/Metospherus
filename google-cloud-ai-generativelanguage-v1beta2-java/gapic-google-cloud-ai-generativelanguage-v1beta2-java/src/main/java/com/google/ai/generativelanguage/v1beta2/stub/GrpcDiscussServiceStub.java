/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.generativelanguage.v1beta2.stub;

import com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest;
import com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse;
import com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest;
import com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.grpc.GrpcCallSettings;
import com.google.api.gax.grpc.GrpcStubCallableFactory;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.common.collect.ImmutableMap;
import com.google.longrunning.stub.GrpcOperationsStub;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * gRPC stub implementation for the DiscussService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@BetaApi
@Generated("by gapic-generator-java")
public class GrpcDiscussServiceStub extends DiscussServiceStub {
  private static final MethodDescriptor<GenerateMessageRequest, GenerateMessageResponse>
      generateMessageMethodDescriptor =
          MethodDescriptor.<GenerateMessageRequest, GenerateMessageResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(
                  "google.ai.generativelanguage.v1beta2.DiscussService/GenerateMessage")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(GenerateMessageRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(GenerateMessageResponse.getDefaultInstance()))
              .build();

  private static final MethodDescriptor<CountMessageTokensRequest, CountMessageTokensResponse>
      countMessageTokensMethodDescriptor =
          MethodDescriptor.<CountMessageTokensRequest, CountMessageTokensResponse>newBuilder()
              .setType(MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(
                  "google.ai.generativelanguage.v1beta2.DiscussService/CountMessageTokens")
              .setRequestMarshaller(
                  ProtoUtils.marshaller(CountMessageTokensRequest.getDefaultInstance()))
              .setResponseMarshaller(
                  ProtoUtils.marshaller(CountMessageTokensResponse.getDefaultInstance()))
              .build();

  private final UnaryCallable<GenerateMessageRequest, GenerateMessageResponse>
      generateMessageCallable;
  private final UnaryCallable<CountMessageTokensRequest, CountMessageTokensResponse>
      countMessageTokensCallable;

  private final BackgroundResource backgroundResources;
  private final GrpcOperationsStub operationsStub;
  private final GrpcStubCallableFactory callableFactory;

  public static final GrpcDiscussServiceStub create(DiscussServiceStubSettings settings)
      throws IOException {
    return new GrpcDiscussServiceStub(settings, ClientContext.create(settings));
  }

  public static final GrpcDiscussServiceStub create(ClientContext clientContext)
      throws IOException {
    return new GrpcDiscussServiceStub(
        DiscussServiceStubSettings.newBuilder().build(), clientContext);
  }

  public static final GrpcDiscussServiceStub create(
      ClientContext clientContext, GrpcStubCallableFactory callableFactory) throws IOException {
    return new GrpcDiscussServiceStub(
        DiscussServiceStubSettings.newBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of GrpcDiscussServiceStub, using the given settings. This is protected
   * so that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcDiscussServiceStub(DiscussServiceStubSettings settings, ClientContext clientContext)
      throws IOException {
    this(settings, clientContext, new GrpcDiscussServiceCallableFactory());
  }

  /**
   * Constructs an instance of GrpcDiscussServiceStub, using the given settings. This is protected
   * so that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected GrpcDiscussServiceStub(
      DiscussServiceStubSettings settings,
      ClientContext clientContext,
      GrpcStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;
    this.operationsStub = GrpcOperationsStub.create(clientContext, callableFactory);

    GrpcCallSettings<GenerateMessageRequest, GenerateMessageResponse>
        generateMessageTransportSettings =
            GrpcCallSettings.<GenerateMessageRequest, GenerateMessageResponse>newBuilder()
                .setMethodDescriptor(generateMessageMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      ImmutableMap.Builder<String, String> params = ImmutableMap.builder();
                      params.put("model", String.valueOf(request.getModel()));
                      return params.build();
                    })
                .build();
    GrpcCallSettings<CountMessageTokensRequest, CountMessageTokensResponse>
        countMessageTokensTransportSettings =
            GrpcCallSettings.<CountMessageTokensRequest, CountMessageTokensResponse>newBuilder()
                .setMethodDescriptor(countMessageTokensMethodDescriptor)
                .setParamsExtractor(
                    request -> {
                      ImmutableMap.Builder<String, String> params = ImmutableMap.builder();
                      params.put("model", String.valueOf(request.getModel()));
                      return params.build();
                    })
                .build();

    this.generateMessageCallable =
        callableFactory.createUnaryCallable(
            generateMessageTransportSettings, settings.generateMessageSettings(), clientContext);
    this.countMessageTokensCallable =
        callableFactory.createUnaryCallable(
            countMessageTokensTransportSettings,
            settings.countMessageTokensSettings(),
            clientContext);

    this.backgroundResources =
        new BackgroundResourceAggregation(clientContext.getBackgroundResources());
  }

  public GrpcOperationsStub getOperationsStub() {
    return operationsStub;
  }

  @Override
  public UnaryCallable<GenerateMessageRequest, GenerateMessageResponse> generateMessageCallable() {
    return generateMessageCallable;
  }

  @Override
  public UnaryCallable<CountMessageTokensRequest, CountMessageTokensResponse>
      countMessageTokensCallable() {
    return countMessageTokensCallable;
  }

  @Override
  public final void close() {
    try {
      backgroundResources.close();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to close resource", e);
    }
  }

  @Override
  public void shutdown() {
    backgroundResources.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return backgroundResources.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return backgroundResources.isTerminated();
  }

  @Override
  public void shutdownNow() {
    backgroundResources.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return backgroundResources.awaitTermination(duration, unit);
  }
}

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
import com.google.api.core.InternalApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.core.BackgroundResourceAggregation;
import com.google.api.gax.httpjson.ApiMethodDescriptor;
import com.google.api.gax.httpjson.HttpJsonCallSettings;
import com.google.api.gax.httpjson.HttpJsonStubCallableFactory;
import com.google.api.gax.httpjson.ProtoMessageRequestFormatter;
import com.google.api.gax.httpjson.ProtoMessageResponseParser;
import com.google.api.gax.httpjson.ProtoRestSerializer;
import com.google.api.gax.rpc.ClientContext;
import com.google.api.gax.rpc.UnaryCallable;
import com.google.protobuf.TypeRegistry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * REST stub implementation for the DiscussService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@BetaApi
@Generated("by gapic-generator-java")
public class HttpJsonDiscussServiceStub extends DiscussServiceStub {
  private static final TypeRegistry typeRegistry = TypeRegistry.newBuilder().build();

  private static final ApiMethodDescriptor<GenerateMessageRequest, GenerateMessageResponse>
      generateMessageMethodDescriptor =
          ApiMethodDescriptor.<GenerateMessageRequest, GenerateMessageResponse>newBuilder()
              .setFullMethodName(
                  "google.ai.generativelanguage.v1beta2.DiscussService/GenerateMessage")
              .setHttpMethod("POST")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<GenerateMessageRequest>newBuilder()
                      .setPath(
                          "/v1beta2/{model=models/*}:generateMessage",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<GenerateMessageRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "model", request.getModel());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<GenerateMessageRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putQueryParam(fields, "$alt", "json;enum-encoding=int");
                            return fields;
                          })
                      .setRequestBodyExtractor(
                          request ->
                              ProtoRestSerializer.create()
                                  .toBody("*", request.toBuilder().clearModel().build(), true))
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<GenerateMessageResponse>newBuilder()
                      .setDefaultInstance(GenerateMessageResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private static final ApiMethodDescriptor<CountMessageTokensRequest, CountMessageTokensResponse>
      countMessageTokensMethodDescriptor =
          ApiMethodDescriptor.<CountMessageTokensRequest, CountMessageTokensResponse>newBuilder()
              .setFullMethodName(
                  "google.ai.generativelanguage.v1beta2.DiscussService/CountMessageTokens")
              .setHttpMethod("POST")
              .setType(ApiMethodDescriptor.MethodType.UNARY)
              .setRequestFormatter(
                  ProtoMessageRequestFormatter.<CountMessageTokensRequest>newBuilder()
                      .setPath(
                          "/v1beta2/{model=models/*}:countMessageTokens",
                          request -> {
                            Map<String, String> fields = new HashMap<>();
                            ProtoRestSerializer<CountMessageTokensRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putPathParam(fields, "model", request.getModel());
                            return fields;
                          })
                      .setQueryParamsExtractor(
                          request -> {
                            Map<String, List<String>> fields = new HashMap<>();
                            ProtoRestSerializer<CountMessageTokensRequest> serializer =
                                ProtoRestSerializer.create();
                            serializer.putQueryParam(fields, "$alt", "json;enum-encoding=int");
                            return fields;
                          })
                      .setRequestBodyExtractor(
                          request ->
                              ProtoRestSerializer.create()
                                  .toBody("*", request.toBuilder().clearModel().build(), true))
                      .build())
              .setResponseParser(
                  ProtoMessageResponseParser.<CountMessageTokensResponse>newBuilder()
                      .setDefaultInstance(CountMessageTokensResponse.getDefaultInstance())
                      .setDefaultTypeRegistry(typeRegistry)
                      .build())
              .build();

  private final UnaryCallable<GenerateMessageRequest, GenerateMessageResponse>
      generateMessageCallable;
  private final UnaryCallable<CountMessageTokensRequest, CountMessageTokensResponse>
      countMessageTokensCallable;

  private final BackgroundResource backgroundResources;
  private final HttpJsonStubCallableFactory callableFactory;

  public static final HttpJsonDiscussServiceStub create(DiscussServiceStubSettings settings)
      throws IOException {
    return new HttpJsonDiscussServiceStub(settings, ClientContext.create(settings));
  }

  public static final HttpJsonDiscussServiceStub create(ClientContext clientContext)
      throws IOException {
    return new HttpJsonDiscussServiceStub(
        DiscussServiceStubSettings.newHttpJsonBuilder().build(), clientContext);
  }

  public static final HttpJsonDiscussServiceStub create(
      ClientContext clientContext, HttpJsonStubCallableFactory callableFactory) throws IOException {
    return new HttpJsonDiscussServiceStub(
        DiscussServiceStubSettings.newHttpJsonBuilder().build(), clientContext, callableFactory);
  }

  /**
   * Constructs an instance of HttpJsonDiscussServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected HttpJsonDiscussServiceStub(
      DiscussServiceStubSettings settings, ClientContext clientContext) throws IOException {
    this(settings, clientContext, new HttpJsonDiscussServiceCallableFactory());
  }

  /**
   * Constructs an instance of HttpJsonDiscussServiceStub, using the given settings. This is
   * protected so that it is easy to make a subclass, but otherwise, the static factory methods
   * should be preferred.
   */
  protected HttpJsonDiscussServiceStub(
      DiscussServiceStubSettings settings,
      ClientContext clientContext,
      HttpJsonStubCallableFactory callableFactory)
      throws IOException {
    this.callableFactory = callableFactory;

    HttpJsonCallSettings<GenerateMessageRequest, GenerateMessageResponse>
        generateMessageTransportSettings =
            HttpJsonCallSettings.<GenerateMessageRequest, GenerateMessageResponse>newBuilder()
                .setMethodDescriptor(generateMessageMethodDescriptor)
                .setTypeRegistry(typeRegistry)
                .build();
    HttpJsonCallSettings<CountMessageTokensRequest, CountMessageTokensResponse>
        countMessageTokensTransportSettings =
            HttpJsonCallSettings.<CountMessageTokensRequest, CountMessageTokensResponse>newBuilder()
                .setMethodDescriptor(countMessageTokensMethodDescriptor)
                .setTypeRegistry(typeRegistry)
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

  @InternalApi
  public static List<ApiMethodDescriptor> getMethodDescriptors() {
    List<ApiMethodDescriptor> methodDescriptors = new ArrayList<>();
    methodDescriptors.add(generateMessageMethodDescriptor);
    methodDescriptors.add(countMessageTokensMethodDescriptor);
    return methodDescriptors;
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

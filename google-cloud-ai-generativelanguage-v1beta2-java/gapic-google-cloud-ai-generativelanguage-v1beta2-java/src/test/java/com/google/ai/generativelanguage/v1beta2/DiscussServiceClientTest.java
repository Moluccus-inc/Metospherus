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

package com.google.ai.generativelanguage.v1beta2;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GaxGrpcProperties;
import com.google.api.gax.grpc.testing.LocalChannelProvider;
import com.google.api.gax.grpc.testing.MockGrpcService;
import com.google.api.gax.grpc.testing.MockServiceHelper;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.protobuf.AbstractMessage;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class DiscussServiceClientTest {
  private static MockDiscussService mockDiscussService;
  private static MockServiceHelper mockServiceHelper;
  private LocalChannelProvider channelProvider;
  private DiscussServiceClient client;

  @BeforeClass
  public static void startStaticServer() {
    mockDiscussService = new MockDiscussService();
    mockServiceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(mockDiscussService));
    mockServiceHelper.start();
  }

  @AfterClass
  public static void stopServer() {
    mockServiceHelper.stop();
  }

  @Before
  public void setUp() throws IOException {
    mockServiceHelper.reset();
    channelProvider = mockServiceHelper.createChannelProvider();
    DiscussServiceSettings settings =
        DiscussServiceSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = DiscussServiceClient.create(settings);
  }

  @After
  public void tearDown() throws Exception {
    client.close();
  }

  @Test
  public void generateMessageTest() throws Exception {
    GenerateMessageResponse expectedResponse =
        GenerateMessageResponse.newBuilder()
            .addAllCandidates(new ArrayList<Message>())
            .addAllMessages(new ArrayList<Message>())
            .addAllFilters(new ArrayList<ContentFilter>())
            .build();
    mockDiscussService.addResponse(expectedResponse);

    ModelName model = ModelName.of("[MODEL]");
    MessagePrompt prompt = MessagePrompt.newBuilder().build();
    float temperature = 321701236;
    int candidateCount = 396483411;
    float topP = 110545926;
    int topK = 110545921;

    GenerateMessageResponse actualResponse =
        client.generateMessage(model, prompt, temperature, candidateCount, topP, topK);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockDiscussService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GenerateMessageRequest actualRequest = ((GenerateMessageRequest) actualRequests.get(0));

    Assert.assertEquals(model.toString(), actualRequest.getModel());
    Assert.assertEquals(prompt, actualRequest.getPrompt());
    Assert.assertEquals(temperature, actualRequest.getTemperature(), 0.0001f);
    Assert.assertEquals(candidateCount, actualRequest.getCandidateCount());
    Assert.assertEquals(topP, actualRequest.getTopP(), 0.0001f);
    Assert.assertEquals(topK, actualRequest.getTopK());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void generateMessageExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockDiscussService.addException(exception);

    try {
      ModelName model = ModelName.of("[MODEL]");
      MessagePrompt prompt = MessagePrompt.newBuilder().build();
      float temperature = 321701236;
      int candidateCount = 396483411;
      float topP = 110545926;
      int topK = 110545921;
      client.generateMessage(model, prompt, temperature, candidateCount, topP, topK);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void generateMessageTest2() throws Exception {
    GenerateMessageResponse expectedResponse =
        GenerateMessageResponse.newBuilder()
            .addAllCandidates(new ArrayList<Message>())
            .addAllMessages(new ArrayList<Message>())
            .addAllFilters(new ArrayList<ContentFilter>())
            .build();
    mockDiscussService.addResponse(expectedResponse);

    String model = "model104069929";
    MessagePrompt prompt = MessagePrompt.newBuilder().build();
    float temperature = 321701236;
    int candidateCount = 396483411;
    float topP = 110545926;
    int topK = 110545921;

    GenerateMessageResponse actualResponse =
        client.generateMessage(model, prompt, temperature, candidateCount, topP, topK);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockDiscussService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GenerateMessageRequest actualRequest = ((GenerateMessageRequest) actualRequests.get(0));

    Assert.assertEquals(model, actualRequest.getModel());
    Assert.assertEquals(prompt, actualRequest.getPrompt());
    Assert.assertEquals(temperature, actualRequest.getTemperature(), 0.0001f);
    Assert.assertEquals(candidateCount, actualRequest.getCandidateCount());
    Assert.assertEquals(topP, actualRequest.getTopP(), 0.0001f);
    Assert.assertEquals(topK, actualRequest.getTopK());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void generateMessageExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockDiscussService.addException(exception);

    try {
      String model = "model104069929";
      MessagePrompt prompt = MessagePrompt.newBuilder().build();
      float temperature = 321701236;
      int candidateCount = 396483411;
      float topP = 110545926;
      int topK = 110545921;
      client.generateMessage(model, prompt, temperature, candidateCount, topP, topK);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void countMessageTokensTest() throws Exception {
    CountMessageTokensResponse expectedResponse =
        CountMessageTokensResponse.newBuilder().setTokenCount(-1164226743).build();
    mockDiscussService.addResponse(expectedResponse);

    ModelName model = ModelName.of("[MODEL]");
    MessagePrompt prompt = MessagePrompt.newBuilder().build();

    CountMessageTokensResponse actualResponse = client.countMessageTokens(model, prompt);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockDiscussService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CountMessageTokensRequest actualRequest = ((CountMessageTokensRequest) actualRequests.get(0));

    Assert.assertEquals(model.toString(), actualRequest.getModel());
    Assert.assertEquals(prompt, actualRequest.getPrompt());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void countMessageTokensExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockDiscussService.addException(exception);

    try {
      ModelName model = ModelName.of("[MODEL]");
      MessagePrompt prompt = MessagePrompt.newBuilder().build();
      client.countMessageTokens(model, prompt);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void countMessageTokensTest2() throws Exception {
    CountMessageTokensResponse expectedResponse =
        CountMessageTokensResponse.newBuilder().setTokenCount(-1164226743).build();
    mockDiscussService.addResponse(expectedResponse);

    String model = "model104069929";
    MessagePrompt prompt = MessagePrompt.newBuilder().build();

    CountMessageTokensResponse actualResponse = client.countMessageTokens(model, prompt);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockDiscussService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    CountMessageTokensRequest actualRequest = ((CountMessageTokensRequest) actualRequests.get(0));

    Assert.assertEquals(model, actualRequest.getModel());
    Assert.assertEquals(prompt, actualRequest.getPrompt());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void countMessageTokensExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockDiscussService.addException(exception);

    try {
      String model = "model104069929";
      MessagePrompt prompt = MessagePrompt.newBuilder().build();
      client.countMessageTokens(model, prompt);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}

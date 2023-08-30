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
public class TextServiceClientTest {
  private static MockServiceHelper mockServiceHelper;
  private static MockTextService mockTextService;
  private LocalChannelProvider channelProvider;
  private TextServiceClient client;

  @BeforeClass
  public static void startStaticServer() {
    mockTextService = new MockTextService();
    mockServiceHelper =
        new MockServiceHelper(
            UUID.randomUUID().toString(), Arrays.<MockGrpcService>asList(mockTextService));
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
    TextServiceSettings settings =
        TextServiceSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = TextServiceClient.create(settings);
  }

  @After
  public void tearDown() throws Exception {
    client.close();
  }

  @Test
  public void generateTextTest() throws Exception {
    GenerateTextResponse expectedResponse =
        GenerateTextResponse.newBuilder()
            .addAllCandidates(new ArrayList<TextCompletion>())
            .addAllFilters(new ArrayList<ContentFilter>())
            .addAllSafetyFeedback(new ArrayList<SafetyFeedback>())
            .build();
    mockTextService.addResponse(expectedResponse);

    ModelName model = ModelName.of("[MODEL]");
    TextPrompt prompt = TextPrompt.newBuilder().build();
    float temperature = 321701236;
    int candidateCount = 396483411;
    int maxOutputTokens = 1250078461;
    float topP = 110545926;
    int topK = 110545921;

    GenerateTextResponse actualResponse =
        client.generateText(
            model, prompt, temperature, candidateCount, maxOutputTokens, topP, topK);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockTextService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GenerateTextRequest actualRequest = ((GenerateTextRequest) actualRequests.get(0));

    Assert.assertEquals(model.toString(), actualRequest.getModel());
    Assert.assertEquals(prompt, actualRequest.getPrompt());
    Assert.assertEquals(temperature, actualRequest.getTemperature(), 0.0001f);
    Assert.assertEquals(candidateCount, actualRequest.getCandidateCount());
    Assert.assertEquals(maxOutputTokens, actualRequest.getMaxOutputTokens());
    Assert.assertEquals(topP, actualRequest.getTopP(), 0.0001f);
    Assert.assertEquals(topK, actualRequest.getTopK());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void generateTextExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockTextService.addException(exception);

    try {
      ModelName model = ModelName.of("[MODEL]");
      TextPrompt prompt = TextPrompt.newBuilder().build();
      float temperature = 321701236;
      int candidateCount = 396483411;
      int maxOutputTokens = 1250078461;
      float topP = 110545926;
      int topK = 110545921;
      client.generateText(model, prompt, temperature, candidateCount, maxOutputTokens, topP, topK);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void generateTextTest2() throws Exception {
    GenerateTextResponse expectedResponse =
        GenerateTextResponse.newBuilder()
            .addAllCandidates(new ArrayList<TextCompletion>())
            .addAllFilters(new ArrayList<ContentFilter>())
            .addAllSafetyFeedback(new ArrayList<SafetyFeedback>())
            .build();
    mockTextService.addResponse(expectedResponse);

    String model = "model104069929";
    TextPrompt prompt = TextPrompt.newBuilder().build();
    float temperature = 321701236;
    int candidateCount = 396483411;
    int maxOutputTokens = 1250078461;
    float topP = 110545926;
    int topK = 110545921;

    GenerateTextResponse actualResponse =
        client.generateText(
            model, prompt, temperature, candidateCount, maxOutputTokens, topP, topK);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockTextService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    GenerateTextRequest actualRequest = ((GenerateTextRequest) actualRequests.get(0));

    Assert.assertEquals(model, actualRequest.getModel());
    Assert.assertEquals(prompt, actualRequest.getPrompt());
    Assert.assertEquals(temperature, actualRequest.getTemperature(), 0.0001f);
    Assert.assertEquals(candidateCount, actualRequest.getCandidateCount());
    Assert.assertEquals(maxOutputTokens, actualRequest.getMaxOutputTokens());
    Assert.assertEquals(topP, actualRequest.getTopP(), 0.0001f);
    Assert.assertEquals(topK, actualRequest.getTopK());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void generateTextExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockTextService.addException(exception);

    try {
      String model = "model104069929";
      TextPrompt prompt = TextPrompt.newBuilder().build();
      float temperature = 321701236;
      int candidateCount = 396483411;
      int maxOutputTokens = 1250078461;
      float topP = 110545926;
      int topK = 110545921;
      client.generateText(model, prompt, temperature, candidateCount, maxOutputTokens, topP, topK);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void embedTextTest() throws Exception {
    EmbedTextResponse expectedResponse =
        EmbedTextResponse.newBuilder().setEmbedding(Embedding.newBuilder().build()).build();
    mockTextService.addResponse(expectedResponse);

    ModelName model = ModelName.of("[MODEL]");
    String text = "text3556653";

    EmbedTextResponse actualResponse = client.embedText(model, text);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockTextService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    EmbedTextRequest actualRequest = ((EmbedTextRequest) actualRequests.get(0));

    Assert.assertEquals(model.toString(), actualRequest.getModel());
    Assert.assertEquals(text, actualRequest.getText());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void embedTextExceptionTest() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockTextService.addException(exception);

    try {
      ModelName model = ModelName.of("[MODEL]");
      String text = "text3556653";
      client.embedText(model, text);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }

  @Test
  public void embedTextTest2() throws Exception {
    EmbedTextResponse expectedResponse =
        EmbedTextResponse.newBuilder().setEmbedding(Embedding.newBuilder().build()).build();
    mockTextService.addResponse(expectedResponse);

    String model = "model104069929";
    String text = "text3556653";

    EmbedTextResponse actualResponse = client.embedText(model, text);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<AbstractMessage> actualRequests = mockTextService.getRequests();
    Assert.assertEquals(1, actualRequests.size());
    EmbedTextRequest actualRequest = ((EmbedTextRequest) actualRequests.get(0));

    Assert.assertEquals(model, actualRequest.getModel());
    Assert.assertEquals(text, actualRequest.getText());
    Assert.assertTrue(
        channelProvider.isHeaderSent(
            ApiClientHeaderProvider.getDefaultApiClientHeaderKey(),
            GaxGrpcProperties.getDefaultApiClientHeaderPattern()));
  }

  @Test
  public void embedTextExceptionTest2() throws Exception {
    StatusRuntimeException exception = new StatusRuntimeException(io.grpc.Status.INVALID_ARGUMENT);
    mockTextService.addException(exception);

    try {
      String model = "model104069929";
      String text = "text3556653";
      client.embedText(model, text);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}

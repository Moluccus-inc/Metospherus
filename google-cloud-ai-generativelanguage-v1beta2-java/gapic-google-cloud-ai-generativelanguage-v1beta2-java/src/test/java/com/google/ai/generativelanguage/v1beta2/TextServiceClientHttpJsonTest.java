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

import com.google.ai.generativelanguage.v1beta2.stub.HttpJsonTextServiceStub;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.httpjson.GaxHttpJsonProperties;
import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.api.gax.rpc.ApiClientHeaderProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.ApiExceptionFactory;
import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.testing.FakeStatusCode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class TextServiceClientHttpJsonTest {
  private static MockHttpService mockService;
  private static TextServiceClient client;

  @BeforeClass
  public static void startStaticServer() throws IOException {
    mockService =
        new MockHttpService(
            HttpJsonTextServiceStub.getMethodDescriptors(),
            TextServiceSettings.getDefaultEndpoint());
    TextServiceSettings settings =
        TextServiceSettings.newHttpJsonBuilder()
            .setTransportChannelProvider(
                TextServiceSettings.defaultHttpJsonTransportProviderBuilder()
                    .setHttpTransport(mockService)
                    .build())
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = TextServiceClient.create(settings);
  }

  @AfterClass
  public static void stopServer() {
    client.close();
  }

  @Before
  public void setUp() {}

  @After
  public void tearDown() throws Exception {
    mockService.reset();
  }

  @Test
  public void generateTextTest() throws Exception {
    GenerateTextResponse expectedResponse =
        GenerateTextResponse.newBuilder()
            .addAllCandidates(new ArrayList<TextCompletion>())
            .addAllFilters(new ArrayList<ContentFilter>())
            .addAllSafetyFeedback(new ArrayList<SafetyFeedback>())
            .build();
    mockService.addResponse(expectedResponse);

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

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void generateTextExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String model = "models/model-8306";
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

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void generateTextExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String model = "models/model-8306";
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
    mockService.addResponse(expectedResponse);

    ModelName model = ModelName.of("[MODEL]");
    String text = "text3556653";

    EmbedTextResponse actualResponse = client.embedText(model, text);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void embedTextExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String model = "models/model-8306";
    String text = "text3556653";

    EmbedTextResponse actualResponse = client.embedText(model, text);
    Assert.assertEquals(expectedResponse, actualResponse);

    List<String> actualRequests = mockService.getRequestPaths();
    Assert.assertEquals(1, actualRequests.size());

    String apiClientHeaderKey =
        mockService
            .getRequestHeaders()
            .get(ApiClientHeaderProvider.getDefaultApiClientHeaderKey())
            .iterator()
            .next();
    Assert.assertTrue(
        GaxHttpJsonProperties.getDefaultApiClientHeaderPattern()
            .matcher(apiClientHeaderKey)
            .matches());
  }

  @Test
  public void embedTextExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String model = "models/model-8306";
      String text = "text3556653";
      client.embedText(model, text);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}

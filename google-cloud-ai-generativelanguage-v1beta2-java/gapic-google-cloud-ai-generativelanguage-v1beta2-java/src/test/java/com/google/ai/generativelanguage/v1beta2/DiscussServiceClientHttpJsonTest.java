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

import com.google.ai.generativelanguage.v1beta2.stub.HttpJsonDiscussServiceStub;
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
public class DiscussServiceClientHttpJsonTest {
  private static MockHttpService mockService;
  private static DiscussServiceClient client;

  @BeforeClass
  public static void startStaticServer() throws IOException {
    mockService =
        new MockHttpService(
            HttpJsonDiscussServiceStub.getMethodDescriptors(),
            DiscussServiceSettings.getDefaultEndpoint());
    DiscussServiceSettings settings =
        DiscussServiceSettings.newHttpJsonBuilder()
            .setTransportChannelProvider(
                DiscussServiceSettings.defaultHttpJsonTransportProviderBuilder()
                    .setHttpTransport(mockService)
                    .build())
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = DiscussServiceClient.create(settings);
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
  public void generateMessageTest() throws Exception {
    GenerateMessageResponse expectedResponse =
        GenerateMessageResponse.newBuilder()
            .addAllCandidates(new ArrayList<Message>())
            .addAllMessages(new ArrayList<Message>())
            .addAllFilters(new ArrayList<ContentFilter>())
            .build();
    mockService.addResponse(expectedResponse);

    ModelName model = ModelName.of("[MODEL]");
    MessagePrompt prompt = MessagePrompt.newBuilder().build();
    float temperature = 321701236;
    int candidateCount = 396483411;
    float topP = 110545926;
    int topK = 110545921;

    GenerateMessageResponse actualResponse =
        client.generateMessage(model, prompt, temperature, candidateCount, topP, topK);
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
  public void generateMessageExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String model = "models/model-8306";
    MessagePrompt prompt = MessagePrompt.newBuilder().build();
    float temperature = 321701236;
    int candidateCount = 396483411;
    float topP = 110545926;
    int topK = 110545921;

    GenerateMessageResponse actualResponse =
        client.generateMessage(model, prompt, temperature, candidateCount, topP, topK);
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
  public void generateMessageExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String model = "models/model-8306";
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
    mockService.addResponse(expectedResponse);

    ModelName model = ModelName.of("[MODEL]");
    MessagePrompt prompt = MessagePrompt.newBuilder().build();

    CountMessageTokensResponse actualResponse = client.countMessageTokens(model, prompt);
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
  public void countMessageTokensExceptionTest() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

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
    mockService.addResponse(expectedResponse);

    String model = "models/model-8306";
    MessagePrompt prompt = MessagePrompt.newBuilder().build();

    CountMessageTokensResponse actualResponse = client.countMessageTokens(model, prompt);
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
  public void countMessageTokensExceptionTest2() throws Exception {
    ApiException exception =
        ApiExceptionFactory.createException(
            new Exception(), FakeStatusCode.of(StatusCode.Code.INVALID_ARGUMENT), false);
    mockService.addException(exception);

    try {
      String model = "models/model-8306";
      MessagePrompt prompt = MessagePrompt.newBuilder().build();
      client.countMessageTokens(model, prompt);
      Assert.fail("No exception raised");
    } catch (InvalidArgumentException e) {
      // Expected exception.
    }
  }
}

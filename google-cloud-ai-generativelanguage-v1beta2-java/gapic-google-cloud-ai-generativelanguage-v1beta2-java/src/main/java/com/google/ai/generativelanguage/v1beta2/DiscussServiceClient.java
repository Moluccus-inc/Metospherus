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

import com.google.ai.generativelanguage.v1beta2.stub.DiscussServiceStub;
import com.google.ai.generativelanguage.v1beta2.stub.DiscussServiceStubSettings;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.UnaryCallable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Service Description: An API for using Generative Language Models (GLMs) in dialog applications.
 *
 * <p>Also known as large language models (LLMs), this API provides models that are trained for
 * multi-turn dialog.
 *
 * <p>This class provides the ability to make remote calls to the backing service through method
 * calls that map to API methods. Sample code to get started:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
 *   ModelName model = ModelName.of("[MODEL]");
 *   MessagePrompt prompt = MessagePrompt.newBuilder().build();
 *   float temperature = 321701236;
 *   int candidateCount = 396483411;
 *   float topP = 110545926;
 *   int topK = 110545921;
 *   GenerateMessageResponse response =
 *       discussServiceClient.generateMessage(
 *           model, prompt, temperature, candidateCount, topP, topK);
 * }
 * }</pre>
 *
 * <p>Note: close() needs to be called on the DiscussServiceClient object to clean up resources such
 * as threads. In the example above, try-with-resources is used, which automatically calls close().
 *
 * <p>The surface of this class includes several types of Java methods for each of the API's
 * methods:
 *
 * <ol>
 *   <li>A "flattened" method. With this type of method, the fields of the request type have been
 *       converted into function parameters. It may be the case that not all fields are available as
 *       parameters, and not every API method will have a flattened method entry point.
 *   <li>A "request object" method. This type of method only takes one parameter, a request object,
 *       which must be constructed before the call. Not every API method will have a request object
 *       method.
 *   <li>A "callable" method. This type of method takes no parameters and returns an immutable API
 *       callable object, which can be used to initiate calls to the service.
 * </ol>
 *
 * <p>See the individual methods for example code.
 *
 * <p>Many parameters require resource names to be formatted in a particular way. To assist with
 * these names, this class includes a format method for each type of name, and additionally a parse
 * method to extract the individual identifiers contained within names that are returned.
 *
 * <p>This class can be customized by passing in a custom instance of DiscussServiceSettings to
 * create(). For example:
 *
 * <p>To customize credentials:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * DiscussServiceSettings discussServiceSettings =
 *     DiscussServiceSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * DiscussServiceClient discussServiceClient = DiscussServiceClient.create(discussServiceSettings);
 * }</pre>
 *
 * <p>To customize the endpoint:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * DiscussServiceSettings discussServiceSettings =
 *     DiscussServiceSettings.newBuilder().setEndpoint(myEndpoint).build();
 * DiscussServiceClient discussServiceClient = DiscussServiceClient.create(discussServiceSettings);
 * }</pre>
 *
 * <p>To use REST (HTTP1.1/JSON) transport (instead of gRPC) for sending and receiving requests over
 * the wire:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * DiscussServiceSettings discussServiceSettings =
 *     DiscussServiceSettings.newHttpJsonBuilder().build();
 * DiscussServiceClient discussServiceClient = DiscussServiceClient.create(discussServiceSettings);
 * }</pre>
 *
 * <p>Please refer to the GitHub repository's samples for more quickstart code snippets.
 */
@BetaApi
@Generated("by gapic-generator-java")
public class DiscussServiceClient implements BackgroundResource {
  private final DiscussServiceSettings settings;
  private final DiscussServiceStub stub;

  /** Constructs an instance of DiscussServiceClient with default settings. */
  public static final DiscussServiceClient create() throws IOException {
    return create(DiscussServiceSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of DiscussServiceClient, using the given settings. The channels are
   * created based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final DiscussServiceClient create(DiscussServiceSettings settings)
      throws IOException {
    return new DiscussServiceClient(settings);
  }

  /**
   * Constructs an instance of DiscussServiceClient, using the given stub for making calls. This is
   * for advanced usage - prefer using create(DiscussServiceSettings).
   */
  public static final DiscussServiceClient create(DiscussServiceStub stub) {
    return new DiscussServiceClient(stub);
  }

  /**
   * Constructs an instance of DiscussServiceClient, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected DiscussServiceClient(DiscussServiceSettings settings) throws IOException {
    this.settings = settings;
    this.stub = ((DiscussServiceStubSettings) settings.getStubSettings()).createStub();
  }

  protected DiscussServiceClient(DiscussServiceStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final DiscussServiceSettings getSettings() {
    return settings;
  }

  public DiscussServiceStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates a response from the model given an input `MessagePrompt`.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
   *   ModelName model = ModelName.of("[MODEL]");
   *   MessagePrompt prompt = MessagePrompt.newBuilder().build();
   *   float temperature = 321701236;
   *   int candidateCount = 396483411;
   *   float topP = 110545926;
   *   int topK = 110545921;
   *   GenerateMessageResponse response =
   *       discussServiceClient.generateMessage(
   *           model, prompt, temperature, candidateCount, topP, topK);
   * }
   * }</pre>
   *
   * @param model Required. The name of the model to use.
   *     <p>Format: `name=models/{model}`.
   * @param prompt Required. The structured textual input given to the model as a prompt.
   *     <p>Given a prompt, the model will return what it predicts is the next message in the
   *     discussion.
   * @param temperature Optional. Controls the randomness of the output.
   *     <p>Values can range over `[0.0,1.0]`, inclusive. A value closer to `1.0` will produce
   *     responses that are more varied, while a value closer to `0.0` will typically result in less
   *     surprising responses from the model.
   * @param candidateCount Optional. The number of generated response messages to return.
   *     <p>This value must be between `[1, 8]`, inclusive. If unset, this will default to `1`.
   * @param topP Optional. The maximum cumulative probability of tokens to consider when sampling.
   *     <p>The model uses combined Top-k and nucleus sampling.
   *     <p>Nucleus sampling considers the smallest set of tokens whose probability sum is at least
   *     `top_p`.
   * @param topK Optional. The maximum number of tokens to consider when sampling.
   *     <p>The model uses combined Top-k and nucleus sampling.
   *     <p>Top-k sampling considers the set of `top_k` most probable tokens.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final GenerateMessageResponse generateMessage(
      ModelName model,
      MessagePrompt prompt,
      float temperature,
      int candidateCount,
      float topP,
      int topK) {
    GenerateMessageRequest request =
        GenerateMessageRequest.newBuilder()
            .setModel(model == null ? null : model.toString())
            .setPrompt(prompt)
            .setTemperature(temperature)
            .setCandidateCount(candidateCount)
            .setTopP(topP)
            .setTopK(topK)
            .build();
    return generateMessage(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates a response from the model given an input `MessagePrompt`.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
   *   String model = ModelName.of("[MODEL]").toString();
   *   MessagePrompt prompt = MessagePrompt.newBuilder().build();
   *   float temperature = 321701236;
   *   int candidateCount = 396483411;
   *   float topP = 110545926;
   *   int topK = 110545921;
   *   GenerateMessageResponse response =
   *       discussServiceClient.generateMessage(
   *           model, prompt, temperature, candidateCount, topP, topK);
   * }
   * }</pre>
   *
   * @param model Required. The name of the model to use.
   *     <p>Format: `name=models/{model}`.
   * @param prompt Required. The structured textual input given to the model as a prompt.
   *     <p>Given a prompt, the model will return what it predicts is the next message in the
   *     discussion.
   * @param temperature Optional. Controls the randomness of the output.
   *     <p>Values can range over `[0.0,1.0]`, inclusive. A value closer to `1.0` will produce
   *     responses that are more varied, while a value closer to `0.0` will typically result in less
   *     surprising responses from the model.
   * @param candidateCount Optional. The number of generated response messages to return.
   *     <p>This value must be between `[1, 8]`, inclusive. If unset, this will default to `1`.
   * @param topP Optional. The maximum cumulative probability of tokens to consider when sampling.
   *     <p>The model uses combined Top-k and nucleus sampling.
   *     <p>Nucleus sampling considers the smallest set of tokens whose probability sum is at least
   *     `top_p`.
   * @param topK Optional. The maximum number of tokens to consider when sampling.
   *     <p>The model uses combined Top-k and nucleus sampling.
   *     <p>Top-k sampling considers the set of `top_k` most probable tokens.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final GenerateMessageResponse generateMessage(
      String model,
      MessagePrompt prompt,
      float temperature,
      int candidateCount,
      float topP,
      int topK) {
    GenerateMessageRequest request =
        GenerateMessageRequest.newBuilder()
            .setModel(model)
            .setPrompt(prompt)
            .setTemperature(temperature)
            .setCandidateCount(candidateCount)
            .setTopP(topP)
            .setTopK(topK)
            .build();
    return generateMessage(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates a response from the model given an input `MessagePrompt`.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
   *   GenerateMessageRequest request =
   *       GenerateMessageRequest.newBuilder()
   *           .setModel(ModelName.of("[MODEL]").toString())
   *           .setPrompt(MessagePrompt.newBuilder().build())
   *           .setTemperature(321701236)
   *           .setCandidateCount(396483411)
   *           .setTopP(110545926)
   *           .setTopK(110545921)
   *           .build();
   *   GenerateMessageResponse response = discussServiceClient.generateMessage(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final GenerateMessageResponse generateMessage(GenerateMessageRequest request) {
    return generateMessageCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates a response from the model given an input `MessagePrompt`.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
   *   GenerateMessageRequest request =
   *       GenerateMessageRequest.newBuilder()
   *           .setModel(ModelName.of("[MODEL]").toString())
   *           .setPrompt(MessagePrompt.newBuilder().build())
   *           .setTemperature(321701236)
   *           .setCandidateCount(396483411)
   *           .setTopP(110545926)
   *           .setTopK(110545921)
   *           .build();
   *   ApiFuture<GenerateMessageResponse> future =
   *       discussServiceClient.generateMessageCallable().futureCall(request);
   *   // Do something.
   *   GenerateMessageResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GenerateMessageRequest, GenerateMessageResponse>
      generateMessageCallable() {
    return stub.generateMessageCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Runs a model's tokenizer on a string and returns the token count.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
   *   ModelName model = ModelName.of("[MODEL]");
   *   MessagePrompt prompt = MessagePrompt.newBuilder().build();
   *   CountMessageTokensResponse response = discussServiceClient.countMessageTokens(model, prompt);
   * }
   * }</pre>
   *
   * @param model Required. The model's resource name. This serves as an ID for the Model to use.
   *     <p>This name should match a model name returned by the `ListModels` method.
   *     <p>Format: `models/{model}`
   * @param prompt Required. The prompt, whose token count is to be returned.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final CountMessageTokensResponse countMessageTokens(
      ModelName model, MessagePrompt prompt) {
    CountMessageTokensRequest request =
        CountMessageTokensRequest.newBuilder()
            .setModel(model == null ? null : model.toString())
            .setPrompt(prompt)
            .build();
    return countMessageTokens(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Runs a model's tokenizer on a string and returns the token count.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
   *   String model = ModelName.of("[MODEL]").toString();
   *   MessagePrompt prompt = MessagePrompt.newBuilder().build();
   *   CountMessageTokensResponse response = discussServiceClient.countMessageTokens(model, prompt);
   * }
   * }</pre>
   *
   * @param model Required. The model's resource name. This serves as an ID for the Model to use.
   *     <p>This name should match a model name returned by the `ListModels` method.
   *     <p>Format: `models/{model}`
   * @param prompt Required. The prompt, whose token count is to be returned.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final CountMessageTokensResponse countMessageTokens(String model, MessagePrompt prompt) {
    CountMessageTokensRequest request =
        CountMessageTokensRequest.newBuilder().setModel(model).setPrompt(prompt).build();
    return countMessageTokens(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Runs a model's tokenizer on a string and returns the token count.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
   *   CountMessageTokensRequest request =
   *       CountMessageTokensRequest.newBuilder()
   *           .setModel(ModelName.of("[MODEL]").toString())
   *           .setPrompt(MessagePrompt.newBuilder().build())
   *           .build();
   *   CountMessageTokensResponse response = discussServiceClient.countMessageTokens(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final CountMessageTokensResponse countMessageTokens(CountMessageTokensRequest request) {
    return countMessageTokensCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Runs a model's tokenizer on a string and returns the token count.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
   *   CountMessageTokensRequest request =
   *       CountMessageTokensRequest.newBuilder()
   *           .setModel(ModelName.of("[MODEL]").toString())
   *           .setPrompt(MessagePrompt.newBuilder().build())
   *           .build();
   *   ApiFuture<CountMessageTokensResponse> future =
   *       discussServiceClient.countMessageTokensCallable().futureCall(request);
   *   // Do something.
   *   CountMessageTokensResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<CountMessageTokensRequest, CountMessageTokensResponse>
      countMessageTokensCallable() {
    return stub.countMessageTokensCallable();
  }

  @Override
  public final void close() {
    stub.close();
  }

  @Override
  public void shutdown() {
    stub.shutdown();
  }

  @Override
  public boolean isShutdown() {
    return stub.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return stub.isTerminated();
  }

  @Override
  public void shutdownNow() {
    stub.shutdownNow();
  }

  @Override
  public boolean awaitTermination(long duration, TimeUnit unit) throws InterruptedException {
    return stub.awaitTermination(duration, unit);
  }
}

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

import com.google.ai.generativelanguage.v1beta2.stub.TextServiceStub;
import com.google.ai.generativelanguage.v1beta2.stub.TextServiceStubSettings;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.UnaryCallable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Service Description: API for using Generative Language Models (GLMs) trained to generate text.
 *
 * <p>Also known as Large Language Models (LLM)s, these generate text given an input prompt from the
 * user.
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
 * try (TextServiceClient textServiceClient = TextServiceClient.create()) {
 *   ModelName model = ModelName.of("[MODEL]");
 *   TextPrompt prompt = TextPrompt.newBuilder().build();
 *   float temperature = 321701236;
 *   int candidateCount = 396483411;
 *   int maxOutputTokens = 1250078461;
 *   float topP = 110545926;
 *   int topK = 110545921;
 *   GenerateTextResponse response =
 *       textServiceClient.generateText(
 *           model, prompt, temperature, candidateCount, maxOutputTokens, topP, topK);
 * }
 * }</pre>
 *
 * <p>Note: close() needs to be called on the TextServiceClient object to clean up resources such as
 * threads. In the example above, try-with-resources is used, which automatically calls close().
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
 * <p>This class can be customized by passing in a custom instance of TextServiceSettings to
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
 * TextServiceSettings textServiceSettings =
 *     TextServiceSettings.newBuilder()
 *         .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
 *         .build();
 * TextServiceClient textServiceClient = TextServiceClient.create(textServiceSettings);
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
 * TextServiceSettings textServiceSettings =
 *     TextServiceSettings.newBuilder().setEndpoint(myEndpoint).build();
 * TextServiceClient textServiceClient = TextServiceClient.create(textServiceSettings);
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
 * TextServiceSettings textServiceSettings = TextServiceSettings.newHttpJsonBuilder().build();
 * TextServiceClient textServiceClient = TextServiceClient.create(textServiceSettings);
 * }</pre>
 *
 * <p>Please refer to the GitHub repository's samples for more quickstart code snippets.
 */
@BetaApi
@Generated("by gapic-generator-java")
public class TextServiceClient implements BackgroundResource {
  private final TextServiceSettings settings;
  private final TextServiceStub stub;

  /** Constructs an instance of TextServiceClient with default settings. */
  public static final TextServiceClient create() throws IOException {
    return create(TextServiceSettings.newBuilder().build());
  }

  /**
   * Constructs an instance of TextServiceClient, using the given settings. The channels are created
   * based on the settings passed in, or defaults for any settings that are not set.
   */
  public static final TextServiceClient create(TextServiceSettings settings) throws IOException {
    return new TextServiceClient(settings);
  }

  /**
   * Constructs an instance of TextServiceClient, using the given stub for making calls. This is for
   * advanced usage - prefer using create(TextServiceSettings).
   */
  public static final TextServiceClient create(TextServiceStub stub) {
    return new TextServiceClient(stub);
  }

  /**
   * Constructs an instance of TextServiceClient, using the given settings. This is protected so
   * that it is easy to make a subclass, but otherwise, the static factory methods should be
   * preferred.
   */
  protected TextServiceClient(TextServiceSettings settings) throws IOException {
    this.settings = settings;
    this.stub = ((TextServiceStubSettings) settings.getStubSettings()).createStub();
  }

  protected TextServiceClient(TextServiceStub stub) {
    this.settings = null;
    this.stub = stub;
  }

  public final TextServiceSettings getSettings() {
    return settings;
  }

  public TextServiceStub getStub() {
    return stub;
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates a response from the model given an input message.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (TextServiceClient textServiceClient = TextServiceClient.create()) {
   *   ModelName model = ModelName.of("[MODEL]");
   *   TextPrompt prompt = TextPrompt.newBuilder().build();
   *   float temperature = 321701236;
   *   int candidateCount = 396483411;
   *   int maxOutputTokens = 1250078461;
   *   float topP = 110545926;
   *   int topK = 110545921;
   *   GenerateTextResponse response =
   *       textServiceClient.generateText(
   *           model, prompt, temperature, candidateCount, maxOutputTokens, topP, topK);
   * }
   * }</pre>
   *
   * @param model Required. The model name to use with the format name=models/{model}.
   * @param prompt Required. The free-form input text given to the model as a prompt.
   *     <p>Given a prompt, the model will generate a TextCompletion response it predicts as the
   *     completion of the input text.
   * @param temperature Controls the randomness of the output. Note: The default value varies by
   *     model, see the `Model.temperature` attribute of the `Model` returned the `getModel`
   *     function.
   *     <p>Values can range from [0.0,1.0], inclusive. A value closer to 1.0 will produce responses
   *     that are more varied and creative, while a value closer to 0.0 will typically result in
   *     more straightforward responses from the model.
   * @param candidateCount Number of generated responses to return.
   *     <p>This value must be between [1, 8], inclusive. If unset, this will default to 1.
   * @param maxOutputTokens The maximum number of tokens to include in a candidate.
   *     <p>If unset, this will default to 64.
   * @param topP The maximum cumulative probability of tokens to consider when sampling.
   *     <p>The model uses combined Top-k and nucleus sampling.
   *     <p>Tokens are sorted based on their assigned probabilities so that only the most liekly
   *     tokens are considered. Top-k sampling directly limits the maximum number of tokens to
   *     consider, while Nucleus sampling limits number of tokens based on the cumulative
   *     probability.
   *     <p>Note: The default value varies by model, see the `Model.top_p` attribute of the `Model`
   *     returned the `getModel` function.
   * @param topK The maximum number of tokens to consider when sampling.
   *     <p>The model uses combined Top-k and nucleus sampling.
   *     <p>Top-k sampling considers the set of `top_k` most probable tokens. Defaults to 40.
   *     <p>Note: The default value varies by model, see the `Model.top_k` attribute of the `Model`
   *     returned the `getModel` function.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final GenerateTextResponse generateText(
      ModelName model,
      TextPrompt prompt,
      float temperature,
      int candidateCount,
      int maxOutputTokens,
      float topP,
      int topK) {
    GenerateTextRequest request =
        GenerateTextRequest.newBuilder()
            .setModel(model == null ? null : model.toString())
            .setPrompt(prompt)
            .setTemperature(temperature)
            .setCandidateCount(candidateCount)
            .setMaxOutputTokens(maxOutputTokens)
            .setTopP(topP)
            .setTopK(topK)
            .build();
    return generateText(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates a response from the model given an input message.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (TextServiceClient textServiceClient = TextServiceClient.create()) {
   *   String model = ModelName.of("[MODEL]").toString();
   *   TextPrompt prompt = TextPrompt.newBuilder().build();
   *   float temperature = 321701236;
   *   int candidateCount = 396483411;
   *   int maxOutputTokens = 1250078461;
   *   float topP = 110545926;
   *   int topK = 110545921;
   *   GenerateTextResponse response =
   *       textServiceClient.generateText(
   *           model, prompt, temperature, candidateCount, maxOutputTokens, topP, topK);
   * }
   * }</pre>
   *
   * @param model Required. The model name to use with the format name=models/{model}.
   * @param prompt Required. The free-form input text given to the model as a prompt.
   *     <p>Given a prompt, the model will generate a TextCompletion response it predicts as the
   *     completion of the input text.
   * @param temperature Controls the randomness of the output. Note: The default value varies by
   *     model, see the `Model.temperature` attribute of the `Model` returned the `getModel`
   *     function.
   *     <p>Values can range from [0.0,1.0], inclusive. A value closer to 1.0 will produce responses
   *     that are more varied and creative, while a value closer to 0.0 will typically result in
   *     more straightforward responses from the model.
   * @param candidateCount Number of generated responses to return.
   *     <p>This value must be between [1, 8], inclusive. If unset, this will default to 1.
   * @param maxOutputTokens The maximum number of tokens to include in a candidate.
   *     <p>If unset, this will default to 64.
   * @param topP The maximum cumulative probability of tokens to consider when sampling.
   *     <p>The model uses combined Top-k and nucleus sampling.
   *     <p>Tokens are sorted based on their assigned probabilities so that only the most liekly
   *     tokens are considered. Top-k sampling directly limits the maximum number of tokens to
   *     consider, while Nucleus sampling limits number of tokens based on the cumulative
   *     probability.
   *     <p>Note: The default value varies by model, see the `Model.top_p` attribute of the `Model`
   *     returned the `getModel` function.
   * @param topK The maximum number of tokens to consider when sampling.
   *     <p>The model uses combined Top-k and nucleus sampling.
   *     <p>Top-k sampling considers the set of `top_k` most probable tokens. Defaults to 40.
   *     <p>Note: The default value varies by model, see the `Model.top_k` attribute of the `Model`
   *     returned the `getModel` function.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final GenerateTextResponse generateText(
      String model,
      TextPrompt prompt,
      float temperature,
      int candidateCount,
      int maxOutputTokens,
      float topP,
      int topK) {
    GenerateTextRequest request =
        GenerateTextRequest.newBuilder()
            .setModel(model)
            .setPrompt(prompt)
            .setTemperature(temperature)
            .setCandidateCount(candidateCount)
            .setMaxOutputTokens(maxOutputTokens)
            .setTopP(topP)
            .setTopK(topK)
            .build();
    return generateText(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates a response from the model given an input message.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (TextServiceClient textServiceClient = TextServiceClient.create()) {
   *   GenerateTextRequest request =
   *       GenerateTextRequest.newBuilder()
   *           .setModel(ModelName.of("[MODEL]").toString())
   *           .setPrompt(TextPrompt.newBuilder().build())
   *           .setTemperature(321701236)
   *           .setCandidateCount(396483411)
   *           .setMaxOutputTokens(1250078461)
   *           .setTopP(110545926)
   *           .setTopK(110545921)
   *           .addAllSafetySettings(new ArrayList<SafetySetting>())
   *           .addAllStopSequences(new ArrayList<String>())
   *           .build();
   *   GenerateTextResponse response = textServiceClient.generateText(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final GenerateTextResponse generateText(GenerateTextRequest request) {
    return generateTextCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates a response from the model given an input message.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (TextServiceClient textServiceClient = TextServiceClient.create()) {
   *   GenerateTextRequest request =
   *       GenerateTextRequest.newBuilder()
   *           .setModel(ModelName.of("[MODEL]").toString())
   *           .setPrompt(TextPrompt.newBuilder().build())
   *           .setTemperature(321701236)
   *           .setCandidateCount(396483411)
   *           .setMaxOutputTokens(1250078461)
   *           .setTopP(110545926)
   *           .setTopK(110545921)
   *           .addAllSafetySettings(new ArrayList<SafetySetting>())
   *           .addAllStopSequences(new ArrayList<String>())
   *           .build();
   *   ApiFuture<GenerateTextResponse> future =
   *       textServiceClient.generateTextCallable().futureCall(request);
   *   // Do something.
   *   GenerateTextResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<GenerateTextRequest, GenerateTextResponse> generateTextCallable() {
    return stub.generateTextCallable();
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates an embedding from the model given an input message.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (TextServiceClient textServiceClient = TextServiceClient.create()) {
   *   ModelName model = ModelName.of("[MODEL]");
   *   String text = "text3556653";
   *   EmbedTextResponse response = textServiceClient.embedText(model, text);
   * }
   * }</pre>
   *
   * @param model Required. The model name to use with the format model=models/{model}.
   * @param text Required. The free-form input text that the model will turn into an embedding.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final EmbedTextResponse embedText(ModelName model, String text) {
    EmbedTextRequest request =
        EmbedTextRequest.newBuilder()
            .setModel(model == null ? null : model.toString())
            .setText(text)
            .build();
    return embedText(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates an embedding from the model given an input message.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (TextServiceClient textServiceClient = TextServiceClient.create()) {
   *   String model = ModelName.of("[MODEL]").toString();
   *   String text = "text3556653";
   *   EmbedTextResponse response = textServiceClient.embedText(model, text);
   * }
   * }</pre>
   *
   * @param model Required. The model name to use with the format model=models/{model}.
   * @param text Required. The free-form input text that the model will turn into an embedding.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final EmbedTextResponse embedText(String model, String text) {
    EmbedTextRequest request = EmbedTextRequest.newBuilder().setModel(model).setText(text).build();
    return embedText(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates an embedding from the model given an input message.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (TextServiceClient textServiceClient = TextServiceClient.create()) {
   *   EmbedTextRequest request =
   *       EmbedTextRequest.newBuilder()
   *           .setModel(ModelName.of("[MODEL]").toString())
   *           .setText("text3556653")
   *           .build();
   *   EmbedTextResponse response = textServiceClient.embedText(request);
   * }
   * }</pre>
   *
   * @param request The request object containing all of the parameters for the API call.
   * @throws com.google.api.gax.rpc.ApiException if the remote call fails
   */
  public final EmbedTextResponse embedText(EmbedTextRequest request) {
    return embedTextCallable().call(request);
  }

  // AUTO-GENERATED DOCUMENTATION AND METHOD.
  /**
   * Generates an embedding from the model given an input message.
   *
   * <p>Sample code:
   *
   * <pre>{@code
   * // This snippet has been automatically generated and should be regarded as a code template only.
   * // It will require modifications to work:
   * // - It may require correct/in-range values for request initialization.
   * // - It may require specifying regional endpoints when creating the service client as shown in
   * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
   * try (TextServiceClient textServiceClient = TextServiceClient.create()) {
   *   EmbedTextRequest request =
   *       EmbedTextRequest.newBuilder()
   *           .setModel(ModelName.of("[MODEL]").toString())
   *           .setText("text3556653")
   *           .build();
   *   ApiFuture<EmbedTextResponse> future =
   *       textServiceClient.embedTextCallable().futureCall(request);
   *   // Do something.
   *   EmbedTextResponse response = future.get();
   * }
   * }</pre>
   */
  public final UnaryCallable<EmbedTextRequest, EmbedTextResponse> embedTextCallable() {
    return stub.embedTextCallable();
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

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

/**
 * A client to Generative Language API
 *
 * <p>The interfaces provided are listed below, along with usage samples.
 *
 * <p>======================= DiscussServiceClient =======================
 *
 * <p>Service Description: An API for using Generative Language Models (GLMs) in dialog
 * applications.
 *
 * <p>Also known as large language models (LLMs), this API provides models that are trained for
 * multi-turn dialog.
 *
 * <p>Sample for DiscussServiceClient:
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
 * <p>======================= ModelServiceClient =======================
 *
 * <p>Service Description: Provides methods for getting metadata information about Generative
 * Models.
 *
 * <p>Sample for ModelServiceClient:
 *
 * <pre>{@code
 * // This snippet has been automatically generated and should be regarded as a code template only.
 * // It will require modifications to work:
 * // - It may require correct/in-range values for request initialization.
 * // - It may require specifying regional endpoints when creating the service client as shown in
 * // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
 * try (ModelServiceClient modelServiceClient = ModelServiceClient.create()) {
 *   ModelName name = ModelName.of("[MODEL]");
 *   Model response = modelServiceClient.getModel(name);
 * }
 * }</pre>
 *
 * <p>======================= TextServiceClient =======================
 *
 * <p>Service Description: API for using Generative Language Models (GLMs) trained to generate text.
 *
 * <p>Also known as Large Language Models (LLM)s, these generate text given an input prompt from the
 * user.
 *
 * <p>Sample for TextServiceClient:
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
 */
@Generated("by gapic-generator-java")
package com.google.ai.generativelanguage.v1beta2;

import javax.annotation.Generated;

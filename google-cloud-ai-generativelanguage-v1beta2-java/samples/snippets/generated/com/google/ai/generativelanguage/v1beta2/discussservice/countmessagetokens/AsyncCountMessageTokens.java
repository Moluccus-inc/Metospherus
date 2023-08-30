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

package com.google.ai.generativelanguage.v1beta2.samples;

// [START generativelanguage_v1beta2_generated_DiscussService_CountMessageTokens_async]
import com.google.ai.generativelanguage.v1beta2.CountMessageTokensRequest;
import com.google.ai.generativelanguage.v1beta2.CountMessageTokensResponse;
import com.google.ai.generativelanguage.v1beta2.DiscussServiceClient;
import com.google.ai.generativelanguage.v1beta2.MessagePrompt;
import com.google.ai.generativelanguage.v1beta2.ModelName;
import com.google.api.core.ApiFuture;

public class AsyncCountMessageTokens {

  public static void main(String[] args) throws Exception {
    asyncCountMessageTokens();
  }

  public static void asyncCountMessageTokens() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
      CountMessageTokensRequest request =
          CountMessageTokensRequest.newBuilder()
              .setModel(ModelName.of("[MODEL]").toString())
              .setPrompt(MessagePrompt.newBuilder().build())
              .build();
      ApiFuture<CountMessageTokensResponse> future =
          discussServiceClient.countMessageTokensCallable().futureCall(request);
      // Do something.
      CountMessageTokensResponse response = future.get();
    }
  }
}
// [END generativelanguage_v1beta2_generated_DiscussService_CountMessageTokens_async]

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

// [START generativelanguage_v1beta2_generated_DiscussService_GenerateMessage_sync]
import com.google.ai.generativelanguage.v1beta2.DiscussServiceClient;
import com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest;
import com.google.ai.generativelanguage.v1beta2.GenerateMessageResponse;
import com.google.ai.generativelanguage.v1beta2.MessagePrompt;
import com.google.ai.generativelanguage.v1beta2.ModelName;

public class SyncGenerateMessage {

  public static void main(String[] args) throws Exception {
    syncGenerateMessage();
  }

  public static void syncGenerateMessage() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (DiscussServiceClient discussServiceClient = DiscussServiceClient.create()) {
      GenerateMessageRequest request =
          GenerateMessageRequest.newBuilder()
              .setModel(ModelName.of("[MODEL]").toString())
              .setPrompt(MessagePrompt.newBuilder().build())
              .setTemperature(321701236)
              .setCandidateCount(396483411)
              .setTopP(110545926)
              .setTopK(110545921)
              .build();
      GenerateMessageResponse response = discussServiceClient.generateMessage(request);
    }
  }
}
// [END generativelanguage_v1beta2_generated_DiscussService_GenerateMessage_sync]

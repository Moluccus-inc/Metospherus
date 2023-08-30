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

// [START generativelanguage_v1beta2_generated_TextService_GenerateText_sync]
import com.google.ai.generativelanguage.v1beta2.GenerateTextRequest;
import com.google.ai.generativelanguage.v1beta2.GenerateTextResponse;
import com.google.ai.generativelanguage.v1beta2.ModelName;
import com.google.ai.generativelanguage.v1beta2.SafetySetting;
import com.google.ai.generativelanguage.v1beta2.TextPrompt;
import com.google.ai.generativelanguage.v1beta2.TextServiceClient;
import java.util.ArrayList;

public class SyncGenerateText {

  public static void main(String[] args) throws Exception {
    syncGenerateText();
  }

  public static void syncGenerateText() throws Exception {
    // This snippet has been automatically generated and should be regarded as a code template only.
    // It will require modifications to work:
    // - It may require correct/in-range values for request initialization.
    // - It may require specifying regional endpoints when creating the service client as shown in
    // https://cloud.google.com/java/docs/setup#configure_endpoints_for_the_client_library
    try (TextServiceClient textServiceClient = TextServiceClient.create()) {
      GenerateTextRequest request =
          GenerateTextRequest.newBuilder()
              .setModel(ModelName.of("[MODEL]").toString())
              .setPrompt(TextPrompt.newBuilder().build())
              .setTemperature(321701236)
              .setCandidateCount(396483411)
              .setMaxOutputTokens(1250078461)
              .setTopP(110545926)
              .setTopK(110545921)
              .addAllSafetySettings(new ArrayList<SafetySetting>())
              .addAllStopSequences(new ArrayList<String>())
              .build();
      GenerateTextResponse response = textServiceClient.generateText(request);
    }
  }
}
// [END generativelanguage_v1beta2_generated_TextService_GenerateText_sync]

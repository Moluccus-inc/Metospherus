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

package com.google.ai.generativelanguage.v1beta2.stub;

import com.google.ai.generativelanguage.v1beta2.EmbedTextRequest;
import com.google.ai.generativelanguage.v1beta2.EmbedTextResponse;
import com.google.ai.generativelanguage.v1beta2.GenerateTextRequest;
import com.google.ai.generativelanguage.v1beta2.GenerateTextResponse;
import com.google.api.core.BetaApi;
import com.google.api.gax.core.BackgroundResource;
import com.google.api.gax.rpc.UnaryCallable;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
/**
 * Base stub class for the TextService service API.
 *
 * <p>This class is for advanced usage and reflects the underlying API directly.
 */
@BetaApi
@Generated("by gapic-generator-java")
public abstract class TextServiceStub implements BackgroundResource {

  public UnaryCallable<GenerateTextRequest, GenerateTextResponse> generateTextCallable() {
    throw new UnsupportedOperationException("Not implemented: generateTextCallable()");
  }

  public UnaryCallable<EmbedTextRequest, EmbedTextResponse> embedTextCallable() {
    throw new UnsupportedOperationException("Not implemented: embedTextCallable()");
  }

  @Override
  public abstract void close();
}

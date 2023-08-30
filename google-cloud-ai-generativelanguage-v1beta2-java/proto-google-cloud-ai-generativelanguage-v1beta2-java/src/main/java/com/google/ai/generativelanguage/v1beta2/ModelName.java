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

import com.google.api.pathtemplate.PathTemplate;
import com.google.api.resourcenames.ResourceName;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Generated;

// AUTO-GENERATED DOCUMENTATION AND CLASS.
@Generated("by gapic-generator-java")
public class ModelName implements ResourceName {
  private static final PathTemplate MODEL = PathTemplate.createWithoutUrlEncoding("models/{model}");
  private volatile Map<String, String> fieldValuesMap;
  private final String model;

  @Deprecated
  protected ModelName() {
    model = null;
  }

  private ModelName(Builder builder) {
    model = Preconditions.checkNotNull(builder.getModel());
  }

  public String getModel() {
    return model;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder(this);
  }

  public static ModelName of(String model) {
    return newBuilder().setModel(model).build();
  }

  public static String format(String model) {
    return newBuilder().setModel(model).build().toString();
  }

  public static ModelName parse(String formattedString) {
    if (formattedString.isEmpty()) {
      return null;
    }
    Map<String, String> matchMap =
        MODEL.validatedMatch(
            formattedString, "ModelName.parse: formattedString not in valid format");
    return of(matchMap.get("model"));
  }

  public static List<ModelName> parseList(List<String> formattedStrings) {
    List<ModelName> list = new ArrayList<>(formattedStrings.size());
    for (String formattedString : formattedStrings) {
      list.add(parse(formattedString));
    }
    return list;
  }

  public static List<String> toStringList(List<ModelName> values) {
    List<String> list = new ArrayList<>(values.size());
    for (ModelName value : values) {
      if (value == null) {
        list.add("");
      } else {
        list.add(value.toString());
      }
    }
    return list;
  }

  public static boolean isParsableFrom(String formattedString) {
    return MODEL.matches(formattedString);
  }

  @Override
  public Map<String, String> getFieldValuesMap() {
    if (fieldValuesMap == null) {
      synchronized (this) {
        if (fieldValuesMap == null) {
          ImmutableMap.Builder<String, String> fieldMapBuilder = ImmutableMap.builder();
          if (model != null) {
            fieldMapBuilder.put("model", model);
          }
          fieldValuesMap = fieldMapBuilder.build();
        }
      }
    }
    return fieldValuesMap;
  }

  public String getFieldValue(String fieldName) {
    return getFieldValuesMap().get(fieldName);
  }

  @Override
  public String toString() {
    return MODEL.instantiate("model", model);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o != null || getClass() == o.getClass()) {
      ModelName that = ((ModelName) o);
      return Objects.equals(this.model, that.model);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= Objects.hashCode(model);
    return h;
  }

  /** Builder for models/{model}. */
  public static class Builder {
    private String model;

    protected Builder() {}

    public String getModel() {
      return model;
    }

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    private Builder(ModelName modelName) {
      this.model = modelName.model;
    }

    public ModelName build() {
      return new ModelName(this);
    }
  }
}

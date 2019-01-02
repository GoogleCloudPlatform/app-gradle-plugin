/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.gradle.appengine.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NullSafe {

  public static <S, R> R convert(S source, Function<S, R> converter) {
    return (source == null) ? null : converter.apply(source);
  }

  /**
   * Convert a list of a given type using converter.
   *
   * @param source a list to convert
   * @param converter the map function to apply
   * @param <S> type to convert from
   * @param <R> type to convert to
   * @return A converted List with null values stripped out, or null if source is null
   */
  public static <S, R> List<R> convert(List<S> source, Function<S, R> converter) {
    return (source == null)
        ? null
        : source.stream().filter(Objects::nonNull).map(converter).collect(Collectors.toList());
  }
}
